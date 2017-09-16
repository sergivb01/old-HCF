
package com.customhcf.hcf.timer;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.timer.argument.TimerCheckArgument;
import com.customhcf.hcf.timer.argument.TimerSetArgument;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;

public class TimerExecutor
extends ArgumentExecutor {
    public TimerExecutor(HCF plugin) {
        super("timer");
        this.addArgument((CommandArgument)new TimerCheckArgument(plugin));
        this.addArgument((CommandArgument)new TimerSetArgument(plugin));
    }
}

