package net.veilmc.hcf.user;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.config.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener
{
	private Map<UUID, PlayerData> playerData;

	public PlayerManager() {
		this.playerData = new HashMap<UUID, PlayerData>();
		for (final Player player : Bukkit.getOnlinePlayers()) {
			final PlayerData data = new PlayerData();
			this.playerData.put(player.getUniqueId(), data);
		}
		Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)HCF.getInstance());
	}

	public Map<UUID, PlayerData> getAllData() {
		return this.playerData;
	}

	public PlayerData getPlayerData(final Player player) {
		return this.playerData.get(player.getUniqueId());
	}



	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		this.playerData.put(player.getUniqueId(), new PlayerData());
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if (this.playerData.containsKey(player.getUniqueId())) {
			this.playerData.get(player.getUniqueId()).saveConfig();
			new BukkitRunnable() {
				public void run() {
					PlayerManager.this.playerData.remove(player.getUniqueId());
				}
			}.runTaskLater((Plugin)HCF.getInstance(), 2L);
		}
	}

	@EventHandler
	public void onKick(final PlayerKickEvent event) {
		final Player player = event.getPlayer();
		if (this.playerData.containsKey(player.getUniqueId())) {
			this.playerData.get(player.getUniqueId()).saveConfig();
			new BukkitRunnable() {
				public void run() {
					PlayerManager.this.playerData.remove(player.getUniqueId());
				}
			}.runTaskLater((Plugin)HCF.getInstance(), 2L);
		}
	}
}