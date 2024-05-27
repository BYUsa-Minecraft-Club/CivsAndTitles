package edu.byu.minecraft.cat;

import edu.byu.minecraft.cat.commands.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CivsAndTitles implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("civsandtitles");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!");
		CommandRegistrationCallback.EVENT.register(TitleCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(CivCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(BuildCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(CivLeaderCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(AdminCommands::registerCommands);
	}
}