/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wurstclient.clickgui.Component;
import net.wurstclient.keybinds.PossibleKeybind;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.text.WText;

public abstract class Setting
{
	private static final Map<String, String> CN_NAMES = Map.ofEntries(
		Map.entry("AI range", "AI范围"),
		Map.entry("Accent", "强调色"),
		Map.entry("Aim while blocking", "格挡时自瞄"),
		Map.entry("All GUIs", "全部GUI"),
		Map.entry("Allow ClickGUI", "允许菜单界面"),
		Map.entry("Allow chorus fruit", "允许紫颂果"),
		Map.entry("Allow hunger effect", "允许饥饿效果"),
		Map.entry("Allow jump key", "允许跳跃键"),
		Map.entry("Allow offhand", "允许副手"),
		Map.entry("Allow other screens", "允许其他界面"),
		Map.entry("Allow poison effect", "允许中毒效果"),
		Map.entry("Allow sneak key", "允许潜行键"),
		Map.entry("Allow sprint key", "允许疾跑键"),
		Map.entry("Altitude", "高度"),
		Map.entry("Always FastPlace", "始终快速放置"),
		Map.entry("Always show player names", "始终显示玩家名"),
		Map.entry("Amethyst", "紫水晶"),
		Map.entry("Amount", "数量"),
		Map.entry("Animations", "动画"),
		Map.entry("Anti-Kick", "防踢"),
		Map.entry("Anti-Kick Interval", "防踢间隔"),
		Map.entry("Area", "区域"),
		Map.entry("Attack while blocking", "格挡时攻击"),
		Map.entry("Auto-place anchors", "自动放置锚点"),
		Map.entry("Background", "背景"),
		Map.entry("Bamboo", "竹子"),
		Map.entry("Barrel color", "木桶颜色"),
		Map.entry("Beetroots", "甜菜根"),
		Map.entry("Block", "方块"),
		Map.entry("Block Hit Color", "方块击中颜色"),
		Map.entry("Blocking offset", "格挡偏移"),
		Map.entry("Blocks", "方块列表"),
		Map.entry("Cactus", "仙人掌"),
		Map.entry("Carrots", "胡萝卜"),
		Map.entry("Catch delay", "捕捉延迟"),
		Map.entry("Change Moon Phase", "更改月相"),
		Map.entry("Change World Time", "更改世界时间"),
		Map.entry("Check held item", "检查手持物品"),
		Map.entry("Check line of sight", "检查视线"),
		Map.entry("Chest boat color", "运输船颜色"),
		Map.entry("Chest cart color", "运输矿车颜色"),
		Map.entry("Chest color", "箱子颜色"),
		Map.entry("Chorus Plants", "紫颂植物"),
		Map.entry("Cocoa", "可可豆"),
		Map.entry("Cocoa Beans", "可可豆"),
		Map.entry("Color", "颜色"),
		Map.entry("Command", "命令"),
		Map.entry("Crafter color", "合成器颜色"),
		Map.entry("Crops", "农作物"),
		Map.entry("DD color", "DD颜色"),
		Map.entry("Day color", "白天颜色"),
		Map.entry("Death screen button", "死亡界面按钮"),
		Map.entry("Debug draw", "调试绘制"),
		Map.entry("Debug mode", "调试模式"),
		Map.entry("Delay", "延迟"),
		Map.entry("Depth test", "深度测试"),
		Map.entry("Detect received language", "检测接收语言"),
		Map.entry("Disable Freecam", "禁用自由视角"),
		Map.entry("Disable Rain", "禁用下雨"),
		Map.entry("Disable signatures", "禁用签名"),
		Map.entry("Disable telemetry", "禁用遥测"),
		Map.entry("Dispenser color", "发射器颜色"),
		Map.entry("Distance", "距离"),
		Map.entry("Draw blocks to harvest", "绘制待收获方块"),
		Map.entry("Draw blocks to replant", "绘制待补种方块"),
		Map.entry("Draw distance", "绘制距离"),
		Map.entry("Draw replanting spots", "绘制补种位置"),
		Map.entry("Dropper color", "投掷器颜色"),
		Map.entry("ESP color", "透视颜色"),
		Map.entry("Eat while walking", "行走时进食"),
		Map.entry("Edge distance", "边缘距离"),
		Map.entry("End gateway color", "末地折跃门颜色"),
		Map.entry("End portal color", "末地传送门颜色"),
		Map.entry("End portal frame color", "末地传送门框架颜色"),
		Map.entry("Ender color", "末影箱颜色"),
		Map.entry("Entity Hit Color", "实体击中颜色"),
		Map.entry("FOV", "视野"),
		Map.entry("Fade", "淡出效果"),
		Map.entry("Fall speed", "下落速度"),
		Map.entry("Filter allays", "过滤悦灵"),
		Map.entry("Filter armor stands", "过滤盔甲架"),
		Map.entry("Filter babies", "过滤幼年生物"),
		Map.entry("Filter bats", "过滤蝙蝠"),
		Map.entry("Filter end crystals", "过滤末影水晶"),
		Map.entry("Filter endermen", "过滤末影人"),
		Map.entry("Filter flying", "过滤飞行生物"),
		Map.entry("Filter golems", "过滤傀儡"),
		Map.entry("Filter hostile mobs", "过滤敌对生物"),
		Map.entry("Filter invisible", "过滤隐身"),
		Map.entry("Filter minecarts", "过滤矿车"),
		Map.entry("Filter named", "过滤已命名"),
		Map.entry("Filter neutral mobs", "过滤中立生物"),
		Map.entry("Filter passive mobs", "过滤被动生物"),
		Map.entry("Filter passive water mobs", "过滤水生生物"),
		Map.entry("Filter pets", "过滤宠物"),
		Map.entry("Filter piglins", "过滤猪灵"),
		Map.entry("Filter players", "过滤玩家"),
		Map.entry("Filter server messages", "过滤服务器消息"),
		Map.entry("Filter shulker bullets", "过滤潜影贝子弹"),
		Map.entry("Filter shulkers", "过滤潜影贝"),
		Map.entry("Filter sleeping", "过滤睡觉"),
		Map.entry("Filter slimes", "过滤史莱姆"),
		Map.entry("Filter untamed", "过滤未驯服"),
		Map.entry("Filter villagers", "过滤村民"),
		Map.entry("Filter zombie piglins", "过滤僵尸猪灵"),
		Map.entry("Filter zombie villagers", "过滤僵尸村民"),
		Map.entry("Flat mode", "平面模式"),
		Map.entry("Frequency penalty", "频率惩罚"),
		Map.entry("Furnace color", "熔炉颜色"),
		Map.entry("Glow Berries", "发光浆果"),
		Map.entry("Guide", "指南"),
		Map.entry("Guide color", "指南颜色"),
		Map.entry("Health", "生命值"),
		Map.entry("Height", "高度"),
		Map.entry("Height control", "高度控制"),
		Map.entry("Hopper cart color", "漏斗矿车颜色"),
		Map.entry("Hopper color", "漏斗颜色"),
		Map.entry("Horizontal Strength", "水平力度"),
		Map.entry("Hungry Sprint", "饥饿疾跑"),
		Map.entry("ID", "ID"),
		Map.entry("Ignore errors", "忽略错误"),
		Map.entry("Ignore mouse input", "忽略鼠标输入"),
		Map.entry("Include barrels", "包含木桶"),
		Map.entry("Include chest boats", "包含运输船"),
		Map.entry("Include chest carts", "包含运输矿车"),
		Map.entry("Include crafters", "包含合成器"),
		Map.entry("Include dispensers", "包含发射器"),
		Map.entry("Include droppers", "包含投掷器"),
		Map.entry("Include end gateways", "包含末地折跃门"),
		Map.entry("Include end portal frames", "包含末地传送门框架"),
		Map.entry("Include end portals", "包含末地传送门"),
		Map.entry("Include ender chests", "包含末影箱"),
		Map.entry("Include furnaces", "包含熔炉"),
		Map.entry("Include hopper carts", "包含漏斗矿车"),
		Map.entry("Include hoppers", "包含漏斗"),
		Map.entry("Include nether portals", "包含下界传送门"),
		Map.entry("Include normal chests", "包含普通箱子"),
		Map.entry("Include pots", "包含花盆"),
		Map.entry("Include shulkers", "包含潜影盒"),
		Map.entry("Include trap chests", "包含陷阱箱"),
		Map.entry("Indicator", "指示器"),
		Map.entry("Injury threshold", "伤害阈值"),
		Map.entry("Items", "物品列表"),
		Map.entry("Kelp", "海带"),
		Map.entry("Keybind", "按键绑定"),
		Map.entry("Legit mode", "合法模式"),
		Map.entry("Limit", "限制"),
		Map.entry("Lock ID", "锁定ID"),
		Map.entry("Log chunks", "记录区块"),
		Map.entry("Max attempts", "最大尝试次数"),
		Map.entry("Max height", "最大高度"),
		Map.entry("Max settings height", "设置最大高度"),
		Map.entry("Max suggestions per draft", "每稿最大建议数"),
		Map.entry("Max tokens", "最大令牌数"),
		Map.entry("Max vein size", "最大矿脉大小"),
		Map.entry("Melons", "西瓜"),
		Map.entry("Middle click friends", "中键添加好友"),
		Map.entry("Min depth", "最小深度"),
		Map.entry("Min height", "最小高度"),
		Map.entry("Min hunger", "最小饥饿值"),
		Map.entry("Miss Color", "未命中颜色"),
		Map.entry("Moon Phase", "月相"),
		Map.entry("Move speed", "移动速度"),
		Map.entry("MultiTill", "多格耕地"),
		Map.entry("Nether Warts", "下界疣"),
		Map.entry("Nether portal color", "下界传送门颜色"),
		Map.entry("New chunks color", "新区块颜色"),
		Map.entry("Night color", "夜晚颜色"),
		Map.entry("NoCheat+ bypass", "NoCheat+绕过"),
		Map.entry("Non-AI range", "非AI范围"),
		Map.entry("Non-blocking offset", "非格挡偏移"),
		Map.entry("Offset", "偏移"),
		Map.entry("Old chunks color", "旧区块颜色"),
		Map.entry("Omnidirectional Sprint", "全方向疾跑"),
		Map.entry("Opacity", "不透明度"),
		Map.entry("OpenAI legacy endpoint", "OpenAI旧版接口"),
		Map.entry("Ores", "矿石"),
		Map.entry("Other", "其他"),
		Map.entry("Particles", "粒子效果"),
		Map.entry("Patience", "耐心"),
		Map.entry("Pause for mace", "为重锤暂停"),
		Map.entry("Pause when sneaking", "潜行时暂停"),
		Map.entry("Pitcher Plants", "瓶子草"),
		Map.entry("Place torches", "放置火把"),
		Map.entry("Potatoes", "马铃薯"),
		Map.entry("Pots color", "花盆颜色"),
		Map.entry("Power", "力度"),
		Map.entry("Presence penalty", "存在惩罚"),
		Map.entry("Pumpkins", "南瓜"),
		Map.entry("RC mode", "RC模式"),
		Map.entry("Radius", "半径"),
		Map.entry("Range", "范围"),
		Map.entry("Release time", "释放时间"),
		Map.entry("Repair mode", "修复模式"),
		Map.entry("Retry delay", "重试延迟"),
		Map.entry("Reverse sorting", "反向排序"),
		Map.entry("Reverse steal order", "反向偷取顺序"),
		Map.entry("Rotate with player", "随玩家旋转"),
		Map.entry("Rotation Speed", "旋转速度"),
		Map.entry("Saplings", "树苗"),
		Map.entry("Scale", "缩放"),
		Map.entry("Show counter", "显示计数"),
		Map.entry("Show wait time", "显示等待时间"),
		Map.entry("Shulker color", "潜影盒颜色"),
		Map.entry("Slot", "槽位"),
		Map.entry("Sneak at edges", "边缘潜行"),
		Map.entry("Sound", "音效"),
		Map.entry("Speed", "速度"),
		Map.entry("Speed randomization", "速度随机化"),
		Map.entry("Spoof Vanilla", "伪装原版"),
		Map.entry("Stack size", "堆叠大小"),
		Map.entry("Steal/Store buttons", "偷取/存放按钮"),
		Map.entry("Stems", "茎秆"),
		Map.entry("Stop flying in water", "水中停止飞行"),
		Map.entry("Strength", "强度"),
		Map.entry("Sugar Cane", "甘蔗"),
		Map.entry("Super fast mode", "超快模式"),
		Map.entry("Sweet Berries", "甜浆果"),
		Map.entry("Temperature", "温度"),
		Map.entry("Template", "模板"),
		Map.entry("Text", "文本"),
		Map.entry("Time", "时间"),
		Map.entry("Tooltip opacity", "提示不透明度"),
		Map.entry("Top P", "Top P"),
		Map.entry("Torchflowers", "火把花"),
		Map.entry("Totems", "图腾"),
		Map.entry("Tracer", "轨迹线"),
		Map.entry("Tracer color", "轨迹线颜色"),
		Map.entry("Trap chest color", "陷阱箱颜色"),
		Map.entry("Trident yeet mode", "三叉戟投掷模式"),
		Map.entry("Turn off while flying", "飞行时关闭"),
		Map.entry("Twerk speed", "Twerk速度"),
		Map.entry("Twisting Vines", "缠怨藤"),
		Map.entry("Unlimited range", "无限范围"),
		Map.entry("Upward Speed", "上升速度"),
		Map.entry("Use AI", "使用AI"),
		Map.entry("Use AI (experimental)", "使用AI(实验性)"),
		Map.entry("Use hands", "使用手"),
		Map.entry("Use swords", "使用剑"),
		Map.entry("Valid range", "有效范围"),
		Map.entry("Vertical Strength", "垂直力度"),
		Map.entry("Wait time", "等待时间"),
		Map.entry("Weeping Vines", "哭泣藤"),
		Map.entry("Wheat", "小麦"),
		Map.entry("Zoom level", "缩放级别"),
		Map.entry("mcMMO limit", "mcMMO限制"),
		Map.entry("mcMMO mode", "mcMMO模式"),
		Map.entry("mcMMO range", "mcMMO范围"),
		Map.entry("mcMMO range bug", "mcMMO范围bug")
	);
	
	private final String name;
	private final WText description;
	
	public Setting(String name, WText description)
	{
		this.name = Objects.requireNonNull(name);
		this.description = Objects.requireNonNull(description);
	}
	
	public final String getName()
	{
		return name;
	}
	
	/**
	 * @return 用于界面显示的名称（硬编码中文）
	 */
	public String getDisplayName()
	{
		return CN_NAMES.getOrDefault(name, name);
	}
	
	public final String getDescription()
	{
		return description.toString();
	}
	
	public final String getWrappedDescription(int width)
	{
		return ChatUtils.wrapText(getDescription(), width);
	}
	
	public abstract Component getComponent();
	
	public abstract void fromJson(JsonElement json);
	
	public abstract JsonElement toJson();
	
	public abstract JsonObject exportWikiData();
	
	public void update()
	{
		
	}
	
	public abstract Set<PossibleKeybind> getPossibleKeybinds(
		String featureName);
}
