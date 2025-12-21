// LocalPlayerAccessor.java
package net.wurstclient.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor
{
    @Accessor("yRotLast")
    float wurst_getYRotLast();

    @Accessor("xRotLast")
    float wurst_getXRotLast();
}
