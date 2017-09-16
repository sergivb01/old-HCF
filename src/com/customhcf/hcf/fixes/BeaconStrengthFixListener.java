
package com.customhcf.hcf.fixes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconStrengthFixListener
implements Listener {
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onPotionEffectAdd(PotionEffectAddEvent event) {
        PotionEffect effect;
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player && event.getCause() == PotionEffectAddEvent.EffectCause.BEACON && (effect = event.getEffect()).getAmplifier() > 1 && effect.getType().equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
            entity.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), 0, effect.isAmbient()));
            event.setCancelled(true);
        }
    }
}

