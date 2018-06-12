package net.veilmc.hcf.commands.death;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.commands.death.argument.DeathInfoArgument;
import net.veilmc.hcf.commands.death.argument.DeathRefundArgument;
import net.veilmc.hcf.commands.death.argument.DeathReviveArgument;
import net.veilmc.util.command.ArgumentExecutor;

public class DeathExecutor extends ArgumentExecutor{
	public DeathExecutor(HCF plugin){
		super("death");
		this.addArgument(new DeathInfoArgument(plugin));
		this.addArgument(new DeathRefundArgument(plugin));
		this.addArgument(new DeathReviveArgument(plugin));
	}
}
