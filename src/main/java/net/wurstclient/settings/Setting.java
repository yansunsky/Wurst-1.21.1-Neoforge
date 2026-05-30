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
	private static final Map<String, String> CN_NAMES = cnNames();
	
	private static Map<String, String> cnNames()
	{
		var m = new java.util.LinkedHashMap<String, String>();
		m.put("AI range", "AI范围");
		m.put("Accent", "强调色");
		m.put("Aim while blocking", "格挡时自瞄");
		m.put("All GUIs", "全部GUI");
		m.put("Allow ClickGUI", "允许菜单界面");
		m.put("Allow chorus fruit", "允许紫颂果");
		m.put("Allow hunger effect", "允许饥饿效果");
		m.put("Allow jump key", "允许跳跃键");
		m.put("Allow offhand", "允许副手");
		m.put("Allow other screens", "允许其他界面");
		m.put("Allow poison effect", "允许中毒效果");
		m.put("Allow sneak key", "允许潜行键");
		m.put("Allow sprint key", "允许疾跑键");
		m.put("Altitude", "高度");
		m.put("Always FastPlace", "始终快速放置");
		m.put("Always show player names", "始终显示玩家名");
		m.put("Amethyst", "紫水晶");
		m.put("Amount", "数量");
		m.put("Animations", "动画");
		m.put("Anti-Kick", "防踢");
		m.put("Anti-Kick Interval", "防踢间隔");
		m.put("Area", "区域");
		m.put("Attack while blocking", "格挡时攻击");
		m.put("Auto-place anchors", "自动放置锚点");
		m.put("Background", "背景");
		m.put("Bamboo", "竹子");
		m.put("Barrel color", "木桶颜色");
		m.put("Beetroots", "甜菜根");
		m.put("Block", "方块");
		m.put("Block Hit Color", "方块击中颜色");
		m.put("Blocking offset", "格挡偏移");
		m.put("Blocks", "方块列表");
		m.put("Cactus", "仙人掌");
		m.put("Carrots", "胡萝卜");
		m.put("Catch delay", "捕捉延迟");
		m.put("Change moon phase", "更改月相");
		m.put("Change world time", "更改世界时间");
		m.put("Check held item", "检查手持物品");
		m.put("Check line of sight", "检查视线");
		m.put("Chest boat color", "运输船颜色");
		m.put("Chest cart color", "运输矿车颜色");
		m.put("Chest color", "箱子颜色");
		m.put("Chorus plants", "紫颂植物");
		m.put("Cocoa", "可可豆");
		m.put("Cocoa beans", "可可豆");
		m.put("Color", "颜色");
		m.put("Command", "命令");
		m.put("Crafter color", "合成器颜色");
		m.put("Crops", "农作物");
		m.put("DD color", "DD颜色");
		m.put("Day color", "白天颜色");
		m.put("Death screen button", "死亡界面按钮");
		m.put("Debug draw", "调试绘制");
		m.put("Debug mode", "调试模式");
		m.put("Delay", "延迟");
		m.put("Depth test", "深度测试");
		m.put("Detect received language", "检测接收语言");
		m.put("Disable Freecam", "禁用自由视角");
		m.put("Disable rain", "禁用下雨");
		m.put("Disable signatures", "禁用签名");
		m.put("Disable telemetry", "禁用遥测");
		m.put("Dispenser color", "发射器颜色");
		m.put("Distance", "距离");
		m.put("Draw blocks to harvest", "绘制待收获方块");
		m.put("Draw blocks to replant", "绘制待补种方块");
		m.put("Draw distance", "绘制距离");
		m.put("Draw replanting spots", "绘制补种位置");
		m.put("Dropper color", "投掷器颜色");
		m.put("ESP color", "透视颜色");
		m.put("Eat while walking", "行走时进食");
		m.put("Edge distance", "边缘距离");
		m.put("End gateway color", "末地折跃门颜色");
		m.put("End portal color", "末地传送门颜色");
		m.put("End portal frame color", "末地传送门框架颜色");
		m.put("Ender color", "末影箱颜色");
		m.put("Entity Hit Color", "实体击中颜色");
		m.put("FOV", "视野");
		m.put("Fade", "淡出效果");
		m.put("Fall speed", "下落速度");
		m.put("Filter server messages", "过滤服务器消息");
		m.put("Filter untamed", "过滤未驯服");
		m.put("Flat mode", "平面模式");
		m.put("Frequency penalty", "频率惩罚");
		m.put("Furnace color", "熔炉颜色");
		m.put("Glow berries", "发光浆果");
		m.put("Guide", "指南");
		m.put("Guide color", "指南颜色");
		m.put("Health", "生命值");
		m.put("Height", "高度");
		m.put("Height control", "高度控制");
		m.put("Hopper cart color", "漏斗矿车颜色");
		m.put("Hopper color", "漏斗颜色");
		m.put("Horizontal strength", "水平力度");
		m.put("Hungry sprint", "饥饿疾跑");
		m.put("ID", "ID");
		m.put("Ignore errors", "忽略错误");
		m.put("Ignore mouse input", "忽略鼠标输入");
		m.put("Include barrels", "包含木桶");
		m.put("Include chest boats", "包含运输船");
		m.put("Include chest carts", "包含运输矿车");
		m.put("Include crafters", "包含合成器");
		m.put("Include dispensers", "包含发射器");
		m.put("Include droppers", "包含投掷器");
		m.put("Include end gateways", "包含末地折跃门");
		m.put("Include end portal frames", "包含末地传送门框架");
		m.put("Include end portals", "包含末地传送门");
		m.put("Include ender chests", "包含末影箱");
		m.put("Include furnaces", "包含熔炉");
		m.put("Include hopper carts", "包含漏斗矿车");
		m.put("Include hoppers", "包含漏斗");
		m.put("Include nether portals", "包含下界传送门");
		m.put("Include normal chests", "包含普通箱子");
		m.put("Include pots", "包含花盆");
		m.put("Include shulkers", "包含潜影盒");
		m.put("Include trap chests", "包含陷阱箱");
		m.put("Indicator", "指示器");
		m.put("Injury threshold", "伤害阈值");
		m.put("Items", "物品列表");
		m.put("Kelp", "海带");
		m.put("Keybind", "按键绑定");
		m.put("Legit mode", "合法模式");
		m.put("Limit", "限制");
		m.put("Lock ID", "锁定ID");
		m.put("Log chunks", "记录区块");
		m.put("Max attempts", "最大尝试次数");
		m.put("Max height", "最大高度");
		m.put("Max settings height", "设置最大高度");
		m.put("Max suggestions per draft", "每稿最大建议数");
		m.put("Max tokens", "最大令牌数");
		m.put("Max vein size", "最大矿脉大小");
		m.put("Melons", "西瓜");
		m.put("Middle click friends", "中键添加好友");
		m.put("Min depth", "最小深度");
		m.put("Min height", "最小高度");
		m.put("Min hunger", "最小饥饿值");
		m.put("Miss color", "未命中颜色");
		m.put("Moon phase", "月相");
		m.put("Move speed", "移动速度");
		m.put("Multitill", "多格耕地");
		m.put("Nether warts", "下界疣");
		m.put("Nether portal color", "下界传送门颜色");
		m.put("New chunks color", "新区块颜色");
		m.put("Night color", "夜晚颜色");
		m.put("NoCheat+ bypass", "NoCheat+绕过");
		m.put("Non-AI range", "非AI范围");
		m.put("Non-blocking offset", "非格挡偏移");
		m.put("Offset", "偏移");
		m.put("Old chunks color", "旧区块颜色");
		m.put("Omnidirectional sprint", "全方向疾跑");
		m.put("Opacity", "不透明度");
		m.put("OpenAI legacy endpoint", "OpenAI旧版接口");
		m.put("Ores", "矿石");
		m.put("Other", "其他");
		m.put("Particles", "粒子效果");
		m.put("Patience", "耐心");
		m.put("Pause for mace", "为重锤暂停");
		m.put("Pause when sneaking", "潜行时暂停");
		m.put("Pitcher plants", "瓶子草");
		m.put("Place torches", "放置火把");
		m.put("Potatoes", "马铃薯");
		m.put("Pots color", "花盆颜色");
		m.put("Power", "力度");
		m.put("Presence penalty", "存在惩罚");
		m.put("Pumpkins", "南瓜");
		m.put("RC mode", "RC模式");
		m.put("Radius", "半径");
		m.put("Range", "范围");
		m.put("Release time", "释放时间");
		m.put("Repair mode", "修复模式");
		m.put("Retry delay", "重试延迟");
		m.put("Reverse sorting", "反向排序");
		m.put("Reverse steal order", "反向偷取顺序");
		m.put("Rotate with player", "随玩家旋转");
		m.put("Rotation speed", "旋转速度");
		m.put("Saplings", "树苗");
		m.put("Scale", "缩放");
		m.put("Show counter", "显示计数");
		m.put("Show wait time", "显示等待时间");
		m.put("Shulker color", "潜影盒颜色");
		m.put("Slot", "槽位");
		m.put("Sneak at edges", "边缘潜行");
		m.put("Sound", "音效");
		m.put("Speed", "速度");
		m.put("Speed randomization", "速度随机化");
		m.put("Spoof vanilla", "伪装原版");
		m.put("Stack size", "堆叠大小");
		m.put("Steal/Store buttons", "偷取/存放按钮");
		m.put("Stems", "茎秆");
		m.put("Stop flying in water", "水中停止飞行");
		m.put("Strength", "强度");
		m.put("Sugar cane", "甘蔗");
		m.put("Super fast mode", "超快模式");
		m.put("Sweet berries", "甜浆果");
		m.put("Temperature", "温度");
		m.put("Template", "模板");
		m.put("Text", "文本");
		m.put("Time", "时间");
		m.put("Tooltip opacity", "提示不透明度");
		m.put("Top P", "Top P");
		m.put("Torchflowers", "火把花");
		m.put("Totems", "图腾");
		m.put("Tracer", "轨迹线");
		m.put("Tracer color", "轨迹线颜色");
		m.put("Trap chest color", "陷阱箱颜色");
		m.put("Trident yeet mode", "三叉戟投掷模式");
		m.put("Turn off while flying", "飞行时关闭");
		m.put("Twerk speed", "Twerk速度");
		m.put("Twisting vines", "缠怨藤");
		m.put("Unlimited range", "无限范围");
		m.put("Upward speed", "上升速度");
		m.put("Use AI", "使用AI");
		m.put("Use AI (experimental)", "使用AI(实验性)");
		m.put("Use hands", "使用手");
		m.put("Use swords", "使用剑");
		m.put("Valid range", "有效范围");
		m.put("Vertical strength", "垂直力度");
		m.put("Wait time", "等待时间");
		m.put("Weeping vines", "哭泣藤");
		m.put("Wheat", "小麦");
		m.put("Zoom level", "缩放级别");
		m.put("mcMMO limit", "mcMMO限制");
		m.put("mcMMO mode", "mcMMO模式");
		m.put("mcMMO range", "mcMMO范围");
		m.put("mcMMO range bug", "mcMMO范围bug");
		return Map.copyOf(m);
	}
	
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
	
	/**
	 * Exports this setting's data to a {@link JsonObject} for use in the
	 * Wurst Wiki. Must always specify the following properties:
	 * <ul>
	 * <li>name
	 * <li>description
	 * <li>type
	 * </ul>
	 */
	public abstract JsonObject exportWikiData();
	
	public void update()
	{
		
	}
	
	public abstract Set<PossibleKeybind> getPossibleKeybinds(
		String featureName);
}
