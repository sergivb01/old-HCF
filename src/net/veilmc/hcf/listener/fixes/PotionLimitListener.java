
package net.veilmc.hcf.listener.fixes;

import net.md_5.bungee.api.ChatColor;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.config.PotionLimiterData;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public class PotionLimitListener
  implements Listener
{
  private static HCF plugin;
  private static List<Short> disabledPotions;
  private static PotionLimiterData limiter;
  
  public boolean isPotionDisabled(ItemStack item)
  {
    return item.getType() == Material.POTION && disabledPotions.contains(Short.valueOf(item.getDurability()));
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
        ((Player)shooter).sendMessage(ChatColor.RED + "You cannot use this potion.");
          ((Player) shooter).getPlayer().setItemInHand(null);
      }
    }
  }
  
  @EventHandler
  public void onPlayerItemConsume(PlayerItemConsumeEvent event)
  {
    if (isPotionDisabled(event.getItem()))
    {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this potion.");
      event.getPlayer().setItemInHand(null);
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

