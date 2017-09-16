package com.customhcf.hcf.balance;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public interface EconomyManager {
    public static final char ECONOMY_SYMBOL = '$';

    public TObjectIntMap<UUID> getBalanceMap();

    public int getBalance(UUID var1);

    public int setBalance(UUID var1, int var2);

    public int addBalance(UUID var1, int var2);

    public int subtractBalance(UUID var1, int var2);

    public void reloadEconomyData();

    public void saveEconomyData();
}

