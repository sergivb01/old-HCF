
package com.customhcf.hcf.faction.type;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.faction.claim.Claim;

public class SpawnFaction
extends ClaimableFaction
implements ConfigurationSerializable {
    public SpawnFaction() {
        super("Spawn");
        this.safezone = true;
        for (World world : Bukkit.getWorlds()) {
            World.Environment environment = world.getEnvironment();
            if (environment == World.Environment.THE_END) continue;
            double radius = ConfigurationService.SPAWN_RADIUS_MAP.get((Object)world.getEnvironment());
//            this.addClaim(new Claim(this, new Location(world, radius, 0.0, radius), new Location(world, - radius, (double)world.getMaxHeight(), - radius)), null);
        }
    }

    public SpawnFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public boolean isDeathban() {
        return false;
    }
}

