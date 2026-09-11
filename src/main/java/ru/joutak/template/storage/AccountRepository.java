package ru.joutak.template.storage;

import ru.joutak.template.bank.Transaction;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    long getBalance(UUID playerId);

    void applyTransaction(
            UUID playerId,
            long newBalance,
            Transaction transaction
    );

    List<Transaction> getTransactions(UUID playerId);
}