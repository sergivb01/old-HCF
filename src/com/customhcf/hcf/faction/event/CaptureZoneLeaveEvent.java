
package com.customhcf.hcf.faction.event;

import com.customhcf.hcf.faction.event.FactionEvent;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.kothgame.CaptureZone;
import com.customhcf.hcf.kothgame.faction.CapturableFaction;
import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class CaptureZoneLeaveEvent
extends FactionEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final CaptureZone captureZone;
    private final Player player;
    private boolean cancelled;

    public CaptureZoneLeaveEvent(Player player, CapturableFaction capturableFaction, CaptureZone captureZone) {
        super(capturableFaction);
        Preconditions.checkNotNull((Object)player, "Player cannot be null");
        Preconditions.checkNotNull((Object)captureZone, "Capture zone cannot be null");
        this.captureZone = captureZone;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public CapturableFaction getFaction() {
        return (CapturableFaction)super.getFaction();
    }

    public CaptureZone getCaptureZone() {
        return this.captureZone;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

