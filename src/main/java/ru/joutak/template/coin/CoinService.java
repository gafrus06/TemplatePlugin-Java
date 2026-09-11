package ru.joutak.template.coin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.List;

import java.util.Map;

public class CoinService {

    private final NamespacedKey coinKey;
    private static final float JOY_MODEL_DATA = 1001.0f;

    public CoinService(JavaPlugin plugin) {
        this.coinKey = new NamespacedKey(plugin, "joy_coin");
    }

    public ItemStack createCoin(int amount) {
        ItemStack coin =
                new ItemStack(Material.GOLD_NUGGET, amount);

        ItemMeta meta = coin.getItemMeta();

        meta.displayName(
                Component.text(
                        "Джой",
                        NamedTextColor.GOLD
                )
        );

        meta.getPersistentDataContainer().set(
                coinKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        CustomModelDataComponent modelData =
                meta.getCustomModelDataComponent();

        modelData.setFloats(
                List.of(JOY_MODEL_DATA)
        );

        meta.setCustomModelDataComponent(modelData);

        coin.setItemMeta(meta);

        return coin;
    }

    public boolean isCoin(ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item.getType() != Material.GOLD_NUGGET) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta.getPersistentDataContainer().has(
                coinKey,
                PersistentDataType.BYTE
        );
    }

    public int countCoins(PlayerInventory inventory) {
        int total = 0;

        for (ItemStack item : inventory.getContents()) {
            if (isCoin(item)) {
                total += item.getAmount();
            }
        }

        return total;
    }
    public boolean removeCoins(PlayerInventory inventory, int amount) {
        if (amount <= 0) {
            return false;
        }

        if (countCoins(inventory) < amount) {
            return false;
        }

        int remaining = amount;

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);

            if (!isCoin(item)) {
                continue;
            }

            int stackAmount = item.getAmount();

            if (stackAmount <= remaining) {
                remaining -= stackAmount;
                inventory.setItem(slot, null);
            } else {
                item.setAmount(stackAmount - remaining);
                remaining = 0;
            }

            if (remaining == 0) {
                break;
            }
        }

        return true;
    }
    public int getFreeCoinCapacity(PlayerInventory inventory) {
        ItemStack template = createCoin(1);
        int capacity = 0;

        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                capacity += template.getMaxStackSize();
                continue;
            }

            if (item.isSimilar(template)) {
                capacity += item.getMaxStackSize() - item.getAmount();
            }
        }
        return capacity;
    }

    public boolean canFitCoins(
            PlayerInventory inventory,
            int amount
    ) {
        if (amount <= 0) {
            return false;
        }

        return getFreeCoinCapacity(inventory) >= amount;
    }
    public boolean addCoins(
            PlayerInventory inventory,
            int amount
    ) {
        if (!canFitCoins(inventory, amount)) {
            return false;
        }

        int remaining = amount;

        while (remaining > 0) {
            int stackAmount = Math.min(
                    remaining,
                    64
            );

            ItemStack stack = createCoin(stackAmount);

            Map<Integer, ItemStack> leftovers =
                    inventory.addItem(stack);

            if (!leftovers.isEmpty()) {
                return false;
            }

            remaining -= stackAmount;
        }

        return true;
    }
}