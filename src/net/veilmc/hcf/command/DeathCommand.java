package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeathCommand
        implements CommandExecutor {
    private final HCF plugin;

    public DeathCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

    }
}




