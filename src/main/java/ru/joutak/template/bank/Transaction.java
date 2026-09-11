package ru.joutak.template.bank;

import java.time.Instant;

public record Transaction(
        TransactionType type,
        long amount,
        Instant timestamp
) {
}