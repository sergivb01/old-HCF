
package com.customhcf.hcf.kothgame.koth;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.kothgame.koth.argument.KothNextArgument;
import com.customhcf.hcf.kothgame.koth.argument.KothScheduleArgument;
import com.customhcf.hcf.kothgame.koth.argument.KothSetCapDelayArgument;
import com.customhcf.hcf.kothgame.koth.argument.KothShowArgument;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothExecutor
extends ArgumentExecutor {
    private final KothScheduleArgument kothScheduleArgument;

    public KothExecutor(HCF plugin) {
        super("koth");
        this.addArgument((CommandArgument)new KothNextArgument(plugin));
        this.addArgument((CommandArgument)new KothShowArgument());
        this.kothScheduleArgument = new KothScheduleArgument(plugin);
        this.addArgument((CommandArgument)this.kothScheduleArgument);
        this.addArgument((CommandArgument)new KothSetCapDelayArgument(plugin));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
}

