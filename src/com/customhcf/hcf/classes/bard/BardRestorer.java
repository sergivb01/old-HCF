
package com.customhcf.hcf.classes.bard;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.classes.event.PvpClassUnequipEvent;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.sergivb01.event.PotionEffectExpiresEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.UUID;

public class BardRestorer
implements Listener {
    private final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public BardRestorer(HCF plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPvpClassUnequip(PvpClassUnequipEvent event) {
        this.restores.rowKeySet().remove(event.getPlayer().getUniqueId());
    }

    public void setRestoreEffect(final Player player, final PotionEffect effect) {
        boolean shouldCancel = true;
        final Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (final PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) {
                continue;
            }
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            }
            if (effect.getAmplifier() == active.getAmplifier() && effect.getDuration() < active.getDuration()) {
                return;
            }
            this.restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 100 && effect.getDuration() < BardClass.DEFAULT_MAX_DURATION) {
            this.restores.remove(player.getUniqueId(), effect.getType());
        }
    }
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpiresEvent event) {
        PotionEffect previous;
        Player player;
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player && (previous = this.restores.remove((player = (Player)livingEntity).getUniqueId(), event.getEffect().getType())) != null) {
            event.setCancelled(true);
            player.addPotionEffect(previous, true);
        }
    }
}

