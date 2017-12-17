package net.veilmc.hcf.faction.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class ThimbleFaction
        extends ClaimableFaction
        implements ConfigurationSerializable {
    public ThimbleFaction() {
        super("Thimble");
        this.safezone = false;
    }

    public ThimbleFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.LIGHT_PURPLE + this.getName().replace("Glowstone", "Glowstone");
    }

    @Override
    public boolean isDeathban() {
        return false;
    }
}

