package com.sergivb01.hcf.classes.type;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.classes.PvpClass;
import com.sergivb01.hcf.classes.event.PvpClassUnequipEvent;
import com.sergivb01.hcf.utils.Cooldowns;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AssassinClass
		extends PvpClass
		implements Listener{
	private final HCF plugin;
	public HashMap<String, Integer> firstAssassinEffects = new HashMap();

	public AssassinClass(HCF plugin){
		super("Reaper", TimeUnit.SECONDS.toMillis(1L));
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	}

	@EventHandler
	public void onUnEquip(PvpClassUnequipEvent e){
		Player p = e.getPlayer();
		for(Player on : Bukkit.getServer().getOnlinePlayers()){
			if(on.canSee(p) || on.hasPermission("base.commands.vanish")) continue;
			on.showPlayer(p);
		}
		this.firstAssassinEffects.remove(p.getName());
	}

	@EventHandler
	public void onDamageSelf(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(this.plugin.getPvpClassManager().getEquippedClass(p) == null || !this.plugin.getPvpClassManager().getEquippedClass(p).equals(this)){
				return;
			}
			if(this.firstAssassinEffects.containsKey(p.getName()) && this.firstAssassinEffects.get(p.getName()) == 1){
				for(Entity entity : p.getNearbyEntities(20.0, 20.0, 20.0)){
					if(!(entity instanceof Player)) continue;
					Player players = (Player) entity;
					players.sendMessage(ChatColor.YELLOW + "An reaper has taken damage in stealth mode near you: " + ChatColor.GRAY + ChatColor.ITALIC + "(20 x 20)");
				}
			}
		}
	}

	@EventHandler
	public void onHitOtherPlayers(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player p = (Player) e.getDamager();
			Player ent = (Player) e.getEntity();
			if(this.firstAssassinEffects.containsKey(p.getName()) && this.firstAssassinEffects.get(p.getName()) == 1){
				this.afterFiveSeconds(p, true);
			}
		}
	}

	@EventHandler
	public void onClickItem(PlayerInteractEvent e){
		final Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
			PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(p);
			if(equipped == null || !equipped.equals(this)){
				return;
			}
			if(p.getItemInHand().getType() == Material.QUARTZ){
				if(Cooldowns.isOnCooldown("Assassin_item_cooldown", p)){
					p.sendMessage(ChatColor.RED + "You still have an " + ChatColor.GREEN + ChatColor.BOLD + "Reaper" + ChatColor.RED + " cooldown for another " + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Assassin_item_cooldown", p), true) + ChatColor.RED + '.');
					return;
				}
				if(p.getItemInHand().getAmount() == 1){
					p.getInventory().remove(p.getItemInHand());
				}
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GRAY + "Stealth" + ChatColor.YELLOW + " Mode");
				for(Player on : Bukkit.getServer().getOnlinePlayers()){
					on.playEffect(p.getLocation().add(0.5, 2.0, 0.5), Effect.ENDER_SIGNAL, 5);
					on.playEffect(p.getLocation().add(0.5, 1.5, 0.5), Effect.ENDER_SIGNAL, 5);
					on.playEffect(p.getLocation().add(0.5, 1.0, 0.5), Effect.ENDER_SIGNAL, 5);
					on.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
					if(on.hasPermission("base.commands.vanish")) continue;
					on.hidePlayer(p);
				}
				Cooldowns.addCooldown("Assassin_item_cooldown", p, 60);
				p.removePotionEffect(PotionEffectType.SPEED);
				this.firstAssassinEffects.put(p.getName(), 1);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 4), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 0), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0), true);
				BukkitTask bukkitTask = new BukkitRunnable(){

					public void run(){
						if(AssassinClass.this.isApplicableFor(p) && AssassinClass.this.firstAssassinEffects.containsKey(p.getName()) && AssassinClass.this.firstAssassinEffects.get(p.getName()) == 1){
							AssassinClass.this.afterFiveSeconds(p, false);
						}
					}
				}.runTaskLater(this.plugin, 100);
			}
		}
	}

	public void afterFiveSeconds(final Player p, boolean force){
		if(this.firstAssassinEffects.containsKey(p.getName()) && this.isApplicableFor(p)){
			for(Player on : Bukkit.getServer().getOnlinePlayers()){
				if(!on.canSee(p) && !on.hasPermission("base.commands.vanish")){
					on.showPlayer(p);
				}
				on.playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.ENDER_SIGNAL, 3);
				on.playEffect(p.getLocation().add(0.0, 1.5, 0.0), Effect.ENDER_SIGNAL, 3);
				on.playEffect(p.getLocation().add(0.0, 1.0, 0.0), Effect.ENDER_SIGNAL, 3);
				on.playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.BLAZE_SHOOT, 5);
				on.playEffect(p.getLocation().add(0.0, 1.5, 0.0), Effect.BLAZE_SHOOT, 5);
				on.playEffect(p.getLocation().add(0.0, 1.0, 0.0), Effect.BLAZE_SHOOT, 5);
			}
			BukkitTask task1 = new BukkitRunnable(){

				public void run(){
					if(AssassinClass.this.firstAssassinEffects.containsKey(p.getName()) && AssassinClass.this.firstAssassinEffects.get(p.getName()) == 2){
						AssassinClass.this.firstAssassinEffects.remove(p.getName());
						p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GREEN + "Normal" + ChatColor.YELLOW + " Mode");
						if(AssassinClass.this.isApplicableFor(p)){
							p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
							p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
							p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
						}
					}
				}
			}.runTaskLater(this.plugin, 100);
			if(force){
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
				p.removePotionEffect(PotionEffectType.INVISIBILITY);
				this.firstAssassinEffects.remove(p.getName());
				this.firstAssassinEffects.put(p.getName(), 2);
				p.sendMessage(ChatColor.YELLOW + "You have been forced into " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
				return;
			}
			this.firstAssassinEffects.remove(p.getName());
			this.firstAssassinEffects.put(p.getName(), 2);
			p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}

	@Override
	public boolean isApplicableFor(Player player){
		PlayerInventory playerInventory = player.getInventory();
		ItemStack helmet = playerInventory.getHelmet();
		if(helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET){
			return false;
		}
		ItemStack chestplate = playerInventory.getChestplate();
		if(chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE){
			return false;
		}
		ItemStack leggings = playerInventory.getLeggings();
		if(leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS){
			return false;
		}
		ItemStack boots = playerInventory.getBoots();
		return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
	}

}

