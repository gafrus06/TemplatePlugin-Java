package ru.joutak.template.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.joutak.template.bank.Transaction;
import ru.joutak.template.bank.TransactionType;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;


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
    public void applyTransaction(
            UUID playerId,
            long newBalance,
            Transaction transaction
    ) {

        if (newBalance < 0) {
            throw new IllegalArgumentException(
                    "Баланс не может быть отрицательным"
            );
        }

        String playerPath =
                "players." + playerId;
        String balancePath =
                playerPath + ".balance";
        String transactionsPath =
                playerPath + ".transactions";
        Object oldBalance =
                configuration.get(balancePath);

        List<Map<?, ?>> oldTransactions =
                new ArrayList<>(
                        configuration.getMapList(
                                transactionsPath
                        )
                );
        List<Map<?, ?>> newTransactions =
                new ArrayList<>(oldTransactions);

        Map<String, Object> data =
                new HashMap<>();
        data.put(
                "type",
                transaction.type().name()
        );
        data.put(
                "amount",
                transaction.amount()
        );
        data.put(
                "timestamp",
                transaction.timestamp().toString()
        );
        newTransactions.add(data);

        try {

            configuration.set(
                    balancePath,
                    newBalance
            );

            configuration.set(
                    transactionsPath,
                    newTransactions
            );

            save();

        } catch (RuntimeException e) {

            configuration.set(
                    balancePath,
                    oldBalance
            );

            configuration.set(
                    transactionsPath,
                    oldTransactions
            );

            throw e;
        }
    }

    @Override
    public List<Transaction> getTransactions(UUID playerId) {
        String path = "players." + playerId + ".transactions";

        List<Map<?, ?>> storedTransactions =
                configuration.getMapList(path);

        List<Transaction> result = new ArrayList<>();

        for (Map<?, ?> data : storedTransactions) {
            try {
                TransactionType type = TransactionType.valueOf(
                        String.valueOf(data.get("type"))
                );

                long amount = Long.parseLong(
                        String.valueOf(data.get("amount"))
                );

                Instant timestamp = Instant.parse(
                        String.valueOf(data.get("timestamp"))
                );

                result.add(
                        new Transaction(
                                type,
                                amount,
                                timestamp
                        )
                );

            } catch (Exception e) {
                plugin.getLogger().warning(
                        "Не удалось прочитать операцию игрока "
                                + playerId
                );
            }
        }

        return result;
    }

    private void save() {

        Path target = file.toPath();

        Path temp = target.resolveSibling(
                file.getName() + ".tmp"
        );

        try {

            String yaml =
                    configuration.saveToString();

            Files.writeString(
                    temp,
                    yaml,
                    StandardCharsets.UTF_8
            );

            try {

                Files.move(
                        temp,
                        target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(
                        temp,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {

            try {
                Files.deleteIfExists(temp);
            } catch (IOException ignored) {
            }

            throw new IllegalStateException(
                    "Не удалось сохранить accounts.yml",
                    e
            );
        }
    }
}