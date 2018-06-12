package net.veilmc.hcf.listeners.fixes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PexCrashFix implements Listener{

	private String[] commands = {"calc", "eval", "solve", "worldedit:/calc", "worldedit:/eval", "worldedit:/solve", "/worldedit:/", "//calculate"};

	@EventHandler
	public void onCommandSend(PlayerCommandPreprocessEvent e){
		if((!e.getPlayer().isOp())){
			String cmd = e.getMessage().toLowerCase().replaceFirst("/", "");
			if((cmd.startsWith("pex")) || (cmd.startsWith("permission")) || (((cmd.contains("faction")) || (cmd.contains("f"))) && ((cmd.contains("top")) || (cmd.contains("t"))) && ((cmd.contains("balance")) || (cmd.contains("money"))))){
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You lack the correct permissions to run this commands!");
			}
		}
		String cmd = e.getMessage().toLowerCase().replaceFirst("/", "");
		if((cmd.startsWith("pex ")) && (!e.isCancelled())){
			String[] args = cmd.substring("pex ".length()).split(" ");
			if((args.length == 1) && (args[0].equalsIgnoreCase("user") || (args.length == 1) && (args[0].equalsIgnoreCase("users")))){
				e.getPlayer().sendMessage(ChatColor.RED + "Incorrect syntax.");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCommandWith(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();

		boolean blocked = false;
		String[] arrayOfString;
		int j = (arrayOfString = this.commands).length;
		for(int i = 0; i < j; i++){
			String command = arrayOfString[i];
			if(e.getMessage().toLowerCase().startsWith("//" + command.toLowerCase())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "That comamnd is blocked.");
			}
		}
	}

}