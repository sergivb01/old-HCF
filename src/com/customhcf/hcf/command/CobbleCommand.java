package com.customhcf.hcf.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.customhcf.hcf.utils.ConfigurationService;

import java.util.ArrayList;

public class CobbleCommand implements Listener, CommandExecutor{

    public static ArrayList<String> cobbletoggle = new ArrayList();

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        cobbletoggle.remove(p.getName());
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player p = (Player) sender;
        if ((sender instanceof Player))
        {
            if ((command.getName().equalsIgnoreCase("cobble"))) {
                if (args.length == 0) {
                    if ((!cobbletoggle.contains(p.getName()))) {
                        p.sendMessage(ConfigurationService.COBBLE_DISABLED);
                        cobbletoggle.add(p.getName());
                    } else if ((cobbletoggle.contains(p.getName()))) {
                        cobbletoggle.remove(p.getName());
                        p.sendMessage(ConfigurationService.COBBLE_ENABLED);
                        //p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou can now pick up cobblestone."));
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onCobblePickup(PlayerPickupItemEvent e)
    {
        Player p = e.getPlayer();
        if ((cobbletoggle.contains(p.getName())) && e.getItem().getItemStack().getType() == Material.COBBLESTONE) {
            e.setCancelled(true);
        }
    }

}
