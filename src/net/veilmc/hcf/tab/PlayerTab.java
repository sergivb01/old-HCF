package net.veilmc.hcf.tab;

import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabAdapter;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabTemplate;
import org.bukkit.entity.Player;

public class PlayerTab implements TabAdapter{

	public TabTemplate getTemplate(Player player){
		TabTemplate tabTemplate = new TabTemplate();

		for(int i = 0; i < 20; i++){
			tabTemplate.left(i, "Sup Left #" + i);
			tabTemplate.middle(i, "Sup Mid #" + i);
			tabTemplate.right(i, "Sup Mid #" + i);
		}


		return tabTemplate;
	}

}
