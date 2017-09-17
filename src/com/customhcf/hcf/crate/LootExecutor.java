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
        this.addArgument(new LootBankArgument(plugin));
        this.addArgument(new LootBroadcastsArgument());
        this.addArgument(new LootDepositArgument(plugin));
        this.addArgument(new LootGiveArgument(plugin));
        this.addArgument(new LootListArgument(plugin));
        this.addArgument(new LootWithdrawArgument(plugin));
    }
}

