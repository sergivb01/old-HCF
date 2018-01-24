package net.veilmc.hcf.spawn;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.spawn.argument.TokenCheckArgument;
import net.veilmc.hcf.spawn.argument.TokenClearArgument;
import net.veilmc.hcf.spawn.argument.TokenGiveArgument;
import net.veilmc.util.command.ArgumentExecutor;

public class TokenExecutor extends ArgumentExecutor {
    public TokenExecutor(HCF plugin) {
        super("token");
        this.addArgument(new TokenCheckArgument(plugin));
        this.addArgument(new TokenClearArgument(plugin));
        this.addArgument(new TokenGiveArgument(plugin));
    }
}
