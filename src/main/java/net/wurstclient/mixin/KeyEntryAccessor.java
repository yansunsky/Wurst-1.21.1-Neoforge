// KeyEntryAccessor.java
package net.wurstclient.mixin;

import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBindsList.KeyEntry.class)
public interface KeyEntryAccessor
{
    @Accessor("name")
    Component wurst_getName();
}
