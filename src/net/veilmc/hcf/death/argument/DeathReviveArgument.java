package net.veilmc.hcf.death.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeathReviveArgument extends CommandArgument {
    private final HCF plugin;

    public DeathReviveArgument(final HCF plugin) {
        super("revive", "Revive a deathbanned player");
        this.plugin = plugin;
        this.permission = "hcf.command.death.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return true;
    }
}