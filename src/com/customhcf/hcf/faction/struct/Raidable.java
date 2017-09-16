
package com.customhcf.hcf.faction.struct;

import com.customhcf.hcf.faction.struct.RegenStatus;

public interface Raidable {
    public boolean isRaidable();

    public double getDeathsUntilRaidable();

    public double getMaximumDeathsUntilRaidable();

    public double setDeathsUntilRaidable(double var1);

    public long getRemainingRegenerationTime();

    public void setRemainingRegenerationTime(long var1);

    public RegenStatus getRegenStatus();
}

