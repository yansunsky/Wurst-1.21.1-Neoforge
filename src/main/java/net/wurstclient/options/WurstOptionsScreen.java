/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.wurstclient.WurstClient;
import net.wurstclient.analytics.PlausibleAnalytics;
import net.wurstclient.commands.FriendsCmd;
import net.wurstclient.hacks.XRayHack;
import net.wurstclient.other_features.VanillaSpoofOtf;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.util.ChatUtils;

public class WurstOptionsScreen extends Screen
{

	private List<AbstractWidget> wurst_getButtons()
	{
		List<AbstractWidget> result = new ArrayList<>();
		for(var child : this.children())
			if(child instanceof AbstractWidget widget)
				result.add(widget);
		return result;
	}

	private Screen prevScreen;

	public WurstOptionsScreen(Screen prevScreen)
	{
		super(Component.literal(""));
		this.prevScreen = prevScreen;
	}

	@Override
	public void init()
	{
		addRenderableWidget(Button
				.builder(Component.literal("返回"),
						b -> minecraft.setScreen(prevScreen))
				.bounds(width / 2 - 100, height / 4 + 144 - 16, 200, 20).build());

		addSettingButtons();
		addManagerButtons();
		addLinkButtons();
	}

	private void addSettingButtons()
	{
		WurstClient wurst = WurstClient.INSTANCE;
		FriendsCmd friendsCmd = wurst.getCmds().friendsCmd;
		CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
		PlausibleAnalytics plausible = wurst.getPlausible();
		VanillaSpoofOtf vanillaSpoofOtf = wurst.getOtfs().vanillaSpoofOtf;
		CheckboxSetting forceEnglish =
				wurst.getOtfs().translationsOtf.getForceEnglish();

		new WurstOptionsButton(-154, 24,
				() -> "单击添加好友："
						+ (middleClickFriends.isChecked() ? "ON" : "OFF"),
				middleClickFriends.getWrappedDescription(200),
				b -> middleClickFriends
						.setChecked(!middleClickFriends.isChecked()));

		new WurstOptionsButton(-154, 72,
				() -> "原版伪装："
						+ (vanillaSpoofOtf.isEnabled() ? "ON" : "OFF"),
				vanillaSpoofOtf.getDescription(),
				b -> vanillaSpoofOtf.doPrimaryAction());

		new WurstOptionsButton(-154, 96,
				() -> "翻译：" + (!forceEnglish.isChecked() ? "开" : "关"),
				"允许 Wurst 中的文本以英语以外的其他语言显示。"
						+ " 它将使用与 Minecraft 相同的语言。\n\n"
						+ "这是一个实验性功能！",
				b -> forceEnglish.setChecked(!forceEnglish.isChecked()));
	}

	private void addManagerButtons()
	{
		XRayHack xRayHack = WurstClient.INSTANCE.getHax().xRayHack;

		new WurstOptionsButton(-50, 24, () -> "按键绑定",
				"按键绑定允许你只需按下一个按键就能切换任何作弊功能或命令。",
				b -> minecraft.setScreen(new KeybindManagerScreen(this)));

		new WurstOptionsButton(-50, 48, () -> "透视方块",
				"管理 X-Ray 将显示的方块。",
				b -> xRayHack.openBlockListEditor(this));

		new WurstOptionsButton(-50, 72, () -> "Zoom",
				"缩放管理器允许你更改缩放按键以及放大的程度。",
				b -> minecraft.setScreen(new ZoomManagerScreen(this)));
	}

	private void addLinkButtons()
	{
		OS os = Util.getPlatform();

		new WurstOptionsButton(54, 24, () -> "官方网站",
				"§n§lWurstClient.net",
				b -> os.openUri("https://www.wurstclient.net/options-website/"));

		new WurstOptionsButton(54, 48, () -> "Wurst Wiki", "§n§lWurst.Wiki",
				b -> os.openUri("https://www.wurstclient.net/options-wiki/"));

		new WurstOptionsButton(54, 72, () -> "WurstForum", "§n§lWurstForum.net",
				b -> os.openUri("https://www.wurstclient.net/options-forum/"));

		new WurstOptionsButton(54, 96, () -> "Twitter", "@Wurst_Imperium",
				b -> os.openUri("https://www.wurstclient.net/options-twitter/"));

		new WurstOptionsButton(54, 120, () -> "捐赠",
				"§n§lWurstClient.net/donate\n"
						+ "立即捐赠，帮助我让 Wurst Client 保持免费，"
						+ "让每个人都能使用。\n\n"
						+ "每一份帮助都非常感谢！作为回报，"
						+ "你还可以获得一些很酷的特权。",
				b -> os.openUri("https://www.wurstclient.net/options-donate/"));
	}

	@Override
	public void onClose()
	{
		minecraft.setScreen(prevScreen);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY,
					   float partialTicks)
	{
		renderBackground(context, mouseX, mouseY, partialTicks);
		renderTitles(context);

		for(Renderable drawable : renderables)
			drawable.render(context, mouseX, mouseY, partialTicks);

		renderButtonTooltip(context, mouseX, mouseY);
	}

	private void renderTitles(GuiGraphics context)
	{
		Font tr = minecraft.font;
		int middleX = width / 2;
		int y1 = 40;
		int y2 = height / 4 + 24 - 28;

		context.drawCenteredString(tr, "Wurst 选项", middleX, y1, 0xffffff);

		context.drawCenteredString(tr, "设置", middleX - 104, y2, 0xcccccc);
		context.drawCenteredString(tr, "管理", middleX, y2, 0xcccccc);
		context.drawCenteredString(tr, "链接", middleX + 104, y2, 0xcccccc);
	}

	private void renderButtonTooltip(GuiGraphics context, int mouseX,
									 int mouseY)
	{
		for(AbstractWidget button : wurst_getButtons())
		{
			if(!button.isHoveredOrFocused()
					|| !(button instanceof WurstOptionsButton))
				continue;

			WurstOptionsButton woButton = (WurstOptionsButton)button;

			if(woButton.tooltip.isEmpty())
				continue;

			context.renderComponentTooltip(font, woButton.tooltip, mouseX,
					mouseY);
			break;
		}
	}

	private final class WurstOptionsButton extends Button
	{
		private final Supplier<String> messageSupplier;
		private final List<Component> tooltip;

		public WurstOptionsButton(int xOffset, int yOffset,
								  Supplier<String> messageSupplier, String tooltip,
								  OnPress pressAction)
		{
			super(WurstOptionsScreen.this.width / 2 + xOffset,
					WurstOptionsScreen.this.height / 4 - 16 + yOffset, 100, 20,
					Component.literal(messageSupplier.get()), pressAction,
					Button.DEFAULT_NARRATION);

			this.messageSupplier = messageSupplier;

			if(tooltip.isEmpty())
				this.tooltip = Arrays.asList();
			else
			{
				String[] lines = ChatUtils.wrapText(tooltip, 200).split("\n");

				Component[] lines2 = new Component[lines.length];
				for(int i = 0; i < lines.length; i++)
					lines2[i] = Component.literal(lines[i]);

				this.tooltip = Arrays.asList(lines2);
			}

			addRenderableWidget(this);
		}

		@Override
		public void onPress()
		{
			super.onPress();
			setMessage(Component.literal(messageSupplier.get()));
		}
	}
}
