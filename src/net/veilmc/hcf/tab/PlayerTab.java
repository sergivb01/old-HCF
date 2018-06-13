package net.veilmc.hcf.tab;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.Azazel;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabAdapter;
import net.veilmc.hcf.tab.com.bizarrealex.azazel.tab.TabTemplate;
import net.veilmc.hcf.tab.tabs.StaffTab;
import net.veilmc.hcf.user.TabStyles;
import net.veilmc.hcf.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class PlayerTab implements TabAdapter, Listener{
	private HCF plugin;
	private Azazel azazel;
	private StaffTab staffTab;
	private UserManager userManager;
	public static List<Player> clean = new ArrayList<>();

	public PlayerTab(HCF plugin){
		this.plugin = plugin;
		this.azazel = new Azazel(plugin, this);
		this.staffTab = new StaffTab();
		this.userManager = HCF.getInstance().getUserManager();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public TabTemplate getTemplate(Player player){
		if(clean.remove(player)){
			return getClearTemplate();
		}

		if(userManager.getUser(player.getUniqueId()).getTabStyle().equals(TabStyles.STAFF)){
			return staffTab.getTemplate(player);
		}

		TabTemplate tabTemplate = new TabTemplate();


		tabTemplate.left(4, "&3&lFaction Info&7:");
		tabTemplate.left(5, "DTR: &b5.5/5.6");
		tabTemplate.left(6, "Online: &b1/3");
		tabTemplate.left(7, "Balance: &b$500");

		tabTemplate.left(9, "&3&lPlayer Info&7:");
		tabTemplate.left(10, "Kills: &b9");
		tabTemplate.left(11, "Deaths: &b7");

		tabTemplate.left(13, "&3&lLocation&7:");
		tabTemplate.left(14, "&2Spawn");
		tabTemplate.left(15, "(x, y) [NE]");



		tabTemplate.middle(0, "&3&lHCFactions");
		tabTemplate.middle(2, "Online: &b" + Bukkit.getOnlinePlayers().size());

		tabTemplate.middle(4, "&3&lFactionName&7:");
		tabTemplate.middle(5, "&b**&fLeaderName");
		tabTemplate.middle(6, "&b*&fCaptainName");
		tabTemplate.middle(7, "MemberName");


		if(userManager.getUser(player.getUniqueId()).getTabStyle().equals(TabStyles.FACTION_LIST)){
			tabTemplate.right(0, "&3&lFaction List&7:");
			for(int i = 1; i < 20; i++){
				tabTemplate.right(i, "FactionName" + (19 - i) + " &b(" + (19 - i) + "/10)");
			}
			return tabTemplate;
		}
		tabTemplate.right(4, "&3&lMap Information&7:");
		tabTemplate.right(5, "Prot I - Sharp I");
		tabTemplate.right(6, "Border: &b3000");
		tabTemplate.right(7, "SOTW Date: &b18/06/18");

		tabTemplate.right(9, "&3&lLast EOTW&7:");
		tabTemplate.right(10, "Capper: &bBradva");
		tabTemplate.right(11, "FFA: &bveilkid");

		tabTemplate.right(13, "&3&lCurrent Event&7:");
		tabTemplate.right(14, "&bSky Koth &f(500, -500)");



		tabTemplate.farRight(9, "&cFor an optimal experience");
		tabTemplate.farRight(10, "&cWe recommend &4&lCheatBreaker");

		return tabTemplate;
	}

	private TabTemplate getClearTemplate(){
		TabTemplate tabTemplate = new TabTemplate();
		for(int i = 0; i < 20; i++){
			tabTemplate.left(0, "");
			tabTemplate.middle(0, "");
			tabTemplate.right(0, "");
			tabTemplate.farRight(0, "");
		}
		return tabTemplate;
	}


}
