/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.altmanager;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.client.User;
import net.minecraft.core.UUIDUtil;
import net.wurstclient.WurstClient;

public enum LoginManager
{
	;
	
	public static void changeCrackedName(String newName)
	{
		String accessToken = "";
		UUID uuid = UUIDUtil.createOfflinePlayerUUID(newName);

		User session = new User(
				newName,
				uuid,
				accessToken,
				Optional.empty(),
				Optional.empty(),
				User.Type.LEGACY // 6번째 인자 추가
		);
		
		WurstClient.IMC.setWurstSession(session);
	}
}
