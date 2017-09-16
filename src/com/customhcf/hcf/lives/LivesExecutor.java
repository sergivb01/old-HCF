package com.customhcf.hcf.lives;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.lives.argument.LivesCheckArgument;
import com.customhcf.hcf.lives.argument.LivesClearDeathbansArgument;
import com.customhcf.hcf.lives.argument.LivesGiveArgument;
import com.customhcf.hcf.lives.argument.LivesReviveArgument;
import com.customhcf.hcf.lives.argument.LivesSetArgument;
import com.customhcf.hcf.lives.argument.LivesSetDeathbanTimeArgument;
import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;

public class LivesExecutor extends ArgumentExecutor {
    public LivesExecutor(HCF plugin) {
        super("lives");
        this.addArgument((CommandArgument)new LivesCheckArgument(plugin));
        this.addArgument((CommandArgument)new LivesClearDeathbansArgument(plugin));
        this.addArgument((CommandArgument)new LivesGiveArgument(plugin));
        this.addArgument((CommandArgument)new LivesReviveArgument(plugin));
        this.addArgument((CommandArgument)new LivesSetArgument(plugin));
        this.addArgument((CommandArgument)new LivesSetDeathbanTimeArgument());
    }
}

