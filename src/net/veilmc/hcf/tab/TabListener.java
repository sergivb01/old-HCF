package net.veilmc.hcf.tab;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.Azazel;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class TabListener implements Listener{
	private HCF plugin;

	public TabListener(HCF plugin){
		this.plugin = plugin;
		new Azazel(plugin, new PlayerTab());
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}



}
