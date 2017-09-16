
package com.customhcf.hcf.faction;

import com.customhcf.base.BasePlugin;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.claim.Claim;
import com.customhcf.hcf.faction.type.ClaimableFaction;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.visualise.VisualBlockData;
import com.customhcf.hcf.visualise.VisualType;
import com.customhcf.hcf.visualise.VisualiseHandler;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.itemdb.ItemDb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandMap {
    private static final int FACTION_MAP_RADIUS_BLOCKS = 22;

    public static boolean updateMap(Player player, HCF plugin, VisualType visualType, boolean inform) {
        Location location = player.getLocation();
        World world = player.getWorld();
        int locationX = location.getBlockX();
        int locationZ = location.getBlockZ();
        int minimumX = locationX - FACTION_MAP_RADIUS_BLOCKS;
        int minimumZ = locationZ - FACTION_MAP_RADIUS_BLOCKS;
        int maximumX = locationX + FACTION_MAP_RADIUS_BLOCKS;
        int maximumZ = locationZ + FACTION_MAP_RADIUS_BLOCKS;
        LinkedHashSet<Claim> board = new LinkedHashSet<Claim>();
        if (visualType != VisualType.CLAIM_MAP) {
            player.sendMessage((Object)ChatColor.RED + "Not supported: " + visualType.name().toLowerCase() + '.');
            return false;
        }
        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
                if (claim == null || claim.getFaction() == null) continue;
                board.add(claim);
            }
        }
        if (board.isEmpty()) {
            player.sendMessage((Object)ChatColor.RED + "No claims are in your visual range to display.");
            return false;
        }
        for (Claim claim2 : board) {
            int maxHeight = Math.min(world.getMaxHeight(), 256);
            Location[] corners = claim2.getCornerLocations();
            ArrayList<Location> shown = new ArrayList<Location>(maxHeight * corners.length);
            for (Location corner : corners) {
                for (int y = 0; y < maxHeight; ++y) {
                    shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
                }
            }
            LinkedHashMap<Location, VisualBlockData> dataMap = plugin.getVisualiseHandler().generate(player, shown, visualType, true);
            if (dataMap.isEmpty()) continue;
            String materialName = (Object)ChatColor.RED + "Error!";
            for (VisualBlockData visualBlockData : dataMap.values()) {
                if (visualBlockData.getItemType() == Material.STAINED_GLASS) continue;
                materialName = BasePlugin.getPlugin().getItemDb().getName(new ItemStack(visualBlockData.getItemType()));
                break;
            }
            if (!inform) continue;
            player.sendMessage(claim2.getFaction().getDisplayName((CommandSender)player) + (Object)ChatColor.YELLOW + " owns claim displayed by the " + (Object)ChatColor.AQUA + materialName);
        }
        return true;
    }

    public static Location getNearestSafePosition(Player player, Location origin, int searchRadius) {
        FactionManager factionManager = HCF.getPlugin().getFactionManager();
        PlayerFaction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
        for (int x = 16; x < searchRadius; ++x) {
            for (int z = 16; z < searchRadius; ++z) {
                Location atPos = origin.clone().add((double)x, 0.0, (double)z);
                Faction factionAtPos = factionManager.getFactionAt(atPos);
                if (Objects.equals(factionAtPos, playerFaction) || !(factionAtPos instanceof PlayerFaction)) {
                    return BukkitUtils.getHighestLocation((Location)atPos, (Location)atPos);
                }
                Location atNeg = origin.clone().add((double)x, 0.0, (double)z);
                Faction factionAtNeg = factionManager.getFactionAt(atNeg);
                if (!Objects.equals(factionAtNeg, playerFaction) && factionAtNeg instanceof PlayerFaction) continue;
                return BukkitUtils.getHighestLocation((Location)atNeg, (Location)atNeg);
            }
        }
        return null;
    }
}

