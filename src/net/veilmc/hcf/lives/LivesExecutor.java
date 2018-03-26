package net.veilmc.hcf.lives;

import net.veilmc.hcf.lives.argument.LivesClearDeathbansArgument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.lives.argument.LivesCheckArgument;
import net.veilmc.hcf.lives.argument.LivesGiveArgument;
import net.veilmc.hcf.lives.argument.LivesReviveArgument;
import net.veilmc.hcf.lives.argument.LivesSetArgument;
import net.veilmc.hcf.lives.argument.LivesSetDeathbanTimeArgument;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

public class LivesExecutor extends ArgumentExecutor{
	public LivesExecutor(HCF plugin){
		super("lives");
		this.addArgument(new LivesCheckArgument(plugin));
		this.addArgument(new LivesClearDeathbansArgument(plugin));
		this.addArgument(new LivesGiveArgument(plugin));
		this.addArgument(new LivesReviveArgument(plugin));
		this.addArgument(new LivesSetArgument(plugin));
		this.addArgument(new LivesSetDeathbanTimeArgument());
	}
}

