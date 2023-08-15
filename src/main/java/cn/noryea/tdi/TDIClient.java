package cn.noryea.tdi;

import cn.noryea.tdi.config.TDIConfig;
import net.fabricmc.api.ClientModInitializer;

public class TDIClient implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	@Override
	public void onInitializeClient() {
		TDIConfig.init("tdi", TDIConfig.class);
	}
}