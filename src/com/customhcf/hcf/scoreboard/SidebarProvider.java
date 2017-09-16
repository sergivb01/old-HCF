
package com.customhcf.hcf.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;

import com.customhcf.hcf.scoreboard.SidebarEntry;

public interface SidebarProvider {
    public String getTitle();

    public List<SidebarEntry> getLines(Player var1);
}

