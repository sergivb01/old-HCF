
package com.customhcf.hcf.fixes;

import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.config.PotionLimiterData;

public class PotionLimitListener
  implements Listener
{
  private static HCF plugin;
  private static List<Short> disabledPotions;
  private static PotionLimiterData limiter;
  
  public boolean isPotionDisabled(ItemStack item)
  {
    return item.getType() == Material.POTION ? disabledPotions.contains(Short.valueOf(item.getDurability())) : false;
  }
  
  public static void init() {}
  
  public static void reload()
  {
    disabledPotions = PotionLimiterData.getInstance().getConfig().getShortList("disabled-potions");
    System.out.println(disabledPotions.toString());
  }
  
  @EventHandler
  public void onSplash(PotionSplashEvent event)
  {
    if (isPotionDisabled(event.getPotion().getItem()))
    {
      event.setCancelled(true);
      ProjectileSource shooter = event.getEntity().getShooter();
      if ((shooter instanceof Player)) {
        ((Player)shooter).sendMessage(ChatColor.RED + "You cannot use this potions. " + event.getPotion().getItem().getType());
      }
    }
  }
  
  @EventHandler
  public void onPlayerItemConsume(PlayerItemConsumeEvent event)
  {
    if (isPotionDisabled(event.getItem()))
    {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this potions. " + event.getItem().getType());
    }
  }
  
  @EventHandler
  public void onBrew(BrewEvent e)
  {
    BrewerInventory inventory = e.getContents();
    BrewingStand stand = inventory.getHolder();
    stand.setBrewingTime(200);
    if (isPotionDisabled(e.getContents().getItem(0))) {
      e.setCancelled(true);
    }
  }
}

