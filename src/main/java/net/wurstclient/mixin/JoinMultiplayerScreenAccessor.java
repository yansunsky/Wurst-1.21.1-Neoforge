// JoinMultiplayerScreenAccessor.java
package net.wurstclient.mixin;

import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenAccessor
{
    @Accessor("serverSelectionList")
    public abstract ServerSelectionList wurst_getServerSelectionList();

    @Invoker("join")
    public abstract void wurst_join(ServerData serverData);
}
