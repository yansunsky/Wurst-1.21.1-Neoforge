/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import java.nio.file.Path;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.Util;
import net.minecraft.client.User;
import net.minecraft.server.packs.DownloadQueue;
import net.wurstclient.WurstClient;

/**
 * Patches a fingerprinting vulnerability by creating a separate cache
 * folder for each Minecraft account.
 * <p>
 * This mixin targets {@link DownloadQueue}, which resolves the download
 * cache paths for server resource packs.
 * <p>
 * The {@code entries.forEach()} lambda in the {@code runDownload(BatchConfig, Map)}
 * method resolves cache paths, and we inject our own UUID-based subdirectory.
 *
 * @see <a href="https://github.com/Wurst-Imperium/Wurst7/issues/1226">Issue #1226</a>
 */
@Mixin(DownloadQueue.class)
public abstract class DownloaderMixin implements AutoCloseable
{
	@Shadow
	@Final
	private Path cacheDir;
	
	/**
	 * Creates a per-account cache subdirectory to prevent website
	 * fingerprinting via the resource pack cache.
	 */
	@WrapOperation(at = @At(value = "INVOKE",
		target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;",
		ordinal = 0,
		remap = false), method = "lambda$runDownload$0", remap = false)
	private Path wrapResolve(Path instance, String filename,
		Operation<Path> original)
	{
		Path result = original.call(instance, filename);
		
		// If the path has already been modified by another mod (likely trying
		// to patch the same exploit), don't modify it further.
		if(result == null || !result.getParent().equals(cacheDir))
			return result;
		
		User session = WurstClient.MC.getUser();
		UUID uuid = session.getProfileId();
		if(uuid == null)
			uuid = Util.NIL_UUID;
		
		return result.getParent().resolve(uuid.toString())
			.resolve(result.getFileName());
	}
}
