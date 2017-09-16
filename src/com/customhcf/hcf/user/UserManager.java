
package com.customhcf.hcf.user;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.user.FactionUser;
import com.customhcf.util.Config;
import com.google.common.base.MoreObjects;

import java.util.*;

import org.bukkit.Server;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UserManager
implements Listener {
    private final HCF plugin;
    private final Map<UUID, FactionUser> users = new HashMap<UUID, FactionUser>();
    private Config userConfig;

    public UserManager(HCF plugin) {
        this.plugin = plugin;
        this.reloadUserData();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        this.users.putIfAbsent(uuid, new FactionUser(uuid));
    }

    public Map<UUID, FactionUser> getUsers() {
        return this.users;
    }

    
    public FactionUser getUserAsync(UUID uuid) {
        Map<UUID, FactionUser> map = this.users;
        synchronized (map) {
            FactionUser revert = new FactionUser(uuid);
            FactionUser user = this.users.putIfAbsent(uuid, revert);
            return (FactionUser)MoreObjects.firstNonNull((Object)user, (Object)revert);
        }
    }

    public FactionUser getUser(UUID uuid) {
        FactionUser revert = new FactionUser(uuid);
        FactionUser user = this.users.putIfAbsent(uuid, revert);
        return (FactionUser)MoreObjects.firstNonNull((Object)user, (Object)revert);
    }

    public void reloadUserData() {
        this.userConfig = new Config(this.plugin, "faction-users");
        final Object object = this.userConfig.get("users");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Collection<String> keys = (Collection<String>)section.getKeys(false);
            for (final String id : keys) {
                this.users.put(UUID.fromString(id), (FactionUser)this.userConfig.get(section.getCurrentPath() + '.' + id));
            }
        }
    }

    public void saveUserData() {
        Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
        LinkedHashMap<String, FactionUser> saveMap = new LinkedHashMap<String, FactionUser>(entrySet.size());
        for (Map.Entry<UUID, FactionUser> entry : entrySet) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("users", saveMap);
        this.userConfig.save();
    }
}

