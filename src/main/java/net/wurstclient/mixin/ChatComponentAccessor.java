// ChatComponentAccessor.java
package net.wurstclient.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor
{
    @Accessor("trimmedMessages")
    List<GuiMessage.Line> wurst_getTrimmedMessages();
}
