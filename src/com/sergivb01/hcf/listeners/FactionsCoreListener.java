package com.sergivb01.hcf.listeners;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.events.CaptureZone;
import com.sergivb01.hcf.events.faction.CapturableFaction;
import com.sergivb01.hcf.faction.event.CaptureZoneEnterEvent;
import com.sergivb01.hcf.faction.event.CaptureZoneLeaveEvent;
import com.sergivb01.hcf.faction.event.PlayerClaimEnterEvent;
import com.sergivb01.hcf.faction.struct.Raidable;
import com.sergivb01.hcf.faction.type.*;
import com.sergivb01.hcf.utils.config.ConfigurationService;
import com.sergivb01.util.BukkitUtils;
import com.sergivb01.util.cuboid.Cuboid;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class FactionsCoreListener implements Listener{
	public static final String PROTECTION_BYPASS_PERMISSION = "hcf.faction.protection.bypass";
	private static final ImmutableMultimap<Object, Object> ITEM_BLOCK_INTERACTABLES;
	private static final ImmutableSet<Material> BLOCK_INTERACTABLES;

	static{
		ITEM_BLOCK_INTERACTABLES = ImmutableMultimap.builder().put(Material.DIAMOND_HOE, Material.GRASS).put(Material.GOLD_HOE, Material.GRASS).put(Material.IRON_HOE, Material.GRASS).put(Material.STONE_HOE, Material.GRASS).put(Material.WOOD_HOE, Material.GRASS).build();
		BLOCK_INTERACTABLES = Sets.immutableEnumSet(Material.BED, Material.BED_BLOCK, Material.BEACON, Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.ANVIL, Material.LEVER, Material.FIRE);
	}

	private final HCF plugin;

	public FactionsCoreListener(HCF plugin){
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static boolean attemptBuild(Entity entity, Location location, String denyMessage){
		return FactionsCoreListener.attemptBuild(entity, location, denyMessage, false);
	}

	public static boolean attemptBuild(Entity entity, Location location, String denyMessage, boolean isInteraction){
		boolean result = false;
		if(entity instanceof Player){
			PlayerFaction playerFaction;
			Player player = (Player) entity;
			if(player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")){
				return true;
			}
			if(player.getWorld().getEnvironment() == World.Environment.THE_END){
				player.sendMessage(ConfigurationService.END_CANNOT_BUILD);
				return false;
			}
			Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(location);
			if(!(factionAt instanceof ClaimableFaction)){
				result = true;
			}else if(factionAt instanceof Raidable && ((Raidable) factionAt).isRaidable()){
				result = true;
			}
			if(factionAt instanceof PlayerFaction && (playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId())) != null && playerFaction.equals(factionAt)){
				result = true;
			}
			if(result){
				if(!isInteraction && Math.abs(location.getBlockX()) <= ConfigurationService.UNBUILDABLE_RANGE && Math.abs(location.getBlockZ()) <= ConfigurationService.UNBUILDABLE_RANGE){
					if(denyMessage != null){
						player.sendMessage(ConfigurationService.WORLD_CANNOT_BUILD);
					}
					return false;
				}
			}else if(denyMessage != null){
				player.sendMessage(String.format(denyMessage, factionAt.getDisplayName(player)));
			}
		}
		return result;
	}

	public static boolean canBuildAt(Location from, Location to){
		Faction toFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(to);
		return !(toFactionAt instanceof Raidable) || ((Raidable) toFactionAt).isRaidable() || toFactionAt.equals(HCF.getPlugin().getFactionManager().getFactionAt(from));
	}

	private void handleMove(PlayerMoveEvent event, PlayerClaimEnterEvent.EnterCause enterCause){
		Faction toFaction;
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()){
			return;
		}
		Player player = event.getPlayer();
		boolean cancelled = false;
		Faction fromFaction = this.plugin.getFactionManager().getFactionAt(from);
		if(!Objects.equals(fromFaction, toFaction = this.plugin.getFactionManager().getFactionAt(to))){
			PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
			Bukkit.getPluginManager().callEvent(calledEvent);
			cancelled = calledEvent.isCancelled();
		}else if(toFaction instanceof CapturableFaction){
			CapturableFaction capturableFaction = (CapturableFaction) toFaction;
			for(CaptureZone captureZone : capturableFaction.getCaptureZones()){
				Cuboid cuboid = captureZone.getCuboid();
				if(cuboid == null) continue;
				boolean containsFrom = cuboid.contains(from);
				boolean containsTo = cuboid.contains(to);
				if(containsFrom && !containsTo){
					CaptureZoneLeaveEvent calledEvent2 = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
					Bukkit.getPluginManager().callEvent(calledEvent2);
					cancelled = calledEvent2.isCancelled();
					break;
				}
				if(containsFrom || !containsTo) continue;
				CaptureZoneEnterEvent calledEvent3 = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
				Bukkit.getPluginManager().callEvent(calledEvent3);
				cancelled = calledEvent3.isCancelled();
				break;
			}
		}
		if(cancelled){
			if(enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT){
				event.setCancelled(true);
			}else{
				from.add(0.5, 0.0, 0.5);
				event.setTo(from);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())
			this.handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerTeleportEvent event){
		this.handleMove(event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event){
		switch(event.getCause()){
			default:
		}
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onStickyPistonExtend(BlockPistonExtendEvent event){
		Faction targetFaction;
		Block block = event.getBlock();
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
		if((targetBlock.isEmpty() || targetBlock.isLiquid()) && (targetFaction = this.plugin.getFactionManager().getFactionAt(targetBlock.getLocation())) instanceof Raidable && !((Raidable) targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onStickyPistonRetract(BlockPistonRetractEvent event){
		if(!event.isSticky()){
			return;
		}
		Location retractLocation = event.getRetractLocation();
		Block retractBlock = retractLocation.getBlock();
		if(!retractBlock.isEmpty() && !retractBlock.isLiquid()){
			Block block = event.getBlock();
			Faction targetFaction = this.plugin.getFactionManager().getFactionAt(retractLocation);
			if(targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFromTo(BlockFromToEvent event){
		Block toBlock = event.getToBlock();
		Block fromBlock = event.getBlock();
		Material fromType = fromBlock.getType();
		Material toType = toBlock.getType();
		if(!(toType != Material.REDSTONE_WIRE && toType != Material.TRIPWIRE || fromType != Material.AIR && fromType != Material.STATIONARY_LAVA && fromType != Material.LAVA)){
			toBlock.setType(Material.AIR);
		}
		if(!(toBlock.getType() != Material.WATER && toBlock.getType() != Material.STATIONARY_WATER && toBlock.getType() != Material.LAVA && toBlock.getType() != Material.STATIONARY_LAVA || FactionsCoreListener.canBuildAt(fromBlock.getLocation(), toBlock.getLocation()))){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && this.plugin.getFactionManager().getFactionAt(event.getTo()).isSafezone() && !this.plugin.getFactionManager().getFactionAt(event.getFrom()).isSafezone()){
			Player player = event.getPlayer();
			player.sendMessage(ConfigurationService.FAILED_PEARL);
			this.plugin.getTimerManager().enderPearlTimer.refund(player);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPortal(PlayerPortalEvent event){
		if(event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL){
			Location from = event.getFrom();
			Location to = event.getTo();
			Player player = event.getPlayer();
			Faction fromFac = this.plugin.getFactionManager().getFactionAt(from);
			if(fromFac.isSafezone()){
				event.setTo(to.getWorld().getSpawnLocation().add(0.5, 0.0, 0.5));
				event.useTravelAgent(false);
				player.sendMessage(ConfigurationService.TELEPORTED_SPAWN);
				return;
			}
			if(event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL){
				TravelAgent travelAgent = event.getPortalTravelAgent();
				if(!travelAgent.getCanCreatePortal()){
					return;
				}
				Location foundPortal = travelAgent.findPortal(to);
				if(foundPortal != null){
					return;
				}
				Faction factionAt = this.plugin.getFactionManager().getFactionAt(to);
				if(factionAt instanceof ClaimableFaction){
					PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
					if(playerFaction != null && playerFaction.equals(factionAt)){
						return;
					}
					player.sendMessage(ChatColor.YELLOW + "Portal would have created portal in territory of " + factionAt.getDisplayName(player) + ChatColor.YELLOW + '.');
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Player){
			Player attacker;
			Player player = (Player) entity;
			Faction playerFactionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
			EntityDamageEvent.DamageCause cause = event.getCause();
			if(playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE){
				event.setCancelled(true);
			}
			if((attacker = BukkitUtils.getFinalAttacker(event, true)) != null){
				PlayerFaction attackerFaction;
				Faction attackerFactionAt = this.plugin.getFactionManager().getFactionAt(attacker.getLocation());
				if(attackerFactionAt.isSafezone()){
					event.setCancelled(true);
					this.plugin.getMessage().sendMessage(attacker, ConfigurationService.CANNOT_ATTACK);
					return;
				}
				if(playerFactionAt.isSafezone()){
					this.plugin.getMessage().sendMessage(attacker, ConfigurationService.CANNOT_ATTACK);
					return;
				}
				PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
				if(playerFaction != null && (attackerFaction = this.plugin.getFactionManager().getPlayerFaction(attacker)) != null){
					if(attackerFaction.equals(playerFaction)){
						this.plugin.getMessage().sendMessage(attacker, ConfigurationService.IN_FACTION.replace("%player%", player.getName()));
						event.setCancelled(true);
					}else if(attackerFaction.getAllied().contains(playerFaction.getUniqueID())){
						this.plugin.getMessage().sendMessage(attacker, ConfigurationService.ALLY_FACTION.replace("%allyplayer%", player.getName()));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onVehicleEnter(VehicleEnterEvent event){
		AnimalTamer owner;
		Entity entered = event.getEntered();
		if(entered instanceof Player && event.getVehicle() instanceof Horse && (owner = ((Horse) event.getVehicle()).getOwner()) != null && !owner.equals(entered)){
			((Player) entered).sendMessage(ChatColor.YELLOW + "You cannot enter a Horse that belongs to " + ChatColor.RED + owner.getName() + ChatColor.YELLOW + '.');
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent event){
		HumanEntity entity = event.getEntity();
		if(entity instanceof Player && ((Player) entity).getFoodLevel() < event.getFoodLevel() && this.plugin.getFactionManager().getFactionAt(entity.getLocation()).isSafezone()){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPotionSplash(PotionSplashEvent event){
		ThrownPotion potion = event.getEntity();
		if(!BukkitUtils.isDebuff(potion)){
			return;
		}
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(potion.getLocation());
		if(factionAt.isSafezone()){
			event.setCancelled(true);
			return;
		}
		ProjectileSource source = potion.getShooter();
		if(source instanceof Player){
			Player player = (Player) source;
			for(LivingEntity affected : event.getAffectedEntities()){
				Player target;
				if(!(affected instanceof Player) || player.equals(affected) || (target = (Player) affected).equals(source) || !this.plugin.getFactionManager().getFactionAt(target.getLocation()).isSafezone())
					continue;
				event.setIntensity(affected, 0.0);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityTarget(EntityTargetEvent event){
		switch(event.getReason()){
			case CLOSEST_PLAYER:
			case RANDOM_TARGET:{
				Faction factionAt;
				PlayerFaction playerFaction;
				Entity target = event.getTarget();
				if(!(event.getEntity() instanceof LivingEntity) || !(target instanceof Player) || !(factionAt = this.plugin.getFactionManager().getFactionAt(target.getLocation())).isSafezone() && ((playerFaction = this.plugin.getFactionManager().getPlayerFaction((Player) target)) == null || !factionAt.equals(playerFaction)))
					break;
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(!event.hasBlock()){
			return;
		}
		Block block = event.getClickedBlock();
		Action action = event.getAction();
		if(action == Action.PHYSICAL && !FactionsCoreListener.attemptBuild(event.getPlayer(), block.getLocation(), null)){
			event.setCancelled(true);
		}
		if(action == Action.RIGHT_CLICK_BLOCK){
			boolean canBuild = !BLOCK_INTERACTABLES.contains(block.getType());
			if(canBuild){
				Material itemType;
				itemType = event.hasItem() ? event.getItem().getType() : null;
				if(itemType != null && ITEM_BLOCK_INTERACTABLES.containsKey(itemType) && ITEM_BLOCK_INTERACTABLES.get(itemType).contains(event.getClickedBlock().getType())){
					canBuild = false;
				}else{
					MaterialData materialData = block.getState().getData();
					if(materialData instanceof Cauldron && !((Cauldron) materialData).isEmpty() && event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE){
						canBuild = false;
					}
				}
			}
			if(!block.getType().equals(Material.WORKBENCH)){

				if(!canBuild && !FactionsCoreListener.attemptBuild(event.getPlayer(), block.getLocation(), ChatColor.YELLOW + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.', true)){
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent event){
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if(factionAt instanceof WarzoneFaction || factionAt instanceof Raidable && !((Raidable) factionAt).isRaidable()){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFade(BlockFadeEvent event){
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLeavesDelay(LeavesDecayEvent event){
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockForm(BlockFormEvent event){
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
		if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityChangeBlock(EntityChangeBlockEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity && !FactionsCoreListener.attemptBuild(entity, event.getBlock().getLocation(), null)){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(event.getBlock().getLocation());

		if(factionAt instanceof GlowstoneFaction && event.getBlock().getType() == Material.GLOWSTONE){
			return;
		}
		if(!FactionsCoreListener.attemptBuild(event.getPlayer(), event.getBlock().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event){
		if(!FactionsCoreListener.attemptBuild(event.getPlayer(), event.getBlockPlaced().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketFill(PlayerBucketFillEvent event){
		if(!FactionsCoreListener.attemptBuild(event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketEmptyEvent event){
		if(!FactionsCoreListener.attemptBuild(event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event){
		Entity remover = event.getRemover();
		if(remover instanceof Player && !FactionsCoreListener.attemptBuild(remover, event.getEntity().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingPlace(HangingPlaceEvent event){
		if(!FactionsCoreListener.attemptBuild(event.getPlayer(), event.getEntity().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingDamageByEntity(EntityDamageByEntityEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Hanging && !FactionsCoreListener.attemptBuild(BukkitUtils.getFinalAttacker(event, false), entity.getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onHangingInteractByPlayer(final PlayerInteractEntityEvent event){
		final Entity entity = event.getRightClicked();
		if(entity instanceof Hanging && !attemptBuild(event.getPlayer(), entity.getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')){
			event.setCancelled(true);
		}
	}
}