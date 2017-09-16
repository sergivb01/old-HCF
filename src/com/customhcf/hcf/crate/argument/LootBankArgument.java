
package com.customhcf.hcf.crate.argument;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.crate.KeyManager;
import com.customhcf.util.command.CommandArgument;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LootBankArgument
extends CommandArgument {
    private final HCF plugin;

    public LootBankArgument(HCF plugin) {
        super("bank", "Check the loot keys in your bank account");
        this.plugin = plugin;
        this.permission = "hcf.command.loot.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage((Object)ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        UUID uuid = player.getUniqueId();
        Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
        if (crateKeyMap.isEmpty()) {
            sender.sendMessage((Object)ChatColor.RED + "There are no keys in your bank account.");
            return true;
        }
        for (Map.Entry<String, Integer> entry : crateKeyMap.entrySet()) {
            sender.sendMessage((Object)ChatColor.YELLOW + entry.getKey() + ": " + (Object)ChatColor.GOLD + entry.getValue());
        }
        return true;
    }
}

