
package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.faction.FactionExecutor;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import net.veilmc.util.command.CommandArgument;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionHelpArgument
extends CommandArgument {
    private static final int HELP_PER_PAGE = 10;
    private final FactionExecutor executor;
    private ImmutableMultimap<Integer, Text> pages;

    public FactionHelpArgument(FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }
        Integer page = Ints.tryParse(args[1]);
        if (page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }

    private void showPage(CommandSender sender, String label, int pageNumber) {
        if (this.pages == null) {
            boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            ArrayListMultimap pages = ArrayListMultimap.create();
            for (CommandArgument argument : this.executor.getArguments()) {
                String permission;
                if (argument.equals(this) || (permission = argument.getPermission()) != null && !sender.hasPermission(permission) || argument.isPlayerOnly() && !isPlayer) continue;
                pages.get(val).add(new Text(ChatColor.YELLOW + "  /" + label + ' ' + argument.getName() + ChatColor.WHITE + " - " + ChatColor.GREEN + argument.getDescription()).setColor(ChatColor.GRAY).setClick(ClickAction.SUGGEST_COMMAND, "/" + label + " " + argument.getName()));
                if (++count % 10 != 0) continue;
                ++val;
            }
            this.pages = ImmutableMultimap.copyOf(pages);
        }
        int totalPageCount = this.pages.size() / 10 + 1;
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        if (pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + " Faction Help " + ChatColor.GRAY + "(Page" + pageNumber + " out of " + totalPageCount + ")");
        sender.sendMessage(" ");
        for (Text message : this.pages.get(pageNumber)) {
            message.send(sender);
        }
        //sender.sendMessage((Object)ChatColor.GRAY + " Use " + (Object)ChatColor.GREEN + '/' + label + ' ' + this.getName() + " <#>" + (Object)ChatColor.GRAY + " to view other pages.");
        //if (pageNumber == 1) {
        	sender.sendMessage(" ");
        	sender.sendMessage(ChatColor.GRAY + " » " + ChatColor.YELLOW + "You are currently on page " + ChatColor.GREEN + pageNumber + "/" + ChatColor.GREEN + totalPageCount);
            //sender.sendMessage((Object)ChatColor.GRAY + "Click a command to '" + (Object)ChatColor.ITALIC + "instantly" + (Object)ChatColor.GRAY + "' preform it.");
        
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
}
}


