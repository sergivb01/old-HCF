
package com.customhcf.hcf.faction;

import com.customhcf.hcf.faction.struct.ChatChannel;
import com.customhcf.hcf.faction.struct.Role;
import com.google.common.base.Enums;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class FactionMember
implements ConfigurationSerializable {
    private final UUID uniqueID;
    private ChatChannel chatChannel;
    private Role role;

    public FactionMember(Player player, ChatChannel chatChannel, Role role) {
        this.uniqueID = player.getUniqueId();
        this.chatChannel = chatChannel;
        this.role = role;
    }

    public FactionMember(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String)map.get("uniqueID"));
        this.chatChannel = (ChatChannel)((Object)Enums.getIfPresent((Class)ChatChannel.class, (String)((String)map.get("chatChannel"))).or((Object)ChatChannel.PUBLIC));
        this.role = (Role)((Object)Enums.getIfPresent((Class)Role.class, (String)((String)map.get("role"))).or((Object)Role.MEMBER));
    }

    public Map<String, Object> serialize() {
        LinkedHashMap map = Maps.newLinkedHashMap();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("chatChannel", this.chatChannel.name());
        map.put("role", this.role.name());
        return map;
    }

    public String getName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)this.uniqueID);
        return offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline() ? offlinePlayer.getName() : null;
    }

    public UUID getUniqueId() {
        return this.uniqueID;
    }

    public ChatChannel getChatChannel() {
        return this.chatChannel;
    }

    public void setChatChannel(ChatChannel chatChannel) {
        Preconditions.checkNotNull((Object)((Object)chatChannel), (Object)"ChatChannel cannot be null");
        this.chatChannel = chatChannel;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Player toOnlinePlayer() {
        return Bukkit.getPlayer((UUID)this.uniqueID);
    }
}

