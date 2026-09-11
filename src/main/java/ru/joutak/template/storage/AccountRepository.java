package ru.joutak.template.storage;

import java.util.UUID;

public interface AccountRepository {
    long getBalance(UUID playerId);

    void setBalance(UUID playerId, long balance);
}
