package net.veilmc.hcf.tab;

import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabAdapter;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabTemplate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerTab implements TabAdapter{

	public TabTemplate getTemplate(Player player){
		TabTemplate tabTemplate = new TabTemplate();

		tabTemplate.left(4, "&3&lFaction Info&7:");
		tabTemplate.left(5, "DTR: &b5.5/5.6");
		tabTemplate.left(5, "Online: &b1/3");
		tabTemplate.left(6, "Balance: &b$500");

		tabTemplate.left(8, "&3&lPlayer Info&7:");
		tabTemplate.left(9, "Kills: &b9");
		tabTemplate.left(10, "Deaths: &b7");

		tabTemplate.left(12, "&3&lLocation&7:");
		tabTemplate.left(13, "(x, y)");



		tabTemplate.middle(0, "&3&lHCFactions");
		tabTemplate.middle(2, "Online: &b" + Bukkit.getOnlinePlayers().size());

		tabTemplate.middle(4, "&3&lFactionName&7:");
		tabTemplate.middle(5, "&b***&fLeaderName");
		tabTemplate.middle(6, "&b*&fCoLeaderName");
		tabTemplate.middle(7, "&b*&fCaptainName");
		tabTemplate.middle(7, "MemberName");

		tabTemplate.right(0, "&3&lNo idea to what to add");
		tabTemplate.right(1, "&3&lin this col :/");




		tabTemplate.farRight(9, "&cFor an optimal experience");
		tabTemplate.farRight(10, "&cWe recommend &4&lCheatBreaker");

		return tabTemplate;
	}


}
