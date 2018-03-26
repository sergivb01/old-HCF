package net.veilmc.hcf.death;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.death.argument.DeathInfoArgument;
import net.veilmc.hcf.death.argument.DeathRefundArgument;
import net.veilmc.hcf.death.argument.DeathReviveArgument;
import net.veilmc.util.command.ArgumentExecutor;

public class DeathExecutor extends ArgumentExecutor{
	public DeathExecutor(HCF plugin){
		super("death");
		this.addArgument(new DeathInfoArgument(plugin));
		this.addArgument(new DeathRefundArgument(plugin));
		this.addArgument(new DeathReviveArgument(plugin));
	}
}
