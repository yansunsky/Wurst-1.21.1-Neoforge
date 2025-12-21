package net.wurstclient;

import net.neoforged.fml.common.Mod;

@Mod("sloth1")
public final class WurstInitializer
{
	private static boolean initialized;

	public WurstInitializer()
	{
		if(initialized)
			throw new RuntimeException(
					"WurstInitializer constructor ran twice!");

		WurstClient.INSTANCE.initialize();
		initialized = true;
	}
}
