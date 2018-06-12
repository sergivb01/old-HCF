package net.veilmc.hcf.kothgame.conquest;

import net.veilmc.hcf.HCF;
import net.veilmc.util.command.ArgumentExecutor;

public class ConquestExecutor
		extends ArgumentExecutor{
	public ConquestExecutor(HCF plugin){
		super("conquest");
		this.addArgument(new ConquestSetpointsArgument(plugin));
	}
}

