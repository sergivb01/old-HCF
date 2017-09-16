
package com.customhcf.hcf.faction.claim;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.struct.Role;
import com.customhcf.hcf.faction.type.ClaimableFaction;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.faction.type.RoadFaction;
import com.customhcf.hcf.faction.type.WildernessFaction;
import com.customhcf.hcf.visualise.VisualType;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.cuboid.Cuboid;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClaimHandler {
    public static final int MIN_CLAIM_HEIGHT = 0;
    public static final int MAX_CLAIM_HEIGHT = 256;
    public static final ItemStack CLAIM_WAND = new ItemBuilder(Material.DIAMOND_HOE).displayName(ChatColor.LIGHT_PURPLE.toString() + "Claim Wand").lore(new String[]{(Object)ChatColor.YELLOW + "Left or Right Click " + (Object)ChatColor.RED + "a Block" + (Object)ChatColor.YELLOW + " to:", (Object)ChatColor.GRAY + "Set the first and second position of ", (Object)ChatColor.GRAY + "your Claim selection.", "", (Object)ChatColor.YELLOW + "Right Click " + (Object)ChatColor.RED + "the Air" + (Object)ChatColor.YELLOW + " to:", (Object)ChatColor.GRAY + "Clear your current Claim selection.", "", (Object)ChatColor.YELLOW + "Shift " + (Object)ChatColor.YELLOW + "Left Click " + (Object)ChatColor.RED + "the Air or a Block" + (Object)ChatColor.YELLOW + " to:", (Object)ChatColor.GRAY + "Purchase your current Claim selection."}).build();
    public static final int MIN_CLAIM_RADIUS = 5;
    public static final int MAX_CHUNKS_PER_LIMIT = 16;
    private static final int NEXT_PRICE_MULTIPLIER_AREA = 250;
    private static final int NEXT_PRICE_MULTIPLIER_CLAIM = 500;
    private static final double CLAIM_SELL_MULTIPLIER = 0.8;
    private static final double CLAIM_PRICE_PER_BLOCK = 0.25;
    public final ConcurrentMap<Object, Object> claimSelectionMap;
    private final HCF plugin;

    public ClaimHandler(HCF plugin) {
        this.plugin = plugin;
        this.claimSelectionMap = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build().asMap();
    }

    public int calculatePrice(Cuboid claim, int currentClaims, boolean selling) {
        if (currentClaims == -1 || !claim.hasBothPositionsSet()) {
            return 0;
        }
        int multiplier = 1;
        int remaining = claim.getArea();
        double price = 0.0;
        while (remaining > 0) {
            if (--remaining % 250 == 0) {
                ++multiplier;
            }
            price += 0.25 * (double)multiplier;
        }
        if (currentClaims != 0) {
            currentClaims = Math.max(currentClaims + (selling ? -1 : 0), 0);
            price += (double)(currentClaims * 500);
        }
        if (selling) {
            price *= 0.8;
        }
        return (int)price;
    }

    public boolean clearClaimSelection(Player player) {
        ClaimSelection claimSelection = (ClaimSelection)this.plugin.getClaimHandler().claimSelectionMap.remove(player.getUniqueId());
        if (claimSelection != null) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CREATE_CLAIM_SELECTION, null);
            return true;
        }
        return false;
    }

    public boolean canSubclaimHere(Player player, Location location) {
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage((Object)ChatColor.RED + "You must be in a faction to subclaim land.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage((Object)ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if (!this.plugin.getFactionManager().getFactionAt(location).equals(playerFaction)) {
            player.sendMessage((Object)ChatColor.RED + "This location is not part of your factions' territory.");
            return false;
        }
        return true;
    }

    public boolean canClaimHere(Player player, Location location) {
        World world = location.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage((Object)ChatColor.RED + "You can only claim land in the Overworld.");
            return false;
        }
        if (!(this.plugin.getFactionManager().getFactionAt(location) instanceof WildernessFaction)) {
            player.sendMessage((Object)ChatColor.RED + "You can only claim land in the " + (Object)ConfigurationService.WILDERNESS_COLOUR + "Wilderness" + (Object)ChatColor.RED + ". " + "Make sure you are past " + 1000 + " blocks from spawn..");
            return false;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage((Object)ChatColor.RED + "You must be in a faction to claim land.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage((Object)ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if (playerFaction.getClaims().size() >= 8) {
            player.sendMessage((Object)ChatColor.RED + "Your faction has maximum claims - " + 8);
            return false;
        }
        int locX = location.getBlockX();
        int locZ = location.getBlockZ();
        FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = locX - 5; x < locX + 5; ++x) {
            for (int z = locZ - 5; z < locZ + 5; ++z) {
                Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if (!(factionAtNew instanceof RoadFaction)) {
                    // empty if block
                }
                if (playerFaction.equals(factionAtNew) || !(factionAtNew instanceof ClaimableFaction)) continue;
                player.sendMessage((Object)ChatColor.RED + "This position contains enemy claims within a " + 5 + " block buffer radius.");
                return false;
            }
        }
        return true;
    }

    public boolean tryPurchasing(Player player, Claim claim) {
        int z;
        int x;
        Preconditions.checkNotNull((Object)claim, (Object)"Claim is null");
        World world = claim.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage((Object)ChatColor.RED + "You can only claim land in the Overworld.");
            return false;
        }
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            player.sendMessage((Object)ChatColor.RED + "You must be in a faction to claim land.");
            return false;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            player.sendMessage((Object)ChatColor.RED + "You must be an officer to claim land.");
            return false;
        }
        if (playerFaction.getClaims().size() >= 8) {
            player.sendMessage((Object)ChatColor.RED + "Your faction has maximum claims - " + 8);
            return false;
        }
        int factionBalance = playerFaction.getBalance();
        int claimPrice = this.calculatePrice((Cuboid)claim, playerFaction.getClaims().size(), false);
        if (claimPrice > factionBalance) {
            player.sendMessage((Object)ChatColor.RED + "Your faction bank only has " + '$' + factionBalance + ", the price of this claim is " + '$' + claimPrice + '.');
            return false;
        }
        if (claim.getChunks().size() > 16) {
            player.sendMessage((Object)ChatColor.RED + "Claims cannot exceed " + 16 + " chunks.");
            return false;
        }
        if (claim.getWidth() < 5 || claim.getLength() < 5) {
            player.sendMessage((Object)ChatColor.RED + "Claims must be at least " + 5 + 'x' + 5 + " blocks.");
            return false;
        }
        int minimumX = claim.getMinimumX();
        int maximumX = claim.getMaximumX();
        int minimumZ = claim.getMinimumZ();
        int maximumZ = claim.getMaximumZ();
        FactionManager factionManager = this.plugin.getFactionManager();
        for (x = minimumX; x < maximumX; ++x) {
            for (z = minimumZ; z < maximumZ; ++z) {
                Faction factionAt = factionManager.getFactionAt(world, x, z);
                if (factionAt == null || factionAt instanceof WildernessFaction) continue;
                player.sendMessage((Object)ChatColor.RED + "This claim contains a location not within the " + (Object)ChatColor.GRAY + "Wilderness" + (Object)ChatColor.RED + '.');
                return false;
            }
        }
        for (x = minimumX - 1; x < maximumX + 1; ++x) {
            for (z = minimumZ - 1; z < maximumZ + 1; ++z) {
                Faction factionAtNew = factionManager.getFactionAt(world, x, z);
                if (!(factionAtNew instanceof RoadFaction)) {
                    // empty if block
                }
                if (playerFaction.equals(factionAtNew) || !(factionAtNew instanceof ClaimableFaction)) continue;
                player.sendMessage((Object)ChatColor.RED + "This claim contains enemy claims within a " + 1 + " block buffer radius.");
                return false;
            }
        }
        Location minimum = claim.getMinimumPoint();
        Location maximum = claim.getMaximumPoint();
        Set<Claim> otherClaims = playerFaction.getClaims();
        boolean conjoined = otherClaims.isEmpty();
        if (!conjoined) {
            player.sendMessage((Object)ChatColor.RED + "Use /f unclaim to resize your faction claims.");
            return false;
        }
        claim.setY1(0);
        claim.setY2(256);
        if (!playerFaction.addClaim(claim, (CommandSender)player)) {
            return false;
        }
        Location center = claim.getCenter();
        player.sendMessage((Object)ChatColor.YELLOW + "Claim has been purchased for " + (Object)ChatColor.GREEN + '$' + claimPrice + (Object)ChatColor.YELLOW + '.');
        playerFaction.setBalance(factionBalance - claimPrice);
        playerFaction.broadcast((Object)ChatColor.GOLD + player.getName() + (Object)ChatColor.GREEN + " claimed land for your faction at " + (Object)ChatColor.GOLD + '(' + center.getBlockX() + ", " + center.getBlockZ() + ')' + (Object)ChatColor.GREEN + '.', player.getUniqueId());
        return true;
    }
}

