package net.veilmc.hcf.commands;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.tab.PlayerTab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
		int style = Integer.parseInt(args[0]);
		HCF.getInstance().getUserManager().getUser(((Player)sender).getUniqueId()).setTabStyle(style);
		sender.sendMessage("Style set to " + style);
		PlayerTab.clean.add((Player)sender);
		return true;
	}


}
