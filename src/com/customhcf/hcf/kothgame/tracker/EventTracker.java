
package com.customhcf.hcf.kothgame.tracker;

import org.bukkit.entity.Player;

import com.customhcf.hcf.kothgame.CaptureZone;
import com.customhcf.hcf.kothgame.EventTimer;
import com.customhcf.hcf.kothgame.EventType;
import com.customhcf.hcf.kothgame.faction.EventFaction;

@Deprecated
public interface EventTracker {
    EventType getEventType();

    void tick(EventTimer var1, EventFaction var2);

    void onContest(EventFaction var1, EventTimer var2);

    boolean onControlTake(Player var1, CaptureZone var2);

    boolean onControlLoss(Player var1, CaptureZone var2, EventFaction var3);

    void stopTiming();
}

