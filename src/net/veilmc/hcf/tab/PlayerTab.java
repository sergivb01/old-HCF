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
		tabTemplate.left(6, "Online: &b1/3");
		tabTemplate.left(7, "Balance: &b$500");

		tabTemplate.left(9, "&3&lPlayer Info&7:");
		tabTemplate.left(10, "Kills: &b9");
		tabTemplate.left(11, "Deaths: &b7");

		tabTemplate.left(13, "&3&lLocation&7:");
		tabTemplate.left(14, "(x, y) [NE]");



		tabTemplate.middle(0, "&3&lHCFactions");
		tabTemplate.middle(2, "Online: &b" + Bukkit.getOnlinePlayers().size());

		tabTemplate.middle(4, "&3&lFactionName&7:");
		tabTemplate.middle(5, "&b**&fLeaderName");
		tabTemplate.middle(6, "&b*&fCaptainName");
		tabTemplate.middle(7, "MemberName");



		tabTemplate.right(4, "&3&lMap Information&7:");
		tabTemplate.right(5, "Prot I - Sharp I");
		tabTemplate.right(6, "Border: &b3000");
		tabTemplate.right(7, "SOTW Date: &b18/06/18");

		tabTemplate.right(9, "something");
		tabTemplate.right(10, "something");
		tabTemplate.right(11, "something");

		tabTemplate.right(13, "&3&lCurrent Event&7:");
		tabTemplate.right(14, "&bSky Koth &f(500, -500)");



		tabTemplate.farRight(9, "&cFor an optimal experience");
		tabTemplate.farRight(10, "&cWe recommend &4&lCheatBreaker");

		return tabTemplate;
	}


}
