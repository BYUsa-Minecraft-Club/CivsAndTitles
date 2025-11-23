package edu.byu.minecraft.cat.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import edu.byu.minecraft.cat.CivsAndTitles;

import java.io.*;

public record PostgresConfig(String url, Integer port, String database, String username, String password) {
    public static Codec<PostgresConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("host").forGetter(PostgresConfig::url),
            Codec.INT.fieldOf("port").forGetter(PostgresConfig::port),
            Codec.STRING.fieldOf("database").forGetter(PostgresConfig::database),
            Codec.STRING.fieldOf("username").forGetter(PostgresConfig::username),
            Codec.STRING.fieldOf("password").forGetter(PostgresConfig::password)
    ).apply(instance, PostgresConfig::new));

    private static final String CONFIG_FILE = "postgres.json";

    private static final String DEFAULT_FILE = """
{
  "host": "localhost",
  "port": 5432,
  "database": "titles",
  "username": "postgres",
  "password": "postgres"
}
""";
    private static final PostgresConfig DEFAULT_CONFIG = new PostgresConfig("localhost",5432,"titles","postgres","postgres");

    public static PostgresConfig loadOrCreate() {
        Gson gson = new Gson();
        File config = CivsAndTitles.getPath(CONFIG_FILE);
        if (!config.exists()) {
            CivsAndTitles.LOGGER.info("Postgres config not found, generating file");
            CivsAndTitles.LOGGER.warn("Make sure to configure minecraft and your database!");
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
            CivsAndTitles.LOGGER.error("Malformed postgres config file found. Using default config");
            return DEFAULT_CONFIG;
        }
    }

    public String toJdbcUrl() {
        return "jdbc:postgresql://"+url+":"+port.toString()+"/"+database;
    }
}
