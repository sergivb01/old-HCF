package net.veilmc.hcf.spawn.argument;

import com.google.common.primitives.Ints;
import net.veilmc.base.BaseConstants;
import net.veilmc.hcf.HCF;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TokenGiveArgument
        extends CommandArgument {
    private final HCF plugin;
    private static final String PERMISSION = "hcf.command.token.argument.give.bypass";

    public TokenGiveArgument(HCF plugin) {
        super("give", "Give a player a token.");
        this.plugin = plugin;
        this.aliases = new String[]{"transfer", "send", "pay", "add"};
        this.permission = "hcf.command.token.argument." + this.getName();
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <playerName> <amount>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        Integer amount = Ints.tryParse(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount of tokens must be positive.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[1]));
            return true;
        }
        Player onlineTarget = target.getPlayer();
        if (sender instanceof Player && !sender.hasPermission(PERMISSION)) {
            Player player = (Player)sender;
            int ownedTokens = this.plugin.getUserManager().getUser(player.getUniqueId()).getSpawnTokens();
            if (amount > ownedTokens) {
                sender.sendMessage(ChatColor.RED + "You tried to give " + target.getName() + ' ' + amount + " tokens, but you only have " + ownedTokens + '.');
                return true;
            }
            this.plugin.getUserManager().getUser(player.getUniqueId()).setSpawnTokens(ownedTokens - amount);
        }
        final int targetTokens = this.plugin.getUserManager().getUser(target.getUniqueId()).getSpawnTokens();
        this.plugin.getUserManager().getUser(target.getUniqueId()).setSpawnTokens(targetTokens + amount);
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ' ' + amount + ' ' + (amount > 1 ? "token" : "tokens") + '.');
        sender.sendMessage(ChatColor.GREEN + "Remaining Tokens: " + ChatColor.RED + targetTokens +  ChatColor.RED + ' ' + ((targetTokens == 1) ? "token" : "tokens") + '.');
        if (onlineTarget != null) {
            onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you " + ChatColor.GOLD + amount + ' ' + (amount > 1 ? "token" : "tokens") + '.');
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.emptyList();
    }
}
