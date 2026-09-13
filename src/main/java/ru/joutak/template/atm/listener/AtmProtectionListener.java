package ru.joutak.template.atm.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.joutak.template.atm.Atm;
import ru.joutak.template.atm.AtmService;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Optional;

public class AtmProtectionListener implements Listener {

    private final AtmService atmService;

    public AtmProtectionListener(AtmService atmService) {
        this.atmService = atmService;
    }


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isAtmBlock);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isAtmBlock);
    }
    private boolean isAtmBlock(Block block) {
        return atmService
                .findByBlock(block.getLocation())
                .isPresent();
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {

        boolean movesAtm = event.getBlocks()
                .stream()
                .anyMatch(this::isAtmBlock);

        if (movesAtm) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {

        boolean movesAtm = event.getBlocks()
                .stream()
                .anyMatch(this::isAtmBlock);

        if (movesAtm) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        if (isAtmBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (isAtmBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onBlockBreakProtection(BlockBreakEvent event) {

        if (!isAtmBlock(event.getBlock())) {
            return;
        }

        if (!event.getPlayer()
                .hasPermission("joybank.atm.break")) {

            event.setCancelled(true);

            event.getPlayer().sendMessage(
                    "Вы не можете разрушить зарегистрированный банкомат."
            );
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onBlockBreakCleanup(BlockBreakEvent event) {

        Optional<Atm> atm =
                atmService.findByBlock(
                        event.getBlock().getLocation()
                );

        if (atm.isEmpty()) {
            return;
        }

        if (!event.getPlayer()
                .hasPermission("joybank.atm.break")) {
            return;
        }

        atmService.unregister(
                atm.get().id()
        );

        event.getPlayer().sendMessage(
                "Банкомат удалён."
        );
    }
}