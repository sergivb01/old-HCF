package net.veilmc.hcf.listener;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.user.FactionUser;
import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.struct.Role;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class DeathListener
		implements Listener{
	private static final long REGEN_DELAY = TimeUnit.MINUTES.toMillis(60L);
	public static HashMap<UUID, ItemStack[]> PlayerInventoryContents = new HashMap();
	public static HashMap<UUID, ItemStack[]> PlayerArmorContents = new HashMap();
	private final HCF plugin;

	public DeathListener(HCF plugin){
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeathKillIncrement(PlayerDeathEvent event){
		Player killer = event.getEntity().getKiller();
		if(killer != null){
			FactionUser user = this.plugin.getUserManager().getUser(killer.getUniqueId());
			user.setKills(user.getKills() + 1);
		}
	}



	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		Entity entityKiller;
		Player killer = (Player)(entityKiller = (Entity)event.getEntity().getKiller());
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
		if(playerFaction != null && !ConfigurationService.KIT_MAP){
			Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
			Role role = playerFaction.getMember(player.getUniqueId()).getRole();
			long regen = ConfigurationService.VEILZ_REGEN;
			if(playerFaction.getDeathsUntilRaidable() >= -5.0D){
				playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossMultiplier());

					playerFaction.setRemainingRegenerationTime(REGEN_DELAY);

				playerFaction.broadcast(ChatColor.RED + "Member Death: " + ChatColor.WHITE + role.getAstrix() + player.getName() + ChatColor.YELLOW + " DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format(playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
			}else{
					playerFaction.setRemainingRegenerationTime(REGEN_DELAY);
				playerFaction.broadcast(ChatColor.RED + "Member Death: " + ChatColor.WHITE + role.getAstrix() + player.getName() + ChatColor.YELLOW + " DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format(playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
			}
		}
		PacketPlayOutSpawnEntityWeather packet;
		if((Bukkit.spigot().getTPS()[0] > 15.0D) && (!ConfigurationService.KIT_MAP)){
			PlayerInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
			PlayerArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
			Location location = player.getLocation();
			WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
			EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);
			packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
			for(Player target : Bukkit.getServer().getOnlinePlayers()){
				if(this.plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()){
					((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
					target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
				}
			}
		}
	}



}

