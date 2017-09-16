
package com.customhcf.hcf.classes.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.customhcf.hcf.classes.PvpClass;

public class PvpClassEquipEvent
extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final PvpClass pvpClass;

    public PvpClassEquipEvent(Player player, PvpClass pvpClass) {
        super(player);
        this.pvpClass = pvpClass;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PvpClass getPvpClass() {
        return this.pvpClass;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

