package edu.byu.minecraft.cat.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import edu.byu.minecraft.cat.CivsAndTitles;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public record Config(DatabaseType database, boolean enable_advancement_awards, boolean modify_display_name) {
    public enum DatabaseType {
        Postgres("postgres"),
        SqLite("sqlite"),
        None("none");

        final String type;
        public static final Codec<DatabaseType> CODEC = Codec.STRING.comapFlatMap(
                DatabaseType::validate,
                DatabaseType::getType
        );

        DatabaseType(String type) {
            this.type = type;
        }

        String getType() {
            return this.type;
        }

        static DataResult<DatabaseType> validate(String type) {
            DatabaseType out = null;
            switch (type) {
                case "sqlite" -> out = DatabaseType.SqLite;
                case "postgres" -> out = DatabaseType.Postgres;
                case "none" -> out = DatabaseType.None;
            }
            if (out == null) {
                return DataResult.error(() -> "Invalid database type " + type);
            } else {
                return DataResult.success(out);
            }
        }
    }

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DatabaseType.CODEC.fieldOf("database").forGetter(Config::database),
            Codec.BOOL.fieldOf("enable_advancement_rewards").forGetter(Config::enable_advancement_awards),
            Codec.BOOL.fieldOf("modify_display_name").forGetter(Config::modify_display_name)
    ).apply(instance, Config::new));

    private static final String CONFIG_FILE = "config.json";

    private static final String DEFAULT_FILE = """
{
  "database": "sqlite",
  "enable_advancement_rewards": true,
  "modify_display_name": false
}
""";

    private static final Config DEFAULT_CONFIG = new Config(DatabaseType.SqLite, true, false);

    public static Config loadOrCreate() {
        Gson gson = new Gson();
        File config = CivsAndTitles.getPath(CONFIG_FILE);
        if (!config.exists()) {
            CivsAndTitles.LOGGER.info("Mod config not found, generating file");
            try (FileWriter writer = new FileWriter(config)) {
                writer.write(DEFAULT_FILE);
            } catch (IOException i) {
                CivsAndTitles.LOGGER.error("There was an error writing the config file");
            }
            return DEFAULT_CONFIG;
        }
        try (FileReader reader = new FileReader(config)) {
            return CODEC.parse(JsonOps.INSTANCE, gson.fromJson(reader, JsonObject.class)).getOrThrow();
        } catch (IOException i) {
            CivsAndTitles.LOGGER.error("There was an error reading the config file. Using default config");
            return DEFAULT_CONFIG;
        } catch (IllegalStateException e) {
            CivsAndTitles.LOGGER.error("Malformed config file found. Using default config");
            return DEFAULT_CONFIG;
        }
    }
}
