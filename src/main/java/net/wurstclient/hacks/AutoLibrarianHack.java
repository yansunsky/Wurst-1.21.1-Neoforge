/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.hacks.autolibrarian.BookOffer;
import net.wurstclient.hacks.autolibrarian.UpdateBooksSetting;
import net.wurstclient.mixin.MinecraftAccessor;
import net.wurstclient.mixin.MultiPlayerGameModeAccessor;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.BookOffersSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.FaceTargetSetting;
import net.wurstclient.settings.FaceTargetSetting.FaceTarget;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.settings.SwingHandSetting;
import net.wurstclient.settings.SwingHandSetting.SwingHand;
import net.wurstclient.util.*;
import net.wurstclient.util.BlockBreaker.BlockBreakingParams;
import net.wurstclient.util.BlockPlacer.BlockPlacingParams;

@SearchTags({"auto librarian", "AutoVillager", "auto villager",
	"VillagerTrainer", "villager trainer", "LibrarianTrainer",
	"librarian trainer", "AutoHmmm", "auto hmmm"})
public final class AutoLibrarianHack extends Hack
	implements UpdateListener, RenderListener
{
	private final BookOffersSetting wantedBooks = new BookOffersSetting(
		"需要的书",
		"你希望村民出售的附魔书列表。\n\n"
			+ "一旦村民学会出售其中一本，AutoLibrarian 就会停止训练当前村民。\n\n"
			+ "你还可以为每本书设置最高价格，"
			+ "以防你已经有村民在出售它但想要更便宜的价格。",
		"minecraft:depth_strider;3", "minecraft:efficiency;5",
		"minecraft:feather_falling;4", "minecraft:fortune;3",
		"minecraft:looting;3", "minecraft:mending;1", "minecraft:protection;4",
		"minecraft:respiration;3", "minecraft:sharpness;5",
		"minecraft:silk_touch;1", "minecraft:unbreaking;3");
	
	private final CheckboxSetting lockInTrade = new CheckboxSetting(
		"锁定交易",
		"一旦村民学会出售你想要的书，就自动从村民那里购买。"
			+ "这样可以防止村民之后更改交易内容。\n\n"
			+ "使用此功能时，请确保你的背包里至少有"
			+ " 24 张纸和 9 个绿宝石。或者，1 本书和"
			+ " 64 个绿宝石也可以。",
		false);
	
	private final UpdateBooksSetting updateBooks = new UpdateBooksSetting();
	
	private final SliderSetting range =
		new SliderSetting("Range", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final FaceTargetSetting faceTarget =
		FaceTargetSetting.withoutPacketSpam(this, FaceTarget.SERVER);
	
	private final SwingHandSetting swingHand =
		new SwingHandSetting(this, SwingHand.SERVER);
	
	private final SliderSetting repairMode = new SliderSetting("修复模式",
		"当斧头的耐久度达到指定阈值时，"
			+ "阻止 AutoLibrarian 使用你的斧头，这样你可以在它损坏前修复它。\n"
			+ "可以从 0（关闭）调整到 100 次剩余使用次数。",
		1, 0, 100, 1, ValueDisplay.INTEGER.withLabel(0, "关"));
	
	private final OverlayRenderer overlay = new OverlayRenderer();
	private final HashSet<Villager> experiencedVillagers = new HashSet<>();
	
	private Villager villager;
	private BlockPos jobSite;
	
	private boolean placingJobSite;
	private boolean breakingJobSite;
	
	public AutoLibrarianHack()
	{
		super("AutoLibrarian");
		setCategory(Category.OTHER);
		addSetting(wantedBooks);
		addSetting(lockInTrade);
		addSetting(updateBooks);
		addSetting(range);
		addSetting(faceTarget);
		addSetting(swingHand);
		addSetting(repairMode);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}

	// 사용부

	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(RenderListener.class, this);

		if(breakingJobSite)
		{
			((MultiPlayerGameModeAccessor)(Object)MC.gameMode)
					.wurst_setIsDestroying(true);
			MC.gameMode.stopDestroyBlock();
			breakingJobSite = false;
		}

		overlay.resetProgress();
		villager = null;
		jobSite = null;
		placingJobSite = false;
		breakingJobSite = false;
		experiencedVillagers.clear();
	}


	@Override
	public void onUpdate()
	{
		if(villager == null)
		{
			setTargetVillager();
			return;
		}
		
		if(jobSite == null)
		{
			setTargetJobSite();
			return;
		}
		
		if(placingJobSite && breakingJobSite)
			throw new IllegalStateException(
				"Trying to place and break job site at the same time. Something is wrong.");
		
		if(placingJobSite)
		{
			placeJobSite();
			return;
		}
		
		if(breakingJobSite)
		{
			breakJobSite();
			return;
		}
		
		if(!(MC.screen instanceof MerchantScreen tradeScreen))
		{
			openTradeScreen();
			return;
		}
		
		// Can't see experience until the trade screen is open, so we have to
		// check it here and start over if the villager is already experienced.
		int experience = tradeScreen.getMenu().getTraderXp();
		if(experience > 0)
		{
			ChatUtils.warning("位于 "
				+ villager.blockPosition().toShortString()
				+ " 的村民已经有经验了，意味着它无法再被训练。");
			ChatUtils.message("正在寻找另一个村民...");
			experiencedVillagers.add(villager);
			villager = null;
			jobSite = null;
			closeTradeScreen();
			return;
		}
		
		// check which book the villager is selling
		BookOffer bookOffer =
			findEnchantedBookOffer(tradeScreen.getMenu().getOffers());
		
		if(bookOffer == null)
		{
			ChatUtils.message("村民没有在出售附魔书。");
			closeTradeScreen();
			breakingJobSite = true;
			System.out.println("Breaking job site...");
			return;
		}
		
		ChatUtils.message(
			"村民正在出售 " + bookOffer.getEnchantmentNameWithLevel()
				+ "，价格 " + bookOffer.getFormattedPrice() + "。");
		
		// if wrong enchantment, break job site and start over
		if(!wantedBooks.isWanted(bookOffer))
		{
			breakingJobSite = true;
			System.out.println("Breaking job site...");
			closeTradeScreen();
			return;
		}
		
		// lock in the trade, if enabled
		if(lockInTrade.isChecked())
		{
			// select the first valid trade
			tradeScreen.getMenu().setSelectionHint(0);
			tradeScreen.getMenu().tryMoveItems(0);
			MC.getConnection().send(new ServerboundSelectTradePacket(0));
			
			// buy whatever the villager is selling
			MC.gameMode.handleInventoryMouseClick(
				tradeScreen.getMenu().containerId, 2, 0, ClickType.PICKUP,
				MC.player);
			
			// close the trade screen
			closeTradeScreen();
		}
		
		// update wanted books based on the user's settings
		updateBooks.getSelected().update(wantedBooks, bookOffer);
		
		ChatUtils.message("完成！");
		setEnabled(false);
	}
	
	private void breakJobSite()
	{
		if(jobSite == null)
			throw new IllegalStateException("Job site is null.");
		
		BlockBreakingParams params =
			BlockBreaker.getBlockBreakingParams(jobSite);
		
		if(params == null || BlockUtils.getState(jobSite).canBeReplaced())
		{
			System.out.println("Job site has been broken. Replacing...");
			breakingJobSite = false;
			placingJobSite = true;
			return;
		}
		
		// equip tool
		WURST.getHax().autoToolHack.equipBestTool(jobSite, false, true,
			repairMode.getValueI());
		
		// face block
		faceTarget.face(params.hitVec());
		
		// damage block and swing hand
		if(MC.gameMode.continueDestroyBlock(jobSite, params.side()))
			swingHand.swing(InteractionHand.MAIN_HAND);
		
		// update progress
		overlay.updateProgress();
	}
	
	private void placeJobSite()
	{
		if(jobSite == null)
			throw new IllegalStateException("Job site is null.");
		
		if(!BlockUtils.getState(jobSite).canBeReplaced())
		{
			if(BlockUtils.getBlock(jobSite) == Blocks.LECTERN)
			{
				System.out.println("Job site has been placed.");
				placingJobSite = false;
				
			}else
			{
				System.out
					.println("Found wrong block at job site. Breaking...");
				breakingJobSite = true;
				placingJobSite = false;
			}
			
			return;
		}
		
		// check if holding a lectern
		if(!MC.player.isHolding(Items.LECTERN))
		{
			InventoryUtils.selectItem(Items.LECTERN, 36);
			return;
		}
		
		// get the hand that is holding the lectern
		InteractionHand hand = MC.player.getMainHandItem().is(Items.LECTERN)
			? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		
		// sneak-place to avoid activating trapdoors/chests/etc.
		IKeyBinding sneakKey = IKeyBinding.get(MC.options.keyShift);
		sneakKey.setDown(true);
		if(!MC.player.isShiftKeyDown())
			return;
		
		// get block placing params
		BlockPlacingParams params = BlockPlacer.getBlockPlacingParams(jobSite);
		if(params == null)
		{
			sneakKey.resetPressedState();
			return;
		}
		
		// face block
		faceTarget.face(params.hitVec());
		
		// place block
		InteractionResult result =
			MC.gameMode.useItemOn(MC.player, hand, params.toHitResult());
		
		// swing hand
		if(result.consumesAction() && result.shouldSwing())
			swingHand.swing(hand);
		
		// reset sneak
		sneakKey.resetPressedState();
	}
	
	private void openTradeScreen()
	{
		if(((MinecraftAccessor)(Object)MC).wurst_getRightClickDelay() > 0)
			return;
		
		MultiPlayerGameMode im = MC.gameMode;
		LocalPlayer player = MC.player;
		
		if(player.distanceToSqr(villager) > range.getValueSq())
		{
			ChatUtils.error("村民超出范围。请考虑把村民"
				+ "困住，以免它走开。");
			setEnabled(false);
			return;
		}
		
		// create realistic hit result
		AABB box = villager.getBoundingBox();
		Vec3 start = RotationUtils.getEyesPos();
		Vec3 end = box.getCenter();
		Vec3 hitVec = box.clip(start, end).orElse(start);
		EntityHitResult hitResult = new EntityHitResult(villager, hitVec);
		
		// face end vector
		faceTarget.face(end);
		
		// click on villager
		InteractionHand hand = InteractionHand.MAIN_HAND;
		InteractionResult actionResult =
			im.interactAt(player, villager, hitResult, hand);
		
		if(!actionResult.consumesAction())
			im.interact(player, villager, hand);
		
		// swing hand
		if(actionResult.consumesAction() && actionResult.shouldSwing())
			swingHand.swing(hand);
		
		// set cooldown
		((MinecraftAccessor)(Object)MC).wurst_setRightClickDelay(4);
	}
	
	private void closeTradeScreen()
	{
		MC.player.closeContainer();
		((MinecraftAccessor)(Object)MC).wurst_setRightClickDelay(4);
	}
	
	private BookOffer findEnchantedBookOffer(MerchantOffers tradeOffers)
	{
		for(MerchantOffer tradeOffer : tradeOffers)
		{
			ItemStack stack = tradeOffer.getResult();
			if(!(stack.getItem() instanceof EnchantedBookItem))
				continue;
			
			Set<Entry<Holder<Enchantment>>> enchantmentLevelMap =
				EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet();
			if(enchantmentLevelMap.isEmpty())
				continue;
			
			Object2IntMap.Entry<Holder<Enchantment>> firstEntry =
				enchantmentLevelMap.stream().findFirst().orElseThrow();
			
			String enchantment = firstEntry.getKey().getRegisteredName();
			int level = firstEntry.getIntValue();
			int price = tradeOffer.getCostA().getCount();
			BookOffer bookOffer = new BookOffer(enchantment, level, price);
			
			if(!bookOffer.isFullyValid())
			{
				System.out.println("Found invalid enchanted book offer.\n"
					+ "Component data: " + enchantmentLevelMap);
				continue;
			}
			
			return bookOffer;
		}
		
		return null;
	}
	
	private void setTargetVillager()
	{
		LocalPlayer player = MC.player;
		double rangeSq = range.getValueSq();
		
		Stream<Villager> stream = StreamSupport
			.stream(MC.level.entitiesForRendering().spliterator(), true)
			.filter(e -> !e.isRemoved()).filter(Villager.class::isInstance)
			.map(e -> (Villager)e).filter(e -> e.getHealth() > 0)
			.filter(e -> player.distanceToSqr(e) <= rangeSq)
			.filter(e -> e.getVillagerData()
				.getProfession() == VillagerProfession.LIBRARIAN)
			.filter(e -> e.getVillagerData().getLevel() == 1)
			.filter(e -> !experiencedVillagers.contains(e));
		
		villager =
			stream.min(Comparator.comparingDouble(e -> player.distanceToSqr(e)))
				.orElse(null);
		
		if(villager == null)
		{
			String errorMsg = "找不到附近的图书管理员。";
			int numExperienced = experiencedVillagers.size();
			if(numExperienced > 0)
				errorMsg += "（除了 " + numExperienced + " 个"
					+ (numExperienced == 1 ? "is" : "are")
					+ " 已有经验的。）";
			
			ChatUtils.error(errorMsg);
			ChatUtils.message("请确保图书管理员和讲台"
				+ "在你站立的位置都够得着。");
			setEnabled(false);
			return;
		}
		
		System.out.println("Found villager at " + villager.blockPosition());
	}
	
	private void setTargetJobSite()
	{
		Vec3 eyesVec = RotationUtils.getEyesPos();
		double rangeSq = range.getValueSq();
		
		Stream<BlockPos> stream = BlockUtils
			.getAllInBoxStream(BlockPos.containing(eyesVec),
				range.getValueCeil())
			.filter(
				pos -> eyesVec.distanceToSqr(Vec3.atCenterOf(pos)) <= rangeSq)
			.filter(pos -> BlockUtils.getBlock(pos) == Blocks.LECTERN);
		
		jobSite = stream
			.min(Comparator.comparingDouble(
				pos -> villager.distanceToSqr(Vec3.atCenterOf(pos))))
			.orElse(null);
		
		if(jobSite == null)
		{
			ChatUtils.error("找不到图书管理员的讲台。");
			ChatUtils.message("请确保图书管理员和讲台"
				+ "在你站立的位置都够得着。");
			setEnabled(false);
			return;
		}
		
		System.out.println("Found lectern at " + jobSite);
	}
	
	@Override
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		int green = 0xC000FF00;
		int red = 0xC0FF0000;
		
		if(villager != null)
			RenderUtils.drawOutlinedBox(matrixStack, villager.getBoundingBox(),
				green, false);
		
		if(jobSite != null)
			RenderUtils.drawOutlinedBox(matrixStack, new AABB(jobSite), green,
				false);
		
		List<AABB> expVilBoxes = experiencedVillagers.stream()
			.map(Villager::getBoundingBox).toList();
		RenderUtils.drawOutlinedBoxes(matrixStack, expVilBoxes, red, false);
		RenderUtils.drawCrossBoxes(matrixStack, expVilBoxes, red, false);
		
		if(breakingJobSite)
			overlay.render(matrixStack, partialTicks, jobSite);
	}
}
