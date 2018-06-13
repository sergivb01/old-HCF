package net.veilmc.hcf.commands;

import net.veilmc.hcf.payloads.Cache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DebugCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
		Cache.commandDelay.forEach((key, value) -> sender.sendMessage("UUID=" + key.toString() + " // Last=" + value));
		sender.sendMessage("=================================================================");
		Cache.payloads.forEach(payload -> sender.sendMessage(payload.toDocument().toJson()));
		return true;
	}

}
