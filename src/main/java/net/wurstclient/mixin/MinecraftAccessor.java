package net.wurstclient.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor
{
    @Accessor("rightClickDelay")
    int wurst_getRightClickDelay();

    @Accessor("rightClickDelay")
    void wurst_setRightClickDelay(int value);

    @Invoker("startUseItem")
    void wurst_invokeStartUseItem();
}