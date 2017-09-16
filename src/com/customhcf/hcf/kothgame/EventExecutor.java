
package com.customhcf.hcf.kothgame;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.kothgame.argument.EventCancelArgument;
import com.customhcf.hcf.kothgame.argument.EventCreateArgument;
import com.customhcf.hcf.kothgame.argument.EventDeleteArgument;
import com.customhcf.hcf.kothgame.argument.EventRenameArgument;
import com.customhcf.hcf.kothgame.argument.EventSetAreaArgument;
import com.customhcf.hcf.kothgame.argument.EventSetCapzoneArgument;
import com.customhcf.hcf.kothgame.argument.EventStartArgument;
import com.customhcf.hcf.kothgame.argument.EventUptimeArgument;

import com.customhcf.hcf.palace.EventSetCapzone;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;

public class EventExecutor
extends ArgumentExecutor {
    public EventExecutor(HCF plugin) {
        super("event");
        this.addArgument((CommandArgument)new EventCancelArgument(plugin));
        this.addArgument((CommandArgument)new EventCreateArgument(plugin));
        this.addArgument((CommandArgument)new EventDeleteArgument(plugin));
        this.addArgument((CommandArgument)new EventRenameArgument(plugin));
        this.addArgument((CommandArgument)new EventSetAreaArgument(plugin));
        this.addArgument((CommandArgument)new EventSetCapzoneArgument(plugin));
        this.addArgument((CommandArgument)new EventStartArgument(plugin));
        this.addArgument((CommandArgument)new EventUptimeArgument(plugin));
        this.addArgument(new EventSetCapzone(plugin));
    }
}

