
package com.customhcf.hcf.kothgame.conquest;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.kothgame.conquest.ConquestSetpointsArgument;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;

public class ConquestExecutor
extends ArgumentExecutor {
    public ConquestExecutor(HCF plugin) {
        super("conquest");
        this.addArgument(new ConquestSetpointsArgument(plugin));
    }
}

