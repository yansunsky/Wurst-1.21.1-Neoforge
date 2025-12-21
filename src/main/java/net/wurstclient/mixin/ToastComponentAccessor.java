// ToastComponentAccessor.java
package net.wurstclient.mixin;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(ToastComponent.class)
public interface ToastComponentAccessor
{
    @Accessor("queued")
    Deque<Toast> wurst_getQueued();
}
