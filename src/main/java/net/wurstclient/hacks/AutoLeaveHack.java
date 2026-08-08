/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.item.Items;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.InventoryUtils;

@SearchTags({"auto leave", "AutoDisconnect", "auto disconnect", "AutoQuit",
	"auto quit"})
public final class AutoLeaveHack extends Hack implements UpdateListener
{
	private final SliderSetting health = new SliderSetting("Health",
		"当你的生命值达到或低于此值时离开服务器。",
		4, 0.5, 9.5, 0.5, ValueDisplay.DECIMAL.withSuffix(" hearts"));
	
	public final EnumSetting<Mode> mode = new EnumSetting<>("Mode",
		"\u00a7l退出\u00a7r 模式只是正常退出游戏。\n"
			+ "绕过 NoCheat+，但不能绕过 CombatLog。\n\n"
			+ "\u00a7l字符\u00a7r 模式发送一条特殊的聊天消息，"
			+ "使服务器将你踢出。\n"
			+ "绕过 NoCheat+ 和某些版本的 CombatLog。\n\n"
			+ "\u00a7l自伤\u00a7r 模式发送攻击另一个玩家的数据包，"
			+ "但将你自己同时作为攻击者和"
			+ "目标，使服务器将你踢出。\n"
			+ "同时绕过 CombatLog 和 NoCheat+。",
		Mode.values(), Mode.QUIT);
	
	private final CheckboxSetting disableAutoReconnect = new CheckboxSetting(
		"Disable AutoReconnect", "Automatically turns off AutoReconnect when"
			+ " AutoLeave makes you leave the server.",
		true);
	
	private final SliderSetting totems = new SliderSetting("Totems",
		"在你拥有的图腾数量达到"
			+ "此值或低于此值时才会离开。\n\n"
			+ "11 = 始终可以离开",
		11, 0, 11, 1, ValueDisplay.INTEGER.withSuffix(" totems")
			.withLabel(1, "1 totem").withLabel(11, "ignore"));
	
	public AutoLeaveHack()
	{
		super("AutoLeave");
		setCategory(Category.COMBAT);
		addSetting(health);
		addSetting(mode);
		addSetting(disableAutoReconnect);
		addSetting(totems);
	}
	
	@Override
	public String getRenderName()
	{
		if(MC.player != null && MC.player.getAbilities().instabuild)
			return getName() + " (paused)";
		
		return getName() + " [" + mode.getSelected() + "]";
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check gamemode
		if(MC.player.getAbilities().instabuild)
			return;
		
		// check health
		float currentHealth = MC.player.getHealth();
		if(currentHealth <= 0F || currentHealth > health.getValueF() * 2F)
			return;
		
		// check totems
		if(totems.getValueI() < 11 && InventoryUtils
			.count(Items.TOTEM_OF_UNDYING, 40, true) > totems.getValueI())
			return;
		
		// leave server
		mode.getSelected().leave.run();
		
		// disable
		setEnabled(false);
		
		if(disableAutoReconnect.isChecked())
			WURST.getHax().autoReconnectHack.setEnabled(false);
	}
	
	public static enum Mode
	{
		QUIT("Quit", () -> MC.level.disconnect()),
		
		CHARS("Chars", () -> MC.getConnection().sendChat("\u00a7")),
		
		SELFHURT("SelfHurt",
			() -> MC.getConnection().send(ServerboundInteractPacket
				.createAttackPacket(MC.player, MC.player.isShiftKeyDown())));
		
		private final String name;
		private final Runnable leave;
		
		private Mode(String name, Runnable leave)
		{
			this.name = name;
			this.leave = leave;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
