package ru.joutak.template.bank;

import ru.joutak.template.storage.AccountRepository;

import java.util.UUID;

public class BankService {

    private final AccountRepository accountRepository;

    public BankService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public long getBalance(UUID playerId) {
        return accountRepository.getBalance(playerId);
    }

    public void deposit(UUID playerId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Сумма пополнения должна быть больше нуля"
            );
        }

        long currentBalance = getBalance(playerId);

        accountRepository.setBalance(
                playerId,
                currentBalance + amount
        );
    }

    public boolean withdraw(UUID playerId, long amount) {
        if (amount <= 0) {
            return false;
        }

        long currentBalance = getBalance(playerId);

        if (currentBalance < amount) {
            return false;
        }

        accountRepository.setBalance(
                playerId,
                currentBalance - amount
        );

        return true;
    }
}