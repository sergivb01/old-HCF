package net.veilmc.hcf.tab;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.Azazel;
import org.bukkit.event.Listener;

public class TabListener implements Listener{
	private static HCF plugin;

	public TabListener(HCF plugin){
		new Azazel(plugin, new PlayerTab());
	}


}
