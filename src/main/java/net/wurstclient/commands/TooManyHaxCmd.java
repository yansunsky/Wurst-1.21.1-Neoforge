/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.wurstclient.DontBlock;
import net.wurstclient.Feature;
import net.wurstclient.command.CmdError;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.hack.Hack;
import net.wurstclient.hacks.TooManyHaxHack;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.MathUtils;
import net.wurstclient.util.json.JsonException;

@DontBlock
public final class TooManyHaxCmd extends Command
{
	public TooManyHaxCmd()
	{
		super("toomanyhax",
			"允许管理在 TooManyHax 启用时应阻止哪些功能。\n"
				+ "（在 TooManyHax 启用时生效。）",
			".toomanyhax block <feature>", ".toomanyhax unblock <feature>",
			".toomanyhax block-all", ".toomanyhax unblock-all",
			".toomanyhax list [<page>]", ".toomanyhax load-profile <file>",
			".toomanyhax save-profile <file>",
			".toomanyhax list-profiles [<page>]",
			"配置方案保存在 '.minecraft/wurst/toomanyhax'。");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		switch(args[0].toLowerCase())
		{
			case "block":
			block(args);
			break;
			
			case "unblock":
			unblock(args);
			break;
			
			case "block-all":
			blockAll();
			break;
			
			case "unblock-all":
			unblockAll();
			break;
			
			case "list":
			list(args);
			break;
			
			case "load-profile":
			loadProfile(args);
			break;
			
			case "save-profile":
			saveProfile(args);
			break;
			
			case "list-profiles":
			listProfiles(args);
			break;
			
			default:
			throw new CmdSyntaxError();
		}
	}
	
	private void block(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		String name = args[1];
		Feature feature = parseFeature(name);
		String typeAndName = getType(feature) + " '" + name + "'";
		
		if(!feature.isSafeToBlock())
			throw new CmdError("阻止 " + typeAndName + " 不安全。");
		
		TooManyHaxHack tooManyHax = WURST.getHax().tooManyHaxHack;
		if(tooManyHax.isBlocked(feature))
		{
			ChatUtils.error("" + typeAndName + " 已被阻止。");
			
			if(!tooManyHax.isEnabled())
				ChatUtils.message("启用 TooManyHax 以查看效果。");
			
			return;
		}
		
		tooManyHax.setBlocked(feature, true);
		ChatUtils.message("已将 " + typeAndName + " 添加到 TooManyHax 列表。");
	}
	
	private void unblock(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		String name = args[1];
		Feature feature = parseFeature(name);
		String typeAndName = getType(feature) + " '" + name + "'";
		
		TooManyHaxHack tooManyHax = WURST.getHax().tooManyHaxHack;
		if(!tooManyHax.isBlocked(feature))
			throw new CmdError("未阻止 " + typeAndName + "。");
		
		tooManyHax.setBlocked(feature, false);
		ChatUtils.message("已从 TooManyHax 列表移除 " + typeAndName + "。");
	}
	
	private void blockAll()
	{
		WURST.getHax().tooManyHaxHack.blockAll();
		ChatUtils.message("已阻止所有*功能。");
		ChatUtils
			.message("*Note: A few features cannot be blocked because they");
		ChatUtils.message("是 Wurst 正常运行所必需的。");
	}
	
	private void unblockAll()
	{
		WURST.getHax().tooManyHaxHack.unblockAll();
		ChatUtils.message("已取消阻止所有功能。");
	}
	
	private Feature parseFeature(String name) throws CmdSyntaxError
	{
		Feature feature = WURST.getFeatureByName(name);
		if(feature == null)
			throw new CmdSyntaxError(
				"A feature named '" + name + "' could not be found");
		
		return feature;
	}
	
	private String getType(Feature feature)
	{
		if(feature instanceof Hack)
			return "hack";
		
		if(feature instanceof Command)
			return "command";
		
		if(feature instanceof OtherFeature)
			return "feature";
		
		throw new IllegalStateException();
	}
	
	private void list(String[] args) throws CmdException
	{
		if(args.length > 2)
			throw new CmdSyntaxError();
		
		TooManyHaxHack tooManyHax = WURST.getHax().tooManyHaxHack;
		List<Feature> blocked = tooManyHax.getBlockedFeatures();
		int page = parsePage(args);
		int pages = (int)Math.ceil(blocked.size() / 8.0);
		pages = Math.max(pages, 1);
		
		if(page > pages || page < 1)
			throw new CmdSyntaxError("无效页码：" + page);
		
		String total = "Total: " + blocked.size() + " blocked feature";
		total += blocked.size() != 1 ? "s" : "";
		ChatUtils.message(total);
		
		int start = (page - 1) * 8;
		int end = Math.min(page * 8, blocked.size());
		
		ChatUtils.message("TooManyHax 列表（第 " + page + "/" + pages + " 页）");
		for(int i = start; i < end; i++)
			ChatUtils.message(blocked.get(i).getName());
	}
	
	private int parsePage(String[] args) throws CmdSyntaxError
	{
		if(args.length < 2)
			return 1;
		
		if(!MathUtils.isInteger(args[1]))
			throw new CmdSyntaxError("不是数字：" + args[1]);
		
		return Integer.parseInt(args[1]);
	}
	
	private void loadProfile(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		String name = parseFileName(args[1]);
		
		try
		{
			WURST.getHax().tooManyHaxHack.loadProfile(name);
			ChatUtils.message("TooManyHax 配置方案已加载：" + name);
			
		}catch(NoSuchFileException e)
		{
			throw new CmdError("配置方案 '" + name + "' 不存在。");
			
		}catch(JsonException e)
		{
			e.printStackTrace();
			throw new CmdError(
				"配置方案 '" + name + "' 已损坏：" + e.getMessage());
			
		}catch(IOException e)
		{
			e.printStackTrace();
			throw new CmdError("无法加载配置方案：" + e.getMessage());
		}
	}
	
	private void saveProfile(String[] args) throws CmdException
	{
		if(args.length != 2)
			throw new CmdSyntaxError();
		
		String name = parseFileName(args[1]);
		
		try
		{
			WURST.getHax().tooManyHaxHack.saveProfile(name);
			ChatUtils.message("TooManyHax 配置方案已保存：" + name);
			
		}catch(IOException | JsonException e)
		{
			e.printStackTrace();
			throw new CmdError("无法保存配置方案：" + e.getMessage());
		}
	}
	
	private String parseFileName(String input)
	{
		String fileName = input;
		if(!fileName.endsWith(".json"))
			fileName += ".json";
		
		return fileName;
	}
	
	private void listProfiles(String[] args) throws CmdException
	{
		if(args.length > 2)
			throw new CmdSyntaxError();
		
		ArrayList<Path> files = WURST.getHax().tooManyHaxHack.listProfiles();
		int page = parsePage(args);
		int pages = (int)Math.ceil(files.size() / 8.0);
		pages = Math.max(pages, 1);
		
		if(page > pages || page < 1)
			throw new CmdSyntaxError("无效页码：" + page);
		
		String total = "Total: " + files.size() + " profile";
		total += files.size() != 1 ? "s" : "";
		ChatUtils.message(total);
		
		int start = (page - 1) * 8;
		int end = Math.min(page * 8, files.size());
		
		ChatUtils.message(
			"TooManyHax profile list (page " + page + "/" + pages + ")");
		for(int i = start; i < end; i++)
			ChatUtils.message(files.get(i).getFileName().toString());
	}
}
