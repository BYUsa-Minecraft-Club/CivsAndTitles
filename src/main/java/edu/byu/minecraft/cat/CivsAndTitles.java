package edu.byu.minecraft.cat;

import edu.byu.minecraft.cat.commands.*;
import edu.byu.minecraft.cat.config.Config;
import edu.byu.minecraft.cat.config.PostgresConfig;
import edu.byu.minecraft.cat.dataaccess.DataAccess;
import edu.byu.minecraft.cat.dataaccess.DataAccessException;
import edu.byu.minecraft.cat.dataaccess.none.NoneDataAccess;
import edu.byu.minecraft.cat.dataaccess.postgres.PostgresDataAccess;
import edu.byu.minecraft.cat.dataaccess.sqlite.SqliteDataAccess;
import edu.byu.minecraft.cat.model.Player;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.util.AsyncUtilities;
import edu.byu.minecraft.cat.util.TitleUtilities;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.PlaceholderResult;


public class CivsAndTitles implements ModInitializer {
	public static final String MOD_ID = "civsandtitles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static DataAccess dataAccess;
    private static Config config;
    private static final Path FOLDER = Paths.get(String.format("config/%s/", MOD_ID));

    public static File getPath(String file) {
        try {
            Files.createDirectories(FOLDER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return FOLDER.resolve(file).toFile();
    }

	public static DataAccess getDataAccess() {
		return dataAccess;
	}

	private static void setDataAccess(DataAccess dataAccess) {
		CivsAndTitles.dataAccess = dataAccess;
	}

    public static boolean advancementsEnabled() { return config.enable_advancement_awards(); }

    public static boolean playerMixinEnabled() { return config.modify_display_name(); }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("titles", path);
    }

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

        if (!FOLDER.toFile().exists() && !FOLDER.toFile().mkdir()) {
            CivsAndTitles.LOGGER.error("Couldn't create config folder: {}", FOLDER);
        }

        config = Config.loadOrCreate();
        PostgresConfig pgconfig = PostgresConfig.loadOrCreate(); // We want to create this file regardless

        try {
            switch (config.database()) {
                case Config.DatabaseType.Postgres -> setDataAccess(new PostgresDataAccess(pgconfig));
                case Config.DatabaseType.SqLite -> setDataAccess(new SqliteDataAccess());
                case Config.DatabaseType.None -> setDataAccess(new NoneDataAccess());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
		CommandRegistrationCallback.EVENT.register(TitleCommands::registerCommands);
		CommandRegistrationCallback.EVENT.register(AdminCommands::registerCommands);

		ServerPlayConnectionEvents.JOIN.register(this::playerJoinCallback);
        ServerPlayConnectionEvents.DISCONNECT.register(this::playerLeaveCallback);
        Placeholders.registerServer(
                id("title"),
                (ctx, arg) -> {
                    net.minecraft.world.entity.player.Player serverPlayer = ctx.player();
					if (serverPlayer == null)
						return PlaceholderResult.invalid("No player!");
					Title title = TitleUtilities.getCache(serverPlayer.getUUID());
                    if (title == null) {
                        return PlaceholderResult.value("None ");
                    } else {
                        return PlaceholderResult.value(title.format().copy().append(" "));
                    }
				}
		);
	}

    private void playerJoinCallback(
            ServerGamePacketListenerImpl packetListener,
            PacketSender packetSender,
            MinecraftServer minecraftServer
    ) {
        ServerPlayer serverPlayer = packetListener.getPlayer();
        AsyncUtilities.performAsync(minecraftServer,
                () -> {

                    try {
                        Player dbPlayer = getDataAccess().getPlayerDAO().get(serverPlayer.getUUID());
                        if (dbPlayer == null) {
                            dbPlayer = new Player(serverPlayer.getUUID(), serverPlayer.getGameProfile().name(), null);
                            getDataAccess().getPlayerDAO().insert(dbPlayer);
                        } else if (!serverPlayer.getGameProfile().name().equals(dbPlayer.name())) {
                            dbPlayer = new Player(serverPlayer.getUUID(), serverPlayer.getGameProfile().name(),
                                    dbPlayer.title());
                            getDataAccess().getPlayerDAO().update(dbPlayer);
                        }
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e);
                    }
                    TitleUtilities.updateCache(serverPlayer.getUUID());
                },
                error -> LOGGER.error("Database error while processing player join: ", error),
                error -> LOGGER.error("Unknown error while processing player join: ", error));
	}

    private void playerLeaveCallback(
            ServerGamePacketListenerImpl serverPlayNetworkHandler,
            MinecraftServer minecraftServer
    ) {
        ServerPlayer player = serverPlayNetworkHandler.getPlayer();
        TitleUtilities.removeCache(player.getUUID());
    }
}