/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;

@SearchTags({"name tags"})
public final class NameTagsHack extends Hack
{
	private final SliderSetting scale =
		new SliderSetting("Scale", "名牌应有多大。", 1, 0.05,
			5, 0.05, SliderSetting.ValueDisplay.PERCENTAGE);
	
	private final CheckboxSetting unlimitedRange =
		new CheckboxSetting("Unlimited range",
			"移除名牌的 64 格距离限制。", true);
	
	private final CheckboxSetting seeThrough = new CheckboxSetting(
		"See-through mode",
		"在透视文字层上渲染名牌。这使得它们"
			+ "在墙后更容易阅读，但会导致一些图形故障"
			+ "，比如水和其它透明物体。",
		false);
	
	private final CheckboxSetting forceMobNametags = new CheckboxSetting(
		"始终显示有名字的生物", "即使你没有直接看着它们，也会显示"
			+ "有名字的生物的名牌。",
		true);
	
	private final CheckboxSetting forcePlayerNametags =
		new CheckboxSetting("Always show player names",
			"显示你自己的名牌以及任何"
				+ "原本会被记分板队伍设置禁用的名牌。",
			false);
	
	public NameTagsHack()
	{
		super("NameTags");
		setCategory(Category.RENDER);
		addSetting(scale);
		addSetting(unlimitedRange);
		addSetting(seeThrough);
		addSetting(forceMobNametags);
		addSetting(forcePlayerNametags);
	}
	
	public float getScale()
	{
		return scale.getValueF();
	}
	
	public boolean isUnlimitedRange()
	{
		return isEnabled() && unlimitedRange.isChecked();
	}
	
	public boolean isSeeThrough()
	{
		return isEnabled() && seeThrough.isChecked();
	}
	
	public boolean shouldForceMobNametags()
	{
		return isEnabled() && forceMobNametags.isChecked();
	}
	
	public boolean shouldForcePlayerNametags()
	{
		return isEnabled() && forcePlayerNametags.isChecked();
	}
	
	// See EntityRendererMixin.wurstRenderLabelIfPresent(),
	// LivingEntityRendererMixin, MobEntityRendererMixin
}
