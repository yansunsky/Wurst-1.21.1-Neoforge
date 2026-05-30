package net.wurstclient;

import net.neoforged.fml.common.Mod;

@Mod(WurstInitializer.MODID)
public final class WurstInitializer
{
	public static final String MODID = "wurst";

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
