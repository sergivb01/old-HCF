package com.customhcf.hcf.command;

import com.customhcf.base.BasePlugin;
import com.customhcf.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import sun.misc.MessageUtils;

import java.util.Collections;
import java.util.List;

public class SaveDataCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");

        int errors = 0;
        boolean error = false;

        Bukkit.getServer().savePlayers();
        BasePlugin.getPlugin().getServerHandler().saveServerData();
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Starting backup of data");
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
                p.saveData();
            }catch (Exception e) {
                errors++;
                if(!error) error = true;
            }
        }

        HCF.getPlugin().getFactionManager().saveFactionData();

        if(!error){
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lTask completed with no errors!"));
        }else{
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lTask completed with &6" + errors + "&7 errors!"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

}