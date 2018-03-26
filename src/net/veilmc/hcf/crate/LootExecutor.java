package net.veilmc.hcf.crate;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.crate.argument.LootBankArgument;
import net.veilmc.hcf.crate.argument.LootBroadcastsArgument;
import net.veilmc.hcf.crate.argument.LootDepositArgument;
import net.veilmc.hcf.crate.argument.LootGiveArgument;
import net.veilmc.hcf.crate.argument.LootListArgument;
import net.veilmc.hcf.crate.argument.LootWithdrawArgument;
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

public class LootExecutor
		extends ArgumentExecutor{
	public LootExecutor(HCF plugin){
		super("loot");
		this.addArgument(new LootBankArgument(plugin));
		this.addArgument(new LootBroadcastsArgument());
		this.addArgument(new LootDepositArgument(plugin));
		this.addArgument(new LootGiveArgument(plugin));
		this.addArgument(new LootListArgument(plugin));
		this.addArgument(new LootWithdrawArgument(plugin));
	}
}

