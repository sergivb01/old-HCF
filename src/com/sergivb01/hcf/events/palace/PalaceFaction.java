package com.sergivb01.hcf.events.palace;

import com.google.common.collect.ImmutableList;
import com.sergivb01.hcf.HCF;
import com.sergivb01.hcf.events.CaptureZone;
import com.sergivb01.hcf.events.EventType;
import com.sergivb01.hcf.events.faction.CapturableFaction;
import com.sergivb01.hcf.faction.claim.Claim;
import com.sergivb01.hcf.faction.type.ClaimableFaction;
import com.sergivb01.hcf.faction.type.PlayerFaction;
import com.sergivb01.util.BukkitUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class PalaceFaction
		extends CapturableFaction
		implements ConfigurationSerializable{
	private CaptureZone captureZone;

	public PalaceFaction(String name){
		super(name);
	}

	public PalaceFaction(Map<String, Object> map){
		super(map);
		this.captureZone = ((CaptureZone) map.get("captureZone"));
	}

	public Map<String, Object> serialize(){
		Map<String, Object> map = super.serialize();
		map.put("captureZone", this.captureZone);
		return map;
	}

	public List<CaptureZone> getCaptureZones(){
		return this.captureZone == null ? ImmutableList.of() : ImmutableList.of(this.captureZone);
	}

	public EventType getEventType(){
		return EventType.PALACE;
	}

	public void printDetails(CommandSender sender){
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(getDisplayName(sender));
		for(Claim claim : this.claims){
			Location location = claim.getCenter();
			sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '(' +
					ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
		}
		if(this.captureZone != null){
			long remainingCaptureMillis = this.captureZone.getRemainingCaptureMillis();
			long defaultCaptureMillis = this.captureZone.getDefaultCaptureMillis();
			if((remainingCaptureMillis > 0L) && (remainingCaptureMillis != defaultCaptureMillis)){
				sender.sendMessage(ChatColor.YELLOW + "  Remaining Time: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
			}
			sender.sendMessage(ChatColor.YELLOW + "  Capture Delay: " + ChatColor.RED + this.captureZone.getDefaultCaptureWords());
			if((this.captureZone.getCappingPlayer() != null) && (sender.hasPermission("hcf.palace.checkcapper"))){
				Player capping = this.captureZone.getCappingPlayer();
				PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(capping);
				String factionTag = "[" + (playerFaction == null ? "*" : playerFaction.getName()) + "]";
				sender.sendMessage(ChatColor.YELLOW + "  Current Capper: " + ChatColor.RED + capping.getName() + ChatColor.GOLD + factionTag);
			}
		}
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

	public CaptureZone getCaptureZone(){
		return this.captureZone;
	}

	public void setCaptureZone(CaptureZone captureZone){
		this.captureZone = captureZone;
	}
}
