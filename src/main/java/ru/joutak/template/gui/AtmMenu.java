package ru.joutak.template.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.joutak.template.atm.Atm;
import ru.joutak.template.bank.BankService;

public class AtmMenu {

    private final BankService bankService;

    public AtmMenu(BankService bankService) {
        this.bankService = bankService;
    }

    public void open(Player player, Atm atm) {

        AtmMenuHolder holder =
                new AtmMenuHolder(atm.id());

        long balance = bankService.getBalance(
                player.getUniqueId()
        );

        holder.getInventory().setItem(
                4,
                createButton(
                        Material.GOLD_INGOT,
                        "Баланс: " + balance + " Джой",
                        NamedTextColor.GOLD
                )
        );

        // Внести
        holder.getInventory().setItem(
                9,
                createButton(Material.LIME_WOOL, "Внести 1", NamedTextColor.GREEN)
        );

        holder.getInventory().setItem(
                10,
                createButton(Material.LIME_WOOL, "Внести 16", NamedTextColor.GREEN)
        );

        holder.getInventory().setItem(
                11,
                createButton(Material.LIME_WOOL, "Внести 64", NamedTextColor.GREEN)
        );

        holder.getInventory().setItem(
                12,
                createButton(Material.EMERALD_BLOCK, "Внести всё", NamedTextColor.GREEN)
        );

        // Снять
        holder.getInventory().setItem(
                14,
                createButton(Material.RED_WOOL, "Снять 1", NamedTextColor.RED)
        );

        holder.getInventory().setItem(
                15,
                createButton(Material.RED_WOOL, "Снять 16", NamedTextColor.RED)
        );

        holder.getInventory().setItem(
                16,
                createButton(Material.RED_WOOL, "Снять 64", NamedTextColor.RED)
        );

        holder.getInventory().setItem(
                17,
                createButton(Material.REDSTONE_BLOCK, "Снять всё", NamedTextColor.RED)
        );

        player.openInventory(holder.getInventory());
    }

    private ItemStack createButton(
            Material material,
            String name,
            NamedTextColor color
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text(name, color)
        );

        item.setItemMeta(meta);

        return item;
    }
}