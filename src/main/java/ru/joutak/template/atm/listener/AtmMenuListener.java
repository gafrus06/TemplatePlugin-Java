package ru.joutak.template.atm.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import ru.joutak.template.atm.Atm;
import ru.joutak.template.atm.AtmService;
import ru.joutak.template.bank.BankService;
import ru.joutak.template.coin.CoinService;
import ru.joutak.template.gui.AtmMenu;
import ru.joutak.template.gui.AtmMenuHolder;

import java.util.Optional;

public class AtmMenuListener implements Listener {

    private final CoinService coinService;
    private final BankService bankService;
    private final AtmService atmService;
    private final AtmMenu atmMenu;

    public AtmMenuListener(
            CoinService coinService,
            BankService bankService,
            AtmService atmService,
            AtmMenu atmMenu
    ) {
        this.coinService = coinService;
        this.bankService = bankService;
        this.atmService = atmService;
        this.atmMenu = atmMenu;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getView()
                .getTopInventory()
                .getHolder(false)
                instanceof AtmMenuHolder holder)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getRawSlot()
                >= event.getView().getTopInventory().getSize()) {
            return;
        }

        Optional<Atm> optionalAtm =
                atmService.findById(holder.getAtmId());

        if (optionalAtm.isEmpty()) {
            player.closeInventory();
            player.sendMessage("Этот банкомат больше не существует.");
            return;
        }

        Atm atm = optionalAtm.get();

        switch (event.getRawSlot()) {

            case 9 -> deposit(player, atm, 1);
            case 10 -> deposit(player, atm, 16);
            case 11 -> deposit(player, atm, 64);
            case 12 -> depositAll(player, atm);

            case 14 -> withdraw(player, atm, 1);
            case 15 -> withdraw(player, atm, 16);
            case 16 -> withdraw(player, atm, 64);
            case 17 -> withdrawAll(player, atm);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        if (event.getView()
                .getTopInventory()
                .getHolder(false)
                instanceof AtmMenuHolder) {

            event.setCancelled(true);
        }
    }

    private void deposit(
            Player player,
            Atm atm,
            int amount
    ) {

        int available =
                coinService.countCoins(
                        player.getInventory()
                );

        if (available < amount) {
            player.sendMessage(
                    "Недостаточно наличных. У вас: "
                            + available + " Джой."
            );
            return;
        }

        boolean removed =
                coinService.removeCoins(
                        player.getInventory(),
                        amount
                );

        if (!removed) {
            player.sendMessage(
                    "Не удалось забрать монеты."
            );
            return;
        }

        try {

            bankService.deposit(
                    player.getUniqueId(),
                    amount
            );

        } catch (RuntimeException e) {

            // Возвращаем физические деньги
            coinService.addCoins(
                    player.getInventory(),
                    amount
            );

            player.sendMessage(
                    "Ошибка сохранения. Монеты возвращены."
            );

            return;
        }

        player.sendMessage(
                "Внесено: " + amount + " Джой."
        );

        atmMenu.open(player, atm);
    }
    private void depositAll(
            Player player,
            Atm atm
    ) {

        int amount =
                coinService.countCoins(
                        player.getInventory()
                );

        if (amount == 0) {
            player.sendMessage(
                    "У вас нет наличных Джой."
            );
            return;
        }

        deposit(
                player,
                atm,
                amount
        );
    }
    private void withdraw(
            Player player,
            Atm atm,
            int amount
    ) {

        long balance =
                bankService.getBalance(
                        player.getUniqueId()
                );

        if (balance < amount) {
            player.sendMessage(
                    "Недостаточно денег на счёте. Баланс: "
                            + balance + " Джой."
            );
            return;
        }

        if (!coinService.canFitCoins(
                player.getInventory(),
                amount
        )) {
            player.sendMessage(
                    "Недостаточно места в инвентаре."
            );
            return;
        }

        boolean added =
                coinService.addCoins(
                        player.getInventory(),
                        amount
                );

        if (!added) {
            player.sendMessage(
                    "Не удалось выдать монеты."
            );
            return;
        }

        try {

            boolean withdrawn =
                    bankService.withdraw(
                            player.getUniqueId(),
                            amount
                    );

            if (!withdrawn) {

                coinService.removeCoins(
                        player.getInventory(),
                        amount
                );

                player.sendMessage(
                        "Не удалось снять деньги."
                );

                return;
            }

        } catch (RuntimeException e) {
            coinService.removeCoins(
                    player.getInventory(),
                    amount
            );

            player.sendMessage(
                    "Ошибка сохранения. Операция отменена."
            );

            return;
        }

        player.sendMessage(
                "Снято: " + amount + " Джой."
        );

        atmMenu.open(player, atm);
    }
    private void withdrawAll(
            Player player,
            Atm atm
    ) {

        long balance =
                bankService.getBalance(
                        player.getUniqueId()
                );

        if (balance <= 0) {
            player.sendMessage(
                    "На счёте нет денег."
            );
            return;
        }

        if (balance > Integer.MAX_VALUE) {
            player.sendMessage(
                    "Баланс слишком большой для одной операции."
            );
            return;
        }

        withdraw(
                player,
                atm,
                (int) balance
        );
    }
}