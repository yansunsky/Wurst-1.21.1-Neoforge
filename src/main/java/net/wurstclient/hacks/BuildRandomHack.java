/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.AABB;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixin.MinecraftAccessor;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.FaceTargetSetting;
import net.wurstclient.settings.FaceTargetSetting.FaceTarget;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.SwingHandSetting;
import net.wurstclient.settings.SwingHandSetting.SwingHand;
import net.wurstclient.util.BlockPlacer;
import net.wurstclient.util.BlockPlacer.BlockPlacingParams;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.util.InteractionSimulator;
import net.wurstclient.util.RenderUtils;
import net.wurstclient.util.RotationUtils;

@SearchTags({"build random", "RandomBuild", "random build", "PlaceRandom",
	"place random", "RandomPlace", "random place"})
public final class BuildRandomHack extends Hack
	implements UpdateListener, RenderListener
{
	private final SliderSetting range =
		new SliderSetting("Range", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private SliderSetting maxAttempts = new SliderSetting("Max attempts",
		"BuildRandom 尝试放置的最大随机位置数"
			+ "在同一个游戏刻内放置方块。\n\n"
			+ "更高的值会加快建造速度，但代价是"
			+ "更多的卡顿。",
		128, 1, 1024, 1, ValueDisplay.INTEGER);
	
	private final CheckboxSetting checkItem =
		new CheckboxSetting("Check held item",
			"只有当你实际手持方块时才建造。\n"
				+ "关闭此选项可以用火、水、熔岩、刷怪蛋建造，"
				+ "或者你只想用空手在随机位置右键点击"
				+ "时使用。",
			true);
	
	private final CheckboxSetting checkLOS =
		new CheckboxSetting("Check line of sight",
			"确保 BuildRandom 不会尝试在墙后放置方块。",
			false);
	
	private final FaceTargetSetting faceTarget =
		FaceTargetSetting.withoutPacketSpam(this, FaceTarget.SERVER);
	
	private final SwingHandSetting swingHand =
		new SwingHandSetting(this, SwingHand.SERVER);
	
	private final CheckboxSetting fastPlace =
		new CheckboxSetting("Always FastPlace",
			"即使未启用快速放置，也像启用了一样建造。", false);
	
	private final CheckboxSetting placeWhileBreaking = new CheckboxSetting(
		"Place while breaking",
		"即使你在破坏方块时也会建造。\n"
			+ "Possible with hacks, but wouldn't work in vanilla. May look suspicious.",
		false);
	
	private final CheckboxSetting placeWhileRiding = new CheckboxSetting(
		"Place while riding",
		"Builds even while you are riding a vehicle.\n"
			+ "Possible with hacks, but wouldn't work in vanilla. May look suspicious.",
		false);
	
	private final CheckboxSetting indicator = new CheckboxSetting("Indicator",
		"显示 BuildRandom 正在放置方块的位置。", true);
	
	private final Random random = new Random();
	private BlockPos lastPos;
	
	public BuildRandomHack()
	{
		super("BuildRandom");
		setCategory(Category.BLOCKS);
		addSetting(range);
		addSetting(maxAttempts);
		addSetting(checkItem);
		addSetting(checkLOS);
		addSetting(faceTarget);
		addSetting(swingHand);
		addSetting(fastPlace);
		addSetting(placeWhileBreaking);
		addSetting(placeWhileRiding);
		addSetting(indicator);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		lastPos = null;
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		lastPos = null;
		
		if(WURST.getHax().freecamHack.isEnabled())
			return;
		
		if(!fastPlace.isChecked() && ((MinecraftAccessor)(Object)MC).wurst_getRightClickDelay() > 0)
			return;
		
		if(checkItem.isChecked() && !MC.player.isHolding(
			stack -> !stack.isEmpty() && stack.getItem() instanceof BlockItem))
			return;
		
		if(!placeWhileBreaking.isChecked() && MC.gameMode.isDestroying())
			return;
		
		if(!placeWhileRiding.isChecked() && MC.player.isHandsBusy())
			return;
		
		int maxAttempts = this.maxAttempts.getValueI();
		int blockRange = range.getValueCeil();
		int bound = blockRange * 2 + 1;
		BlockPos pos;
		int attempts = 0;
		
		do
		{
			// generate random position
			pos = BlockPos.containing(RotationUtils.getEyesPos()).offset(
				random.nextInt(bound) - blockRange,
				random.nextInt(bound) - blockRange,
				random.nextInt(bound) - blockRange);
			attempts++;
			
		}while(attempts < maxAttempts && !tryToPlaceBlock(pos));
	}
	
	private boolean tryToPlaceBlock(BlockPos pos)
	{
		if(!BlockUtils.getState(pos).canBeReplaced())
			return false;
		
		BlockPlacingParams params = BlockPlacer.getBlockPlacingParams(pos);
		if(params == null || params.distanceSq() > range.getValueSq())
			return false;
		if(checkLOS.isChecked() && !params.lineOfSight())
			return false;

		((MinecraftAccessor)(Object)MC).wurst_setRightClickDelay(4);
		faceTarget.face(params.hitVec());
		lastPos = pos;
		
		InteractionSimulator.rightClickBlock(params.toHitResult(),
			swingHand.getSelected());
		return true;
	}
	
	@Override
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		if(lastPos == null || !indicator.isChecked())
			return;
		
		// Get colors
		float red = partialTicks * 2F;
		float green = 2 - red;
		float[] rgb = {red, green, 0};
		int quadColor = RenderUtils.toIntColor(rgb, 0.25F);
		int lineColor = RenderUtils.toIntColor(rgb, 0.5F);
		
		// Draw box
		AABB box = new AABB(lastPos);
		RenderUtils.drawSolidBox(matrixStack, box, quadColor, false);
		RenderUtils.drawOutlinedBox(matrixStack, box, lineColor, false);
	}
}
