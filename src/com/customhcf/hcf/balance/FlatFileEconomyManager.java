package com.customhcf.hcf.balance;

import com.customhcf.hcf.balance.EconomyManager;
import com.customhcf.util.Config;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

public class FlatFileEconomyManager
implements EconomyManager {
    private final JavaPlugin plugin;
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private TObjectIntMap<UUID> balanceMap = new TObjectIntHashMap(10, 0.5f, 0);
    private Config balanceConfig;

    public FlatFileEconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reloadEconomyData();
    }

    @Override
    public TObjectIntMap<UUID> getBalanceMap() {
        return this.balanceMap;
    }

    @Override
    public int getBalance(UUID uuid) {
        return this.balanceMap.get((Object)uuid);
    }

    @Override
    public int setBalance(UUID uuid, int amount) {
        this.balanceMap.put(uuid, amount);
        return amount;
    }

    @Override
    public int addBalance(UUID uuid, int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) + amount);
    }

    @Override
    public int subtractBalance(UUID uuid, int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) - amount);
    }


    @Override
    public void reloadEconomyData() {
        this.balanceConfig = new Config(this.plugin, "balances");
        final Object object = this.balanceConfig.get("balances");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            for (final String id : keys) {
                this.balanceMap.put(UUID.fromString(id), this.balanceConfig.getInt("balances." + id));
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void saveEconomyData() {
        LinkedHashMap saveMap = new LinkedHashMap(this.balanceMap.size());
        this.balanceMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        }
        );
        this.balanceConfig.set("balances", saveMap);
        this.balanceConfig.save();
    }
}

