package ru.joutak.template.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.joutak.template.bank.Transaction;
import ru.joutak.template.bank.TransactionType;

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
    public void setBalance(UUID playerId, long balance) {
        String path = "players." + playerId + ".balance";

        configuration.set(path, balance);

        save();
    }

    @Override
    public void addTransaction(
            UUID playerId,
            Transaction transaction
    ) {
        String path = "players." + playerId + ".transactions";

        List<Map<?, ?>> transactions =
                new ArrayList<>(configuration.getMapList(path));

        Map<String, Object> data = new HashMap<>();

        data.put("type", transaction.type().name());
        data.put("amount", transaction.amount());
        data.put("timestamp", transaction.timestamp().toString());

        transactions.add(data);

        configuration.set(path, transactions);

        save();
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