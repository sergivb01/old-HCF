package com.customhcf.hcf.command;

import com.customhcf.hcf.HCF;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class SaveDataCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HCF.getPlugin().saveData();
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");

        /*boolean error = false;

        Bukkit.getServer().savePlayers();
        BasePlugin.getPlugin().getServerHandler().saveServerData();

        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Starting backup of data");
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
                p.saveData();
            }catch (Exception e) {
                if(!error) error = true;
            }
        }

        //HCF.getPlugin().getFactionManager().saveFactionData(); //TODO: Already saving it on hcf auto-save thing!
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAutoSave &eTask was completed " + (error ? "with &aerrors&e" : "successfully!")));

        */
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

}