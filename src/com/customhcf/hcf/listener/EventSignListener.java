
package com.customhcf.hcf.listener;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.DateTimeFormats;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventSignListener
implements Listener {
    private static final String EVENT_SIGN_ITEM_NAME = (Object)ChatColor.GOLD + "Event Sign";

    public static ItemStack getEventSign(String playerName, String kothName) {
        ItemStack stack = new ItemStack(Material.SIGN, 1);
        ItemMeta meta = stack.getItemMeta();
        String name = (Object)ChatColor.AQUA + kothName;
        meta.setDisplayName(EVENT_SIGN_ITEM_NAME);
        meta.setLore((List)Lists.newArrayList((Object[])new String[]{(Object)ChatColor.AQUA + HCF.getPlugin().getFactionManager().getFaction(playerName).getName(), (Object)ChatColor.GRAY + "captured by", (Object)ChatColor.AQUA + ChatColor.stripColor((String)name), (Object)ChatColor.GRAY + DateTimeFormats.DAY_MTH_HR_MIN.format(System.currentTimeMillis())}));
        stack.setItemMeta(meta);
        return stack;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {
        if (this.isEventSign(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (this.isEventSign(block)) {
            BlockState state = block.getState();
            Sign sign = (Sign)state;
            ItemStack stack = new ItemStack(Material.SIGN, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(EVENT_SIGN_ITEM_NAME);
            meta.setLore(Arrays.asList(sign.getLines()));
            stack.setItemMeta(meta);
            Player player = event.getPlayer();
            World world = player.getWorld();
            if (player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")) {
                world.dropItemNaturally(block.getLocation(), stack);
            }
            event.setCancelled(true);
            block.setType(Material.AIR);
            state.update();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final ItemStack stack = event.getItemInHand();
        final BlockState state = event.getBlock().getState();
        if (state instanceof Sign && stack.hasItemMeta()) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().equals(EventSignListener.EVENT_SIGN_ITEM_NAME)) {
                final Sign sign = (Sign)state;
                final List<String> lore = (List<String>)meta.getLore();
                int count = 0;
                for (final String loreLine : lore) {
                    sign.setLine(count++, loreLine);
                    if (count == 4) {
                        break;
                    }
                }
                sign.update();
                sign.setEditible(false);
            }
        }
    }

    private boolean isEventSign(Block block) {
        BlockState state = block.getState();
        if (state instanceof Sign) {
            String[] lines = ((Sign)state).getLines();
            return lines.length > 0 && lines[1] != null && lines[1].equals((Object)ChatColor.GRAY + "captured by");
        }
        return false;
    }
}

