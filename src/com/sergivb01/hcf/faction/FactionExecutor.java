package com.sergivb01.hcf.faction;

import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.faction.argument.*;
import com.sergivb01.hcf.faction.argument.staff.*;
import com.sergivb01.util.command.ArgumentExecutor;
import com.sergivb01.util.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionExecutor
		extends ArgumentExecutor{
	private final CommandArgument helpArgument;

	public FactionExecutor(HCF plugin){
		super("faction");
		this.addArgument(new FactionAcceptArgument(plugin));
		this.addArgument(new FactionAllyArgument(plugin));
		this.addArgument(new FactionChatArgument(plugin));
		this.addArgument(new FactionChatSpyArgument(plugin));
		this.addArgument(new FactionClaimArgument(plugin));
		this.addArgument(new FactionClaimChunkArgument(plugin));
		this.addArgument(new FactionClaimForArgument(plugin));
		this.addArgument(new FactionClaimsArgument(plugin));
		this.addArgument(new FactionClearClaimsArgument(plugin));
		this.addArgument(new FactionCreateArgument(plugin));
		this.addArgument(new FactionAnnouncementArgument(plugin));
		this.addArgument(new FactionDemoteArgument(plugin));
		this.addArgument(new FactionDepositArgument(plugin));
		this.addArgument(new FactionDisbandArgument(plugin));
		this.addArgument(new FactionLockArgument(plugin));
		this.addArgument(new FactionSetDtrRegenArgument(plugin));
		this.addArgument(new FactionForceJoinArgument(plugin));
		this.addArgument(new FactionForceKickArgument(plugin));
		this.addArgument(new FactionForceLeaderArgument(plugin));
		this.addArgument(new FactionForcePromoteArgument(plugin));
		this.addArgument(new FactionBanArgument(plugin));
		this.helpArgument = new FactionHelpArgument(this);
		this.addArgument(this.helpArgument);
		this.addArgument(new FactionMuteArgument(plugin));
		this.addArgument(new FactionHomeArgument(this, plugin));
		this.addArgument(new FactionInviteArgument(plugin));
		//     this.addArgument((CommandArgument)new FactionCoLeaderArgument(plugin));
		this.addArgument(new FactionInvitesArgument(plugin));
		this.addArgument(new FactionKickArgument(plugin));
		this.addArgument(new FactionLeaderArgument(plugin));
		this.addArgument(new FactionLeaveArgument(plugin));
		this.addArgument(new FactionListArgument(plugin));
		this.addArgument(new FactionMapArgument(plugin));
		this.addArgument(new FactionMessageArgument(plugin));
		this.addArgument(new FactionOpenArgument(plugin));
		this.addArgument(new FactionRemoveArgument(plugin));
		this.addArgument(new FactionRenameArgument(plugin));
		this.addArgument(new FactionPromoteArgument(plugin));
		this.addArgument(new FactionSetDtrArgument(plugin));
		this.addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
		this.addArgument(new FactionSetHomeArgument(plugin));
		this.addArgument(new FactionShowArgument(plugin));
		this.addArgument(new FactionStuckArgument(plugin));
		this.addArgument(new FactionUnclaimArgument(plugin));
		this.addArgument(new FactionUnallyArgument(plugin));
		this.addArgument(new FactionUninviteArgument(plugin));
		this.addArgument(new FactionVersionArgument(plugin));
		this.addArgument(new FactionWithdrawArgument(plugin));

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		String permission;
		if(args.length < 1){
			this.helpArgument.onCommand(sender, command, label, args);
			return true;
		}
		CommandArgument argument = this.getArgument(args[0]);
		if(argument != null && ((permission = argument.getPermission()) == null || sender.hasPermission(permission))){
			argument.onCommand(sender, command, label, args);
			return true;
		}
		this.helpArgument.onCommand(sender, command, label, args);
		return true;
	}
}

