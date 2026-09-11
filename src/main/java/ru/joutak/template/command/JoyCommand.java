package ru.joutak.template.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.joutak.template.coin.CoinService;
import org.bukkit.entity.Player;

public class JoyCommand implements CommandExecutor {

    private final CoinService coinService;

    public JoyCommand(CoinService coinService) {
        this.coinService = coinService;
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
            player.sendMessage("Команда: /joy give");
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

            ItemStack coin = coinService.createCoin(amount);

            player.getInventory().addItem(coin);

            player.sendMessage("Вы получили " + amount + " Джой.");

            return true;
        }
        player.sendMessage("Неизвестная команда");
        return true;
    }
}
