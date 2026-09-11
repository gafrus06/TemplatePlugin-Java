package ru.joutak.template.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class YamlAccountRepository implements AccountRepository {

    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration configuration;

    public YamlAccountRepository(JavaPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.file = new File(
                plugin.getDataFolder(),
                "accounts.yml"
        );

        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public long getBalance(UUID playerId) {
        String path = "players." + playerId + ".balance";

        return configuration.getLong(path, 0L);
    }

    @Override
    public void setBalance(UUID playerId, long balance) {
        String path = "players." + playerId + ".balance";

        configuration.set(path, balance);

        save();
    }

    private void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe(
                    "Не удалось сохранить accounts.yml: " + e.getMessage()
            );

            throw new IllegalStateException(
                    "Не удалось сохранить баланс игрока",
                    e
            );
        }
    }
}