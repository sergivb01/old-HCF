package net.veilmc.hcf.commands.lives;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.commands.lives.argument.*;
import net.veilmc.util.command.ArgumentExecutor;

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

