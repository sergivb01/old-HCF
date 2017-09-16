
package com.customhcf.hcf.kothgame.faction;

import java.util.Map;

import com.customhcf.hcf.kothgame.faction.EventFaction;

public abstract class CapturableFaction
extends EventFaction {
    public CapturableFaction(String name) {
        super(name);
    }

    public CapturableFaction(Map<String, Object> map) {
        super(map);
    }
}

