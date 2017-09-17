
package com.customhcf.hcf.faction.type;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.customhcf.hcf.Utils.ConfigurationService;

public class WarzoneFaction
extends Faction {
    public WarzoneFaction() {
        super("Warzone");
    }

    public WarzoneFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ConfigurationService.WARZONE_COLOUR + this.getName();
    }
}

