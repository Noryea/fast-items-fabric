package cn.noryea.fastitems;

import cn.noryea.fastitems.config.FastItemsConfig;
import net.fabricmc.api.ClientModInitializer;

public class FastItemsClient implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final String MOD_ID = "fastitems";

	@Override
	public void onInitializeClient() {
		FastItemsConfig.init(MOD_ID, FastItemsConfig.class);
	}
}