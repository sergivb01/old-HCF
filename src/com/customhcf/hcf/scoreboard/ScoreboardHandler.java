
package com.customhcf.hcf.scoreboard;

import com.customhcf.base.event.PlayerVanishEvent;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.event.FactionRelationCreateEvent;
import com.customhcf.hcf.faction.event.FactionRelationRemoveEvent;
import com.customhcf.hcf.faction.event.PlayerJoinedFactionEvent;
import com.customhcf.hcf.faction.event.PlayerLeftFactionEvent;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.scoreboard.PlayerBoard;
import com.customhcf.hcf.scoreboard.SidebarProvider;
import com.customhcf.hcf.scoreboard.provider.TimerSidebarProvider;
import com.google.common.base.Optional;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class ScoreboardHandler implements Listener
{
    private final Map<UUID, PlayerBoard> playerBoards;
    private final TimerSidebarProvider timerSidebarProvider;
    private final HCF plugin;

    public ScoreboardHandler(final HCF plugin) {
        super();
        this.playerBoards = new HashMap<UUID, PlayerBoard>();
        this.plugin = plugin;
        this.timerSidebarProvider = new TimerSidebarProvider(plugin);
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
        for (final Player players : Bukkit.getOnlinePlayers()) {
            final PlayerBoard playerBoard;
            this.setPlayerBoard(players.getUniqueId(), playerBoard = new PlayerBoard(plugin, players));
            Collection<? extends Player> c = Arrays.asList(Bukkit.getOnlinePlayers());
            playerBoard.addUpdates(c);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdate(player);
        }
        final PlayerBoard board2 = new PlayerBoard(this.plugin, player);
        Collection<? extends Player> c = Arrays.asList(Bukkit.getOnlinePlayers());
        board2.addUpdates(c);
        this.setPlayerBoard(uuid, board2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            final Collection<Player> players = (Collection<Player>)this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for (final Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            final Collection<Player> players = (Collection<Player>)event.getFaction().getOnlinePlayers();
            this.getPlayerBoard(event.getUniqueID()).addUpdates(players);
            for (final Player target : players) {
                this.getPlayerBoard(target.getUniqueId()).addUpdate(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyCreate(final FactionRelationCreateEvent event) {
        final Set<Player> updates = new HashSet<Player>(event.getSenderFaction().getOnlinePlayers());
        updates.addAll(event.getTargetFaction().getOnlinePlayers());
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionAllyRemove(final FactionRelationRemoveEvent event) {
        final Set<Player> updates = new HashSet<Player>(event.getSenderFaction().getOnlinePlayers());
        updates.addAll(event.getTargetFaction().getOnlinePlayers());
        for (final PlayerBoard board : this.playerBoards.values()) {
            board.addUpdates(updates);
        }
    }

    public PlayerBoard getPlayerBoard(final UUID uuid) {
        return this.playerBoards.get(uuid);
    }

    public void setPlayerBoard(final UUID uuid, final PlayerBoard board) {
        this.playerBoards.put(uuid, board);
        board.setSidebarVisible(true);
        board.setDefaultSidebar(this.timerSidebarProvider, 2L);
    }

    public void clearBoards() {
        final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }
}