// MouseHandlerAccessor.java
package net.wurstclient.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor
{
    @Invoker("onPress")
    void wurst_onPress(long window, int button, int action, int mods);
}
