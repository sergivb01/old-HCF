package com.customhcf.hcf.crate;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.crate.argument.LootBankArgument;
import com.customhcf.hcf.crate.argument.LootBroadcastsArgument;
import com.customhcf.hcf.crate.argument.LootDepositArgument;
import com.customhcf.hcf.crate.argument.LootGiveArgument;
import com.customhcf.hcf.crate.argument.LootListArgument;
import com.customhcf.hcf.crate.argument.LootWithdrawArgument;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;

public class LootExecutor
extends ArgumentExecutor {
    public LootExecutor(HCF plugin) {
        super("loot");
        this.addArgument((CommandArgument)new LootBankArgument(plugin));
        this.addArgument((CommandArgument)new LootBroadcastsArgument());
        this.addArgument((CommandArgument)new LootDepositArgument(plugin));
        this.addArgument((CommandArgument)new LootGiveArgument(plugin));
        this.addArgument((CommandArgument)new LootListArgument(plugin));
        this.addArgument((CommandArgument)new LootWithdrawArgument(plugin));
    }
}

