
package com.customhcf.hcf.faction.struct;

import com.customhcf.hcf.faction.struct.RegenStatus;

public interface Raidable {
    boolean isRaidable();

    double getDeathsUntilRaidable();

    double getMaximumDeathsUntilRaidable();

    double setDeathsUntilRaidable(double var1);

    long getRemainingRegenerationTime();

    void setRemainingRegenerationTime(long var1);

    RegenStatus getRegenStatus();
}

