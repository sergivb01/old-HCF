package com.sergivb01.hcf.tab.tabs;

import com.sergivb01.hcf.tab.com.bizarrealex.azazel.tab.TabTemplate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffTab{

	public TabTemplate getTemplate(Player player){
		TabTemplate tabTemplate = new TabTemplate();

		tabTemplate.left(4, "&3&lReports&7:");
		for(int i = 5; i < 16; i++){
			tabTemplate.left(i, "Hacker123 &b(killaura)");
		}


		tabTemplate.middle(0, "&3&lHCFactions");
		tabTemplate.middle(2, "Online: &b" + Bukkit.getOnlinePlayers().size());

		tabTemplate.middle(4, "&3&lServers&7:");
		tabTemplate.middle(5, "HCFactions: &b156 &7(6)");
		tabTemplate.middle(6, "Kits: &b156 &7(8)");
		tabTemplate.middle(7, "SevensHCF: &b360 &7(4)");
		tabTemplate.middle(8, "SoloHCF: &b78 &7(2)");


		tabTemplate.right(4, "&3&lRequests&7:");
		for(int i = 5; i < 16; i++){
			tabTemplate.right(i, "SteveUser &b(how to claim?)");
		}


		return tabTemplate;
	}


}
