package net.veilmc.hcf.death.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeathInfoArgument extends CommandArgument {
    private final HCF plugin;

    public DeathInfoArgument(final HCF plugin) {
        super("info", "Check Deathban Reason");
        this.plugin = plugin;
        this.permission = "hcf.command.death.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    }
}