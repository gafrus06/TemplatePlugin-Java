package ru.joutak.template.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.joutak.template.atm.Atm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class YamlAtmRepository implements AtmRepository {

    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration configuration;

    public YamlAtmRepository(JavaPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        this.file = new File(
                plugin.getDataFolder(),
                "atms.yml"
        );

        this.configuration =
                YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save(Atm atm) {
        String path = "atms." + atm.id();

        configuration.set(
                path + ".world",
                atm.worldId().toString()
        );

        configuration.set(
                path + ".barrel.x",
                atm.barrelX()
        );
        configuration.set(
                path + ".barrel.y",
                atm.barrelY()
        );
        configuration.set(
                path + ".barrel.z",
                atm.barrelZ()
        );

        configuration.set(
                path + ".sign.x",
                atm.signX()
        );
        configuration.set(
                path + ".sign.y",
                atm.signY()
        );
        configuration.set(
                path + ".sign.z",
                atm.signZ()
        );

        saveFile();
    }

    @Override
    public void delete(UUID atmId) {
        configuration.set(
                "atms." + atmId,
                null
        );

        saveFile();
    }

    @Override
    public Optional<Atm> findById(UUID atmId) {
        String path = "atms." + atmId;

        if (!configuration.contains(path)) {
            return Optional.empty();
        }

        return Optional.of(readAtm(path, atmId));
    }

    @Override
    public Collection<Atm> findAll() {
        Collection<Atm> result = new ArrayList<>();

        ConfigurationSection section =
                configuration.getConfigurationSection("atms");

        if (section == null) {
            return result;
        }

        for (String idString : section.getKeys(false)) {

            try {
                UUID id = UUID.fromString(idString);

                result.add(
                        readAtm(
                                "atms." + idString,
                                id
                        )
                );

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(
                        "Некорректный ATM id: " + idString
                );
            }
        }

        return result;
    }

    private Atm readAtm(
            String path,
            UUID id
    ) {
        UUID worldId = UUID.fromString(
                configuration.getString(
                        path + ".world"
                )
        );

        return new Atm(
                id,
                worldId,

                configuration.getInt(
                        path + ".barrel.x"
                ),
                configuration.getInt(
                        path + ".barrel.y"
                ),
                configuration.getInt(
                        path + ".barrel.z"
                ),

                configuration.getInt(
                        path + ".sign.x"
                ),
                configuration.getInt(
                        path + ".sign.y"
                ),
                configuration.getInt(
                        path + ".sign.z"
                )
        );
    }

    private void saveFile() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Не удалось сохранить atms.yml",
                    e
            );
        }
    }
}