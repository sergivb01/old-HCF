
package com.customhcf.hcf.faction;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.argument.*;
import com.customhcf.hcf.faction.argument.staff.*;
import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionExecutor
extends ArgumentExecutor {
    private final CommandArgument helpArgument;

    public FactionExecutor(HCF plugin) {
        super("faction");
        this.addArgument((CommandArgument)new FactionAcceptArgument(plugin));
        this.addArgument((CommandArgument)new FactionAllyArgument(plugin));
        this.addArgument((CommandArgument)new FactionChatArgument(plugin));
        this.addArgument((CommandArgument)new FactionChatSpyArgument(plugin));
        this.addArgument((CommandArgument)new FactionClaimArgument(plugin));
        this.addArgument((CommandArgument)new FactionClaimChunkArgument(plugin));
        this.addArgument((CommandArgument)new FactionClaimForArgument(plugin));
        this.addArgument((CommandArgument)new FactionClaimsArgument(plugin));
        this.addArgument((CommandArgument)new FactionClearClaimsArgument(plugin));
        this.addArgument((CommandArgument)new FactionCreateArgument(plugin));
        this.addArgument((CommandArgument)new FactionAnnouncementArgument(plugin));
        this.addArgument((CommandArgument)new FactionDemoteArgument(plugin));
        this.addArgument((CommandArgument)new FactionDepositArgument(plugin));
        this.addArgument((CommandArgument)new FactionDisbandArgument(plugin));
        this.addArgument((CommandArgument)new FactionLockArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetDtrRegenArgument(plugin));
        this.addArgument((CommandArgument)new FactionForceJoinArgument(plugin));
        this.addArgument((CommandArgument)new FactionForceKickArgument(plugin));
        this.addArgument((CommandArgument)new FactionForceLeaderArgument(plugin));
        this.addArgument((CommandArgument)new FactionForcePromoteArgument(plugin));
        this.addArgument(new FactionBanArgument(plugin));
        this.helpArgument = new FactionHelpArgument(this);
        this.addArgument(this.helpArgument);
        this.addArgument(new FactionMuteArgument(plugin));
        this.addArgument((CommandArgument)new FactionHomeArgument(this, plugin));
        this.addArgument((CommandArgument)new FactionInviteArgument(plugin));
   //     this.addArgument((CommandArgument)new FactionCoLeaderArgument(plugin));
        this.addArgument((CommandArgument)new FactionInvitesArgument(plugin));
        this.addArgument((CommandArgument)new FactionKickArgument(plugin));
        this.addArgument((CommandArgument)new FactionLeaderArgument(plugin));
        this.addArgument((CommandArgument)new FactionLeaveArgument(plugin));
        this.addArgument((CommandArgument)new FactionListArgument(plugin));
        this.addArgument(new FactionManageArgument(plugin));
        this.addArgument((CommandArgument)new FactionMapArgument(plugin));
        this.addArgument((CommandArgument)new FactionMessageArgument(plugin));
        this.addArgument((CommandArgument)new FactionOpenArgument(plugin));
        this.addArgument((CommandArgument)new FactionRemoveArgument(plugin));
        this.addArgument((CommandArgument)new FactionRenameArgument(plugin));
        this.addArgument((CommandArgument)new FactionPromoteArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetDtrArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetDeathbanMultiplierArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetHomeArgument(plugin));
        this.addArgument((CommandArgument)new FactionShowArgument(plugin));
        this.addArgument((CommandArgument)new FactionStuckArgument(plugin));
        this.addArgument((CommandArgument)new FactionUnclaimArgument(plugin));
        this.addArgument((CommandArgument)new FactionUnallyArgument(plugin));
        this.addArgument((CommandArgument)new FactionUninviteArgument(plugin));
        this.addArgument((CommandArgument)new FactionVersionArgument(plugin));
        this.addArgument((CommandArgument)new FactionWithdrawArgument(plugin));

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission;
        if (args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }
        CommandArgument argument = this.getArgument(args[0]);
        if (argument != null && ((permission = argument.getPermission()) == null || sender.hasPermission(permission))) {
            argument.onCommand(sender, command, label, args);
            return true;
        }
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}

