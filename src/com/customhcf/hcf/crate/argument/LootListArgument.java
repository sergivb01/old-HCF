
package com.customhcf.hcf.crate.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.crate.Key;
import com.customhcf.hcf.crate.KeyManager;
import com.customhcf.util.command.CommandArgument;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LootListArgument
extends CommandArgument {
    private final HCF plugin;

    public LootListArgument(HCF plugin) {
        super("list", "List all crate key types");
        this.plugin = plugin;
        this.permission = "hcf.command.loot.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List keyNames = this.plugin.getKeyManager().getKeys().stream().map(Key::getDisplayName).collect(Collectors.toList());
        sender.sendMessage(ChatColor.GRAY + "List of key types: " + StringUtils.join(keyNames, new StringBuilder().append(ChatColor.GRAY).append(", ").toString()));
        return true;
    }
}

