// accessor

package net.wurstclient.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor
{
    @Accessor("isDestroying")
    void wurst_setIsDestroying(boolean value);

    @Accessor("destroyDelay")
    void wurst_setDestroyDelay(int value);

    @Accessor("destroyProgress")
    float wurst_getDestroyProgress();
}
