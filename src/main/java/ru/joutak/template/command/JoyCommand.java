package ru.joutak.template.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.joutak.template.bank.BankService;
import ru.joutak.template.coin.CoinService;
import org.bukkit.entity.Player;
import ru.joutak.template.bank.Transaction;

import java.util.List;

public class JoyCommand implements CommandExecutor {

    private final CoinService coinService;
    private final BankService bankService;

    public JoyCommand(CoinService coinService, BankService bankService) {
        this.coinService = coinService;
        this.bankService = bankService;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
            ){
        if(!(sender instanceof Player player)){
            sender.sendMessage("Эту команду может использовать только игрок");
            return true;
        }
        if(args.length == 0){
            player.sendMessage("Команда: /joy <give|balance>");
            return true;
        }
        if (args[0].equalsIgnoreCase("history")) {

            List<Transaction> transactions =
                    bankService.getTransactions(player.getUniqueId());

            if (transactions.isEmpty()) {
                player.sendMessage("История операций пуста.");
                return true;
            }

            player.sendMessage("История операций:");

            for (Transaction transaction : transactions) {
                player.sendMessage(
                        transaction.type()
                                + " | "
                                + transaction.amount()
                                + " Джой | "
                                + transaction.timestamp()
                );
            }

            return true;
        }
        if (args[0].equalsIgnoreCase("balance")) {

            long balance = bankService.getBalance(
                    player.getUniqueId()
            );

            player.sendMessage(
                    "Ваш баланс: " + balance + " Джой."
            );

            return true;
        }
        if (args[0].equalsIgnoreCase("give")) {

            if (!player.hasPermission("joybank.admin.give")) {
                player.sendMessage("У вас нет прав на эту команду.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("Использование: /joy give <количество>");
                return true;
            }

            int amount;

            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Количество должно быть числом.");
                return true;
            }

            if (amount <= 0) {
                player.sendMessage("Количество должно быть больше 0.");
                return true;
            }

            boolean added = coinService.addCoins(
                    player.getInventory(),
                    amount
            );

            if (!added) {
                player.sendMessage("В инвентаре недостаточно места.");
                return true;
            }

            player.sendMessage("Вы получили " + amount + " Джой.");

            return true;
        }
        player.sendMessage("Неизвестная команда");
        return true;
    }
}
