package com.customhcf.hcf.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import com.customhcf.hcf.HCF;

public class FurnaceSmeltSpeederListener
implements Listener {
	public FurnaceSmeltSpeederListener() {
		final ShapedRecipe cmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
		cmelon.shape(new String[] { "AAA", "CBA", "AAA" }).setIngredient('B', Material.MELON).setIngredient('C', Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(cmelon);
		}
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block;
        BlockState state;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (state = (block = event.getClickedBlock()).getState()) instanceof Furnace) {
            ((Furnace)state).setCookSpeedMultiplier(6.0);
        }
    }
    private void startUpdate(final Furnace tile, final int increase) {
		new BukkitRunnable() {
			public void run() {
				if ((tile.getCookTime() > 0) || (tile.getBurnTime() > 0)) {
					tile.setCookTime((short) (tile.getCookTime() + increase));
					tile.update();
				} else {
					cancel();
				}
			}
		}.runTaskTimer(HCF.getPlugin(), 1L, 1L);
	}

	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent event) {
		Random RND = new Random();
		startUpdate((Furnace) event.getBlock().getState(), RND.nextBoolean() ? 1 : 2);
	}
}


