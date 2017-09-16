
package com.customhcf.hcf.kothgame;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.kothgame.tracker.ConquestTracker;
import com.customhcf.hcf.kothgame.tracker.EventTracker;
import com.customhcf.hcf.kothgame.tracker.KothTracker;
import com.customhcf.hcf.palace.PalaceTracker;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;


public enum EventType
{
    CONQUEST("Conquest", new ConquestTracker(HCF.getPlugin())), PALACE("Palace", new PalaceTracker((HCF.getPlugin()))), KOTH("Koth", new KothTracker(HCF.getPlugin()));

    private static final ImmutableMap<String, EventType> byDisplayName;
    private final EventTracker eventTracker;
    private final String displayName;

    private EventType(String displayName, EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }

    @Deprecated
    public static EventType getByDisplayName(String name) {
        return (EventType)((Object)byDisplayName.get((Object)name.toLowerCase()));
    }

    public EventTracker getEventTracker() {
        return this.eventTracker;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    static {
        ImmutableBiMap.Builder builder = new ImmutableBiMap.Builder();
        for (EventType eventType : EventType.values()) {
            builder.put((Object)eventType.displayName.toLowerCase(), (Object)eventType);
        }
        byDisplayName = builder.build();
    }
}

