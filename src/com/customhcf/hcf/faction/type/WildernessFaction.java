
package com.customhcf.hcf.faction.type;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.customhcf.hcf.Utils.ConfigurationService;

public class WildernessFaction
extends Faction {
    public WildernessFaction() {
        super("Wilderness");
    }

    public WildernessFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return (Object)ConfigurationService.WILDERNESS_COLOUR + this.getName();
    }
}

