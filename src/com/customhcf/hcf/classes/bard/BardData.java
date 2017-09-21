package com.customhcf.hcf.classes.bard;

import com.google.common.base.Preconditions;

public class BardData
{
    public static final double MIN_ENERGY = 0.0;
    public static final double MAX_ENERGY = 100.0;
    public static final long MAX_ENERGY_MILLIS = 100000L;
    private static final double ENERGY_PER_MILLISECOND = 1.25;
    public long buffCooldown;
    private long energyStart;

    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }

    public long getRemainingBuffDelay(final long now) {
        return this.buffCooldown - now;
    }

    public void startEnergyTracking() {
        this.setEnergy(0.0);
    }

    public long getEnergyMillis() {
        if (this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(100000L, (long)(1.25 * (System.currentTimeMillis() - this.energyStart)));
    }

    public long getEnergyMillis(final long now) {
        if (this.energyStart == 0L) {
            return 0L;
        }
        return Math.min(100000L, (long)(1.25 * (now - this.energyStart)));
    }

    public double getEnergy() {
        final double value = this.getEnergyMillis() / 1000.0;
        return Math.round(value * 10.0) / 10.0;
    }

    public void setEnergy(final double energy) {
        Preconditions.checkArgument(energy >= 0.0, "Energy cannot be less than 0.0");
        Preconditions.checkArgument(energy <= 100.0, "Energy cannot be more than 100.0");
        this.energyStart = (long)(System.currentTimeMillis() - 1000.0 * energy);
    }
}
