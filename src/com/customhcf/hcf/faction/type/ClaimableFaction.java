
package com.customhcf.hcf.faction.type;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.claim.Claim;
import com.customhcf.hcf.faction.event.FactionClaimChangeEvent;
import com.customhcf.hcf.faction.event.FactionClaimChangedEvent;
import com.customhcf.hcf.faction.event.cause.ClaimChangeCause;
import com.customhcf.util.GenericUtils;
import com.customhcf.util.cuboid.Cuboid;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import java.util.*;

public class ClaimableFaction
extends Faction {
    protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS =
            (ImmutableMap.of(World.Environment.NETHER, "Nether", World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End"));
    protected final Set<Claim> claims = new HashSet<Claim>();

    public ClaimableFaction(String name) {
        super(name);
    }

    public ClaimableFaction(Map<String, Object> map) {
        super(map);
        this.claims.addAll(GenericUtils.createList((Object)map.get("claims"), (Class)Claim.class));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("claims", new ArrayList<Claim>(this.claims));
        return map;
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
        sender.sendMessage(this.getDisplayName(sender));
        for (Claim claim : this.claims) {
            Location location = claim.getCenter();
            sender.sendMessage((Object)ChatColor.YELLOW + "  Location: " + ChatColor.WHITE.toString() + (String)ENVIRONMENT_MAPPINGS.get((Object)location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
        }
        sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
    }

    public Set<Claim> getClaims() {
        return this.claims;
    }

    public boolean addClaim(Claim claim, CommandSender sender) {
        return this.addClaims(Collections.singleton(claim), sender);
    }

    public boolean addClaims(Collection<Claim> adding, CommandSender sender) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }
        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled() || !this.claims.addAll(adding)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
        return true;
    }

    public boolean removeClaim(Claim claim, CommandSender sender) {
        return this.removeClaims(Collections.singleton(claim), sender);
    }

    public boolean removeClaims(Collection<Claim> removing, CommandSender sender) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }
        int previousClaims = this.claims.size();
        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        
        if (event.isCancelled()) {
            return false;
        }
        for(Claim claim : Lists.newArrayList(removing)){
        	while(claims.contains(claim)){
        		claims.remove(claim);
        	}
        }
        if (this instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction)this;
            Location home = playerFaction.getHome();
            HCF plugin = HCF.getPlugin();
            int refund = 0;
            for (Claim claim : removing) {
                refund += plugin.getClaimHandler().calculatePrice((Cuboid)claim, previousClaims, true);
                if (previousClaims > 0) {
                    --previousClaims;
                }
                if (home == null || !claim.contains(home)) continue;
                playerFaction.setHome(null);
                playerFaction.broadcast(ChatColor.RED.toString() + (Object)ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
                break;
            }
            plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
            playerFaction.broadcast((Object)ChatColor.YELLOW + "Faction leader was refunded " + (Object)ChatColor.GREEN + '$' + refund + (Object)ChatColor.YELLOW + " due to a land unclaim.");
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
        return true;
    }
}

