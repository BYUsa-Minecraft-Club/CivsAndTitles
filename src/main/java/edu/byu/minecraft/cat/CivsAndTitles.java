package edu.byu.minecraft.cat;

import edu.byu.minecraft.cat.commands.*;
import edu.byu.minecraft.cat.dataaccess.DataAccess;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.sqlite.SqliteDataAccess;
import edu.byu.minecraft.cat.model.Player;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CivsAndTitles implements ModInitializer {
	public static final String MOD_ID = "civsandtitles";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static DataAccess dataAccess;

	public static DataAccess getDataAccess() {
		return dataAccess;
	}

	private static void setDataAccess(DataAccess dataAccess) {
		CivsAndTitles.dataAccess = dataAccess;
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

        try {
            setDataAccess(new SqliteDataAccess());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Hello Fabric world!");
		CommandRegistrationCallback.EVENT.register(TitleCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(CivCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(BuildCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(CivLeaderCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(AdminCommands::registerCommands);

		ServerPlayConnectionEvents.JOIN.register(this::playerJoinCallback);
	}

	private void playerJoinCallback(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender,
									MinecraftServer minecraftServer) {
		try {
			ServerPlayerEntity serverPlayer = serverPlayNetworkHandler.getPlayer();
			Player dbPlayer = getDataAccess().getPlayerDAO().get(serverPlayer.getUuid());
			if (dbPlayer == null) {
				dbPlayer = new Player(serverPlayer.getUuid(), serverPlayer.getNameForScoreboard(), 0, "New Player",
						Player.Role.PLAYER, true);
				getDataAccess().getPlayerDAO().insert(dbPlayer);
			}
			else if (!serverPlayer.getNameForScoreboard().equals(dbPlayer.name())) {
				dbPlayer = new Player(serverPlayer.getUuid(), serverPlayer.getNameForScoreboard(), dbPlayer.points(),
						dbPlayer.title(), dbPlayer.role(), dbPlayer.showRank());
				getDataAccess().getPlayerDAO().update(dbPlayer);
			}
		} catch (DataAccessException e) {
			LOGGER.error("Data access error on player join", e);
		}
	}
}