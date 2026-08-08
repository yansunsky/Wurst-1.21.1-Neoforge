/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.BlockBreakingProgressListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixin.MultiPlayerGameModeAccessor;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.BlockUtils;

@SearchTags({"FastMine", "SpeedMine", "SpeedyGonzales", "fast break",
	"fast mine", "speed mine", "speedy gonzales", "NoBreakDelay",
	"no break delay"})
public final class FastBreakHack extends Hack
	implements UpdateListener, BlockBreakingProgressListener
{
	private final SliderSetting activationChance = new SliderSetting(
		"Activation chance",
		"Only FastBreaks some of the blocks you break with the given chance,"
			+ " which makes it harder for anti-cheat plugins to detect.\n\n"
			+ "This setting does nothing if Legit mode is enabled.",
		1, 0, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	private final CheckboxSetting legitMode = new CheckboxSetting("Legit mode",
		"仅移除破坏方块之间的延迟，而不加快"
			+ "破坏过程本身。\n\n"
			+ "这要慢得多，但非常适合绕过反作弊插件。"
			+ "如果常规 FastBreak 不起作用，并且激活"
			+ "几率滑块也没有帮助，请使用此选项。",
		false);
	
	private final Random random = new Random();
	private BlockPos lastBlockPos;
	private boolean fastBreakBlock;
	
	public FastBreakHack()
	{
		super("FastBreak");
		setCategory(Category.BLOCKS);
		addSetting(activationChance);
		addSetting(legitMode);
	}
	
	@Override
	public String getRenderName()
	{
		if(legitMode.isChecked())
			return getName() + "Legit";
		return getName();
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(BlockBreakingProgressListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(BlockBreakingProgressListener.class, this);
		lastBlockPos = null;
	}
	
	@Override
	public void onUpdate()
	{
		((MultiPlayerGameModeAccessor)(Object)MC.gameMode).wurst_setDestroyDelay(0);
	}
	
	@Override
	public void onBlockBreakingProgress(BlockBreakingProgressEvent event)
	{
		if(legitMode.isChecked())
			return;

		if(((MultiPlayerGameModeAccessor)(Object)MC.gameMode).wurst_getDestroyProgress() >= 1.0F)
			return;
		
		BlockPos blockPos = event.getBlockPos();
		if(!blockPos.equals(lastBlockPos))
		{
			lastBlockPos = blockPos;
			fastBreakBlock = random.nextDouble() <= activationChance.getValue();
		}
		
		// Ignore unbreakable blocks to avoid slowdown issue
		if(BlockUtils.isUnbreakable(blockPos))
			return;
		
		if(!fastBreakBlock)
			return;
		
		Action action = ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK;
		Direction direction = event.getDirection();
		IMC.getInteractionManager().sendPlayerActionC2SPacket(action, blockPos,
			direction);
	}
}
