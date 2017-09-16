
package com.customhcf.hcf.faction.type;

import java.util.Map;

import com.customhcf.hcf.Utils.ConfigurationService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.customhcf.hcf.faction.claim.Claim;
import com.customhcf.hcf.faction.type.ClaimableFaction;
import com.customhcf.hcf.faction.type.Faction;

public class GlowstoneFaction
        extends ClaimableFaction
        implements ConfigurationSerializable {
    public GlowstoneFaction() {
        super("Glowstone");
        this.safezone = false;
    }

    public GlowstoneFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return (Object)ChatColor.GOLD + this.getName().replace("Glowstone", "Glowstone");
    }

    @Override
    public boolean isDeathban() {
        return true;
    }
}

