package net.veilmc.hcf.tab.tabs;

import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabTemplate;
import org.bukkit.entity.Player;

public class StaffTab{

	public TabTemplate getTemplate(Player player){
		TabTemplate tabTemplate = new TabTemplate();

		tabTemplate.middle(String.valueOf(System.currentTimeMillis()));

		return tabTemplate;
	}


}
