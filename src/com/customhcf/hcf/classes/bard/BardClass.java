package com.customhcf.hcf.classes.bard;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.classes.PvpClass;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.chat.Lang;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BardClass extends PvpClass implements Runnable
{
    private static final long BUFF_COOLDOWN_MILLIS;
    private static final int TEAMMATE_NEARBY_RADIUS = 20;
    private static final long HELD_REAPPLY_TICKS = 5L;
    private final Map<UUID, BardData> bardDataMap;
    private final Map<Material, BardEffect> bardEffects;
    private final BardRestorer bardRestorer;
    private final TObjectLongMap<UUID> msgCooldowns;
    private HCF plugin;

    public BardClass(final HCF plugin) {
        super("Bard", TimeUnit.SECONDS.toMillis(1L));
        this.bardDataMap = new ConcurrentHashMap<UUID, BardData>();
        this.bardEffects = new EnumMap<Material, BardEffect>(Material.class);
        this.msgCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        this.bardRestorer = new BardRestorer(plugin);
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        this.bardEffects.put(Material.SUGAR, new BardEffect(35, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 120, 1)));
        this.bardEffects.put(Material.BLAZE_POWDER, new BardEffect(40, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0)));
        this.bardEffects.put(Material.IRON_INGOT, new BardEffect(30, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0)));
        this.bardEffects.put(Material.GHAST_TEAR, new BardEffect(25, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 120, 0)));
        this.bardEffects.put(Material.FEATHER, new BardEffect(30, new PotionEffect(PotionEffectType.JUMP, 120, 6), new PotionEffect(PotionEffectType.JUMP, 120, 1)));
        this.bardEffects.put(Material.SPIDER_EYE, new BardEffect(50, new PotionEffect(PotionEffectType.WITHER, 120, 1), null));
        this.bardEffects.put(Material.MAGMA_CREAM, new BardEffect(5, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 5L, 5L);
    }

    @Override
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
                final ItemStack held = player.getItemInHand();
                if (held == null) {
                    continue;
                }
                final BardEffect bardEffect = this.bardEffects.get(held.getType());
                if (bardEffect == null || bardEffect.heldable == null || this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
                    continue;
                }
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                if (playerFaction == null) {
                    continue;
                }
                final Collection<Entity> nearbyEntities = player.getNearbyEntities(20.0, 20.0, 20.0);
                for (final Entity nearby : nearbyEntities) {
                    if (nearby instanceof Player && !player.equals(nearby)) {
                        final Player target = (Player)nearby;
                        if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
                            continue;
                        }
                        this.bardRestorer.setRestoreEffect(target, bardEffect.heldable);
                    }
                    else {
                        if (bardEffect.heldable.getType() != PotionEffectType.JUMP) {
                            continue;
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 1));
                    }
                }
            }
        }
    }

    @Override
    public boolean onEquip(final Player player) {
        if (!ConfigurationService.KIT_MAP && this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You cannot equip classes that effect PvP while you are protected from pvp" + ChatColor.GRAY + " (" + this.getName() + ")");
            return false;
        }
        if (!super.onEquip(player)) {
            return false;
        }
        final BardData bardData = new BardData();
        this.bardDataMap.put(player.getUniqueId(), bardData);
        bardData.startEnergyTracking();
        return true;
    }

    @Override
    public void onUnequip(final Player player) {
        super.onUnequip(player);
        this.clearBardData(player.getUniqueId());
    }

    private void clearBardData(final UUID uuid) {
        this.bardDataMap.remove(uuid);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.clearBardData(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.clearBardData(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeld(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (equipped == null || !equipped.equals(this)) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final long lastMessage = this.msgCooldowns.get(uuid);
        final long millis = System.currentTimeMillis();
        if (lastMessage != this.msgCooldowns.getNoEntryValue() && lastMessage - millis > 0L) {
            return;
        }
        final ItemStack newStack = player.getInventory().getItem(event.getNewSlot());
        if (newStack != null) {
            final BardEffect bardEffect = this.bardEffects.get(newStack.getType());
            if (bardEffect != null) {
                this.msgCooldowns.put(uuid, millis + 1500L);
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }
        final Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack stack = event.getItem();
            final BardEffect bardEffect = this.bardEffects.get(stack.getType());
            if (bardEffect == null || bardEffect.clickable == null) {
                return;
            }
            event.setUseItemInHand(Event.Result.DENY);
            final Player player = event.getPlayer();
            final BardData bardData = this.bardDataMap.get(player.getUniqueId());
            if (bardData != null) {
                if (!this.canUseBardEffect(player, bardData, bardEffect, true)) {
                    return;
                }
                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                }
                else {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                if (bardEffect != null && !this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
                    final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    if (playerFaction != null && !bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                        final Collection<Entity> nearbyEntities = player.getNearbyEntities(20.0, 20.0, 20.0);
                        for (final Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                final Player target = (Player)nearby;
                                if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
                                    continue;
                                }
                                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                    else if (playerFaction != null && bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                        final Collection<Entity> nearbyEntities = player.getNearbyEntities(20.0, 20.0, 20.0);
                        for (final Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                final Player target = (Player)nearby;
                                if (playerFaction.getMembers().containsKey(target.getUniqueId())) {
                                    continue;
                                }
                                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                    else if (bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                        final Collection<Entity> nearbyEntities = player.getNearbyEntities(20.0, 20.0, 20.0);
                        for (final Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                final Player target = (Player)nearby;
                                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                }
                this.bardRestorer.setRestoreEffect(player, bardEffect.clickable);
                final double newEnergy = this.setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
                bardData.buffCooldown = System.currentTimeMillis() + BardClass.BUFF_COOLDOWN_MILLIS;
                player.sendMessage(ChatColor.YELLOW + "You have just used " + this.name + " buff " + ChatColor.AQUA + Lang.fromPotionEffectType(bardEffect.clickable.getType()) + ' ' + (bardEffect.clickable.getAmplifier() + 1) + ChatColor.YELLOW + " costing you " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.YELLOW + " energy. Your energy is now " + ChatColor.GREEN + newEnergy * 10.0 / 10.0 + ChatColor.YELLOW + '.');
            }
        }
    }

    private boolean canUseBardEffect(final Player player, final BardData bardData, final BardEffect bardEffect, final boolean sendFeedback) {
        String errorFeedback = null;
        final double currentEnergy = bardData.getEnergy();
        if (bardEffect.energyCost > currentEnergy) {
            errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.RED + " energy to use this Bard buff, whilst you only have " + ChatColor.BOLD + currentEnergy + ChatColor.RED + '.';
        }
        final long remaining = bardData.getRemainingBuffDelay();
        if (remaining > 0L) {
            errorFeedback = ChatColor.RED + "You still have a cooldown on this " + ChatColor.GREEN + ChatColor.BOLD + "Bard" + ChatColor.RED + " buff for another " + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.';
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if (factionAt.isSafezone()) {
            errorFeedback = ChatColor.RED + "You may not use Bard buffs in safe-zones.";
        }
        if (sendFeedback && errorFeedback != null) {
            player.sendMessage(errorFeedback);
        }
        return errorFeedback == null;
    }

    @Override
    public boolean isApplicableFor(final Player player) {
        final ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.GOLD_HELMET) {
            return false;
        }
        final ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) {
            return false;
        }
        final ItemStack boots = player.getInventory().getBoots();
        return boots != null && boots.getType() == Material.GOLD_BOOTS;
    }

    public long getRemainingBuffDelay(final Player player) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        return (bardData == null) ? 0L : bardData.getRemainingBuffDelay();
    }

    public long getRemainingBuffDelay(final Player player, final long now) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        return (bardData == null) ? 0L : bardData.getRemainingBuffDelay(now);
    }

    public double getEnergy(final Player player) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        return (bardData == null) ? 0.0 : bardData.getEnergy();
    }

    public long getEnergyMillis(final Player player) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        return (bardData == null) ? 0L : bardData.getEnergyMillis();
    }

    public long getEnergyMillis(final Player player, final long now) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        return (bardData == null) ? 0L : bardData.getEnergyMillis(now);
    }

    public double setEnergy(final Player player, final double energy) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        if (bardData == null) {
            return 0.0;
        }
        bardData.setEnergy(energy);
        return bardData.getEnergy();
    }

    static {
        BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(6L);
    }
}