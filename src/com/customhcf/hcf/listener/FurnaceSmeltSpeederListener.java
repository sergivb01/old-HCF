package com.customhcf.hcf.listener;

import com.customhcf.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class FurnaceSmeltSpeederListener implements Listener {

    public FurnaceSmeltSpeederListener(HCF plugin) {
        final ShapedRecipe cmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
        cmelon.shape("AAA", "CBA", "AAA").setIngredient('B', Material.MELON).setIngredient('C', Material.GOLD_NUGGET);
        Bukkit.getServer().addRecipe(cmelon);
    }

    /*private void startUpdate2(final BrewingStand tile, final int increase) {
        new BukkitRunnable() {
            public void run() {
                if (tile.getBrewingTime() > 0) {
                    tile.setBrewingTime((short) (tile.getBrewingTime() + increase));
                    tile.update();
                }else{
                    cancel();
                }
            }
        }.runTaskTimer(HCF.getInstance(), 1L, 1L);
    }

    private void startUpdate(final Furnace tile, final int increase){
        new BukkitRunnable(){
            public void run(){
                if ((tile.getCookTime() > 0) || (tile.getBurnTime() > 0)){
                    tile.setCookTime((short)(tile.getCookTime() + increase));
                    tile.update();
                }else{
                    cancel();
                }
            }
        }.runTaskTimer(HCF.getInstance(), 1L, 1L);
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event){
        startUpdate((Furnace)event.getBlock().getState(), 3);
    }

    @EventHandler
    public void onBrew(BrewEvent event){
        startUpdate2((BrewingStand) event.getBlock().getState(), 5);
    }*/


}