
package net.veilmc.hcf.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;

public interface SidebarProvider {
    public String getTitle();

    public List<SidebarEntry> getLines(Player var1);
}

