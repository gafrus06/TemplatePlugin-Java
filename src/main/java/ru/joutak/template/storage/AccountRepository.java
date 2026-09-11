package ru.joutak.template.storage;

import ru.joutak.template.bank.Transaction;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {
    long getBalance(UUID playerId);

    void setBalance(UUID playerId, long balance);
    void addTransaction(
            UUID playerId,
            Transaction transaction
    );

    List<Transaction> getTransactions(UUID playerId);
}
