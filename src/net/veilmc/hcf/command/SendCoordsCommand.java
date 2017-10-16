package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.struct.ChatChannel;
import net.veilmc.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendCoordsCommand implements CommandExecutor {
    private final HCF plugin;

    public SendCoordsCommand(HCF plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return false;
        }
        Player p = (Player) sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(p);
        if (playerFaction == null) {
            p.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
        }
        final FactionMember member = playerFaction.getMember(p.getUniqueId());
        final ChatChannel currentChannel = member.getChatChannel();
        final ChatChannel parsed = (args.length >= 2) ? ChatChannel.parse(args[1], null) : currentChannel.getRotation();
        return true;
    }
}


