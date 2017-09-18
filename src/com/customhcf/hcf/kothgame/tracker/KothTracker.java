
package com.customhcf.hcf.kothgame.tracker;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.Utils.DateTimeFormats;
import com.customhcf.hcf.kothgame.CaptureZone;
import com.customhcf.hcf.kothgame.EventTimer;
import com.customhcf.hcf.kothgame.EventType;
import com.customhcf.hcf.kothgame.faction.EventFaction;
import com.customhcf.hcf.kothgame.faction.KothFaction;

@Deprecated
public class KothTracker
implements EventTracker {
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private final HCF plugin;

    public KothTracker(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        CaptureZone captureZone = ((KothFaction)eventFaction).getCaptureZone();
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis <= 0) {
            this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        int remainingSeconds = (int)(remainingMillis / 1000);
        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getEventType().getDisplayName() + "§8] " + ChatColor.GOLD + "Someone §eis controlling " + ChatColor.GOLD + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getEventType().getDisplayName() + "§8] " + ChatColor.GOLD + eventFaction.getName() + ChatColor.YELLOW + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        player.sendMessage(ChatColor.YELLOW + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        return true;
    }



    public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction)
    {
      player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
      long remainingMillis = captureZone.getRemainingCaptureMillis();
      if ((remainingMillis > 0L) && (captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE)) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
      }
      return true;
    }

    @Override
    public void stopTiming() {
    }

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15);
    }
}

