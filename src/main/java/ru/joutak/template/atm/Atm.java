package ru.joutak.template.atm;

import java.util.UUID;

public record Atm(
        UUID id,
        UUID worldId,
        int barrelX,
        int barrelY,
        int barrelZ,
        int signX,
        int signY,
        int signZ
) {
}