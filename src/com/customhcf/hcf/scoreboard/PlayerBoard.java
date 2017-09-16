package com.customhcf.hcf.scoreboard;

import com.customhcf.base.BasePlugin;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.classes.archer.ArcherClass;
import com.customhcf.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Collections;

public class PlayerBoard {
    public final BufferedObjective bufferedObjective;
    private final Team members;
    private final Team archers;
    private final Team neutrals;
    private final Team vanish;
    private final Team allies;
    private final Team focused;
    private final Team pvpprot;

    private final Scoreboard scoreboard;
    private final Player player;
    private final HCF plugin;
    private boolean sidebarVisible = false;
    private boolean removed = false;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;

    public PlayerBoard(HCF plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        this.pvpprot = this.scoreboard.registerNewTeam("pvpprot");
        this.pvpprot.setPrefix(ChatColor.AQUA.toString());
        this.vanish = this.scoreboard.registerNewTeam("vanish");
        this.vanish.setPrefix(ConfigurationService.ENEMY_COLOUR.toString());
        this.vanish.setCanSeeFriendlyInvisibles(true);
        this.members = this.scoreboard.registerNewTeam("members");
        this.members.setPrefix(ConfigurationService.TEAMMATE_COLOUR.toString());
        this.members.setCanSeeFriendlyInvisibles(true);
        this.archers = this.scoreboard.registerNewTeam("archers");
        this.archers.setPrefix(net.md_5.bungee.api.ChatColor.RED.toString());
        this.neutrals = this.scoreboard.registerNewTeam("neutrals");
        this.neutrals.setPrefix(ConfigurationService.ENEMY_COLOUR.toString());
        this.allies = this.scoreboard.registerNewTeam("allies");
        this.allies.setPrefix(ConfigurationService.ALLY_COLOUR.toString());

        this.focused = this.scoreboard.registerNewTeam("focused");
        this.focused.setPrefix(ChatColor.LIGHT_PURPLE.toString());
        player.setScoreboard(this.scoreboard);

    }

    
    public void remove() {
        this.removed = true;
        if (this.scoreboard != null) {
            Scoreboard scoreboard = this.scoreboard;
            synchronized (scoreboard) {
                for (Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for (Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isSidebarVisible() {
        return this.sidebarVisible;
    }

    public void setSidebarVisible(boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public void setDefaultSidebar(final SidebarProvider provider, long updateInterval) {
        if (provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        this.defaultProvider = provider;
        if (this.runnable != null) {
            this.runnable.cancel();
        }
        if (provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        this.runnable = new BukkitRunnable(){

            public void run() {
                if (PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if (provider.equals(PlayerBoard.this.defaultProvider)) {
                    PlayerBoard.this.updateObjective();
                }
            }
        };
        this.runnable.runTaskTimerAsynchronously((Plugin)this.plugin, updateInterval, updateInterval);
    }

    public void setTemporarySidebar(final SidebarProvider provider, long expiration) {
        this.temporaryProvider = provider;
        this.updateObjective();
        new BukkitRunnable(){

            public void run() {
                if (PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if (PlayerBoard.this.temporaryProvider == provider) {
                    PlayerBoard.this.temporaryProvider = null;
                    PlayerBoard.this.updateObjective();
                }
            }
        }.runTaskLaterAsynchronously((Plugin)this.plugin, expiration);
    }

    private void updateObjective() {
        SidebarProvider provider;
        SidebarProvider sidebarProvider = provider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        } else {
            this.bufferedObjective.setTitle(provider.getTitle());
            this.bufferedObjective.setAllLines(provider.getLines(this.player));
            this.bufferedObjective.flip();
        }
    }

    public void addUpdate(Player target) {
        this.addUpdates(Collections.singleton(target));
    }

    public void addUpdates(final Collection<? extends Player> updates) {
        if (this.removed) {
            return;
        }
        new BukkitRunnable(){

            public void run() {

                if (PlayerBoard.this.removed) {
                    return;
                }
            	if(!player.isOnline()){
            		return;
            	}
                PlayerFaction playerFaction = null;
                boolean hasRun = false;
                for (Player update : updates) {

                	if(!player.isOnline()){
                		return;
                	}
                    PlayerFaction targetFaction;
                    if (PlayerBoard.this.player.equals((Object)update)) {
                        if (BasePlugin.getPlugin().getUserManager().getUser(update.getUniqueId()).isVanished()) {
                            PlayerBoard.this.vanish.addPlayer((OfflinePlayer)update);
                        }
                        if (HCF.getPlugin().getTimerManager().pvpProtectionTimer.hasCooldown(update)) {
                            PlayerBoard.this.pvpprot.addPlayer((OfflinePlayer)update);
                            continue;
                        }
                        if (ArcherClass.tagged.containsKey(update.getUniqueId())) {
                            PlayerBoard.this.archers.addPlayer((OfflinePlayer)update);
                        }
                        PlayerBoard.this.members.addPlayer((OfflinePlayer)update);
                        continue;
                    }
                    if (!hasRun) {
                        playerFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(PlayerBoard.this.player);
                        hasRun = true;
                    }
                    if (BasePlugin.getPlugin().getUserManager().getUser(update.getUniqueId()).isVanished()) {
                        PlayerBoard.this.vanish.addPlayer((OfflinePlayer)update);
                        continue;
                    }
                    if(playerFaction != null && playerFaction.getFocused() != null && playerFaction.getFocused().equals(update.getName())){
                        PlayerBoard.this.focused.addPlayer((OfflinePlayer)update);
                        continue;

                    }
                    if (ArcherClass.tagged.containsKey(update.getUniqueId())) {
                        PlayerBoard.this.archers.addPlayer((OfflinePlayer)update);
                        continue;
                    }
                    if (playerFaction == null || (targetFaction = PlayerBoard.this.plugin.getFactionManager().getPlayerFaction(update)) == null) {
                        PlayerBoard.this.neutrals.addPlayer((OfflinePlayer)update);
                        continue;
                    }
                    if (playerFaction.equals(targetFaction)) {
                        PlayerBoard.this.members.addPlayer((OfflinePlayer)update);
                        continue;
                    }
                    if (playerFaction.getAllied().contains(targetFaction.getUniqueID())) {
                        PlayerBoard.this.allies.addPlayer((OfflinePlayer)update);
                        continue;
                    }

                    PlayerBoard.this.neutrals.addPlayer((OfflinePlayer)update);
                }
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }

}