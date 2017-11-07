package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoordsCommand
        implements CommandExecutor {
    private final HCF plugin;

    public CoordsCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Bukkit.dispatchCommand(sender, "help");
        return true;
    }
}