package com.customhcf.hcf.listener;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.event.FactionChatEvent;
import com.customhcf.hcf.faction.struct.ChatChannel;
import com.customhcf.hcf.faction.type.PlayerFaction;
<<<<<<< HEAD
import com.customhcf.hcf.utils.ConfigurationService;
import com.google.common.collect.ImmutableSet;
=======
>>>>>>> 9840e73dc859ed88166010ffeddcddd6aa1dbcab
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChatListener
implements Listener {
    private static final String DOUBLE_POST_BYPASS_PERMISSION = "hcf.doublepost.bypass";
    private static final Pattern PATTERN;
    private final ConcurrentMap<Object, Object> messageHistory;
    private final HCF plugin;

    public ChatListener(HCF plugin) {
        this.plugin = plugin;
        this.messageHistory = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build().asMap();
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        String lastMessage = (String)this.messageHistory.get(player.getUniqueId());
        String cleanedMessage = PATTERN.matcher(message).replaceAll("");
        if (lastMessage != null && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1) && !player.hasPermission("hcf.doublepost.bypass")) {
            player.sendMessage(ChatColor.RED + "Double posting is prohibited.");
            event.setCancelled(true);
            return;
        }
        this.messageHistory.put(player.getUniqueId(), cleanedMessage);
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        Set recipients = event.getRecipients();
        if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if (!this.isGlobalChannel(message)) {
                Set online = playerFaction.getOnlinePlayers();
                if (chatChannel == ChatChannel.ALLIANCE) {
                    List<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
                return;
            }
            message = message.substring(1, message.length()).trim();
            event.setMessage(message);
        }
        event.setCancelled(true);
<<<<<<< HEAD
        Boolean isTag = true;
        if (player.hasPermission("faction.removetag")) {
            isTag = true;
        }

        Integer k = Integer.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
        double d = Integer.valueOf(player.getStatistic(Statistic.DEATHS));
        double kd = k / d;
        String kdr;
        DecimalFormat df = new DecimalFormat("#.##");

        if (df.format(kd).matches(".*\\d+.*")){
            kdr = df.format(kd);
        } else {
            kdr = "0";
        }

        String kdrPrefix = (kd > 2) ? ChatColor.RED + kdr : ChatColor.GREEN + kdr;
=======
>>>>>>> 9840e73dc859ed88166010ffeddcddd6aa1dbcab

        //String kdrPrefix = (kdr > 2 ? ChatColor.RED + kdr : ChatColor.GREEN + kdr);

        String rank = ChatColor.translateAlternateColorCodes('&', "&e" + PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
        String displayName = player.getDisplayName();
        displayName = rank + displayName;
<<<<<<< HEAD
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        String tag = playerFaction == null ? "" : ChatColor.GOLD + "[" + playerFaction.getDisplayName(console) + ChatColor.RED + "] " + ((ConfigurationService.KIT_MAP) ? ChatColor.GRAY + "(" + kdr + ChatColor.DARK_GRAY + ") " : "");
        console.sendMessage( tag +  displayName + ChatColor.GOLD + ": " + ChatColor.GRAY + message);
=======

        String tag = playerFaction == null ? "" : ChatColor.GOLD + "[" + playerFaction.getDisplayName(Bukkit.getConsoleSender()) + ChatColor.RED + "] ";

        Bukkit.getConsoleSender().sendMessage( tag +  displayName + ChatColor.GOLD + ": " + ChatColor.GRAY + message);

>>>>>>> 9840e73dc859ed88166010ffeddcddd6aa1dbcab
        for (Player recipient : event.getRecipients()) {
        	tag = playerFaction == null ? ChatColor.GOLD + "[" + ChatColor.RED + "*" + ChatColor.GOLD + "] " :ChatColor.GOLD + "[" +  playerFaction.getDisplayName(recipient) + ChatColor.GOLD + "] " + ((ConfigurationService.KIT_MAP) ? ChatColor.GRAY + "(" + kdr + ChatColor.DARK_GRAY + ") " : "");
            recipient.sendMessage(tag + displayName + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
        }
    }

    private boolean isGlobalChannel(String input) {
        int length = input.length();
        if (length <= 1 || !input.startsWith("!")) {
            return false;
        }
        for (int i = 1; i < length; ++i) {
            char character = input.charAt(i);
            if (character == ' ') {
                continue;
            }
            if (character != '/') break;
            return false;
        }
        return true;
    }

    static {
        PATTERN = Pattern.compile("\\W");
    }
}