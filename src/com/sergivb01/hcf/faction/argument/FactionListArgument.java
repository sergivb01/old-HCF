package com.sergivb01.hcf.faction.argument;

import com.google.common.primitives.Ints;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.util.BukkitUtils;
import com.sergivb01.util.MapSorting;
import com.sergivb01.util.command.CommandArgument;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class FactionListArgument extends CommandArgument{
	private static final int MAX_FACTIONS_PER_PAGE = 10;
	private final HCF plugin;

	public FactionListArgument(final HCF plugin){
		super("list", "See a list of all factions.");
		this.plugin = plugin;
		this.aliases = new String[]{"l"};
	}

	private static net.md_5.bungee.api.ChatColor fromBukkit(final ChatColor chatColor){
		return net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar());
	}

	public String getUsage(final String label){
		return '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		Integer page;
		if(args.length < 2){
			page = 1;
		}else{
			page = Ints.tryParse(args[1]);
			if(page == null){
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
				return true;
			}
		}
		new BukkitRunnable(){
			public void run(){
				FactionListArgument.this.showList(page, label, sender);
			}
		}.runTaskAsynchronously(this.plugin);
		return true;
	}

	private void showList(final int pageNumber, final String label, final CommandSender sender){
		if(pageNumber < 1){
			sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
			return;
		}
		final Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<PlayerFaction, Integer>();
		final Player senderPlayer = (Player) sender;
		for(final Player target : Bukkit.getServer().getOnlinePlayers()){
			if(senderPlayer == null || senderPlayer.canSee(target)){
				final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(target);
				if(playerFaction == null){
					continue;
				}
				factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
			}
		}
		final Map<Integer, List<BaseComponent[]>> pages = new HashMap<Integer, List<BaseComponent[]>>();
		final List<Map.Entry<PlayerFaction, Integer>> sortedMap = (List<Map.Entry<PlayerFaction, Integer>>) MapSorting.sortedValues(factionOnlineMap, (Comparator) Comparator.reverseOrder());
		for(final Map.Entry<PlayerFaction, Integer> entry : sortedMap){
			int currentPage = pages.size();
			List<BaseComponent[]> results = pages.get(currentPage);
			if(results == null || results.size() >= 10){
				pages.put(++currentPage, results = new ArrayList<>(10));
			}
			DecimalFormat format = new DecimalFormat("##.##");
			final PlayerFaction playerFaction2 = entry.getKey();
			final String displayName = playerFaction2.getName();
			final int index = results.size() + ((currentPage > 1) ? ((currentPage - 1) * 10) : 0) + 1;
			final ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.GRAY);
			builder.append(ChatColor.YELLOW + ChatColor.stripColor(displayName)).color(net.md_5.bungee.api.ChatColor.YELLOW).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " show " + playerFaction2.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "Click to view " + displayName + ChatColor.GREEN + '.').create()));
			builder.append(" (" + ChatColor.GRAY + entry.getValue() + '/' + playerFaction2.getMembers().size() + ")", ComponentBuilder.FormatRetention.FORMATTING).color(net.md_5.bungee.api.ChatColor.GRAY);
			builder.append(" " + ChatColor.GRAY + "[" + playerFaction2.getDtrColour() + format.format(playerFaction2.getDeathsUntilRaidable()) + ChatColor.YELLOW + "/" + format.format(playerFaction2.getMaximumDeathsUntilRaidable()) + ChatColor.GRAY + "]");
			results.add(builder.create());
		}
		final int maxPages = pages.size();
		if(pageNumber > maxPages){
			sender.sendMessage(ChatColor.RED + "There " + ((maxPages == 1) ? ("is only " + maxPages + " page") : "no factions to be displayed at this time") + ".");
			return;
		}
		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + " Faction List " + ChatColor.GRAY + "(Page " + pageNumber + " out of " + maxPages + ")");
		final Player player = (Player) sender;
		final Collection<BaseComponent[]> components = pages.get(pageNumber);
		for(final BaseComponent[] component : components){
			if(component == null){
				continue;
			}
			if(player != null){
				player.spigot().sendMessage(component);
			}else{
				sender.sendMessage(TextComponent.toPlainText(component));
			}
		}
		sender.sendMessage(ChatColor.YELLOW + " Use " + ChatColor.WHITE + '/' + label + ' ' + this.getName() + " <#>" + ChatColor.YELLOW + " to view the other pages.");
		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}
}
