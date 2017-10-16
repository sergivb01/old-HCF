package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class coords
        implements CommandExecutor {
    private final HCF plugin;

    public coords(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        Bukkit.dispatchCommand(p, "help");
        return true;
    }
}