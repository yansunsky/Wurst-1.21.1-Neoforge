// FishingHookAccessor.java
package net.wurstclient.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FishingHook.class)
public interface FishingHookAccessor
{
    @Invoker("calculateOpenWater")
    boolean wurst_calculateOpenWater(BlockPos pos);
}
