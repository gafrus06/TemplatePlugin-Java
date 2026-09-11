package ru.joutak.template.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AtmMenuHolder implements InventoryHolder {

    private final UUID atmId;
    private final Inventory inventory;

    public AtmMenuHolder(UUID atmId) {
        this.atmId = atmId;

        this.inventory = Bukkit.createInventory(
                this,
                27,
                Component.text("JoyBank")
        );
    }

    public UUID getAtmId() {
        return atmId;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}