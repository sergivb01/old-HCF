package net.veilmc.hcf.kothgame.conquest;

import net.veilmc.hcf.HCF;
<<<<<<< HEAD
=======
import net.veilmc.hcf.HCF;
>>>>>>> origin/new
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

public class ConquestExecutor
		extends ArgumentExecutor{
	public ConquestExecutor(HCF plugin){
		super("conquest");
		this.addArgument(new ConquestSetpointsArgument(plugin));
	}
}

