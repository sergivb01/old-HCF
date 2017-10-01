package com.customhcf.hcf.listener;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.hcf.timer.PlayerTimer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {
    private final HCF plugin;
    private String prefix;
    private String signTitle;

    public ElevatorListener(HCF plugin) {
        this.plugin = plugin;
        this.prefix = ChatColor.BLUE + ChatColor.BOLD.toString() + "[Elevators] " + ChatColor.AQUA;
        this.signTitle = ChatColor.BLUE + ChatColor.BOLD.toString() + "[Elevator]";
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignUpdate(final SignChangeEvent e) {
        if (StringUtils.containsIgnoreCase(e.getLine(0), "Elevator")) {
            boolean up;
            if (StringUtils.containsIgnoreCase(e.getLine(1), "Up")) {
                up = true;
            } else {
                if (!StringUtils.containsIgnoreCase(e.getLine(1), "Down")) {
                    e.getPlayer().sendMessage(this.prefix + "Invalid sign! Needs to be Up or Down");
                    this.fail(e);
                    return;
                }
                up = false;
            }
            e.setLine(0, this.signTitle);
            e.setLine(1, up ? "Up" : "Down");
            e.setLine(2, "");
            e.setLine(3, "");
        }
    }

    public void fail(final SignChangeEvent e) {
        e.setLine(0, this.signTitle);
        e.setLine(1, ChatColor.RED + "Error");
        e.setLine(2, "");
        e.setLine(3, "");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            final Block block = e.getClickedBlock();
            if (block.getState() instanceof Sign) {
                final Sign sign = (Sign) block.getState();
                final String[] lines = sign.getLines();
                if (lines[0].equals(this.signTitle)) {
                    boolean up;
                    if (lines[1].equalsIgnoreCase("Up")) {
                        up = true;
                    } else {
                        if (!lines[1].equalsIgnoreCase("Down")) {
                            return;
                        }
                        up = false;
                    }
                    this.signClick(e.getPlayer(), sign.getLocation(), up);
                }
            }
        }
    }

    public boolean signClick(final Player player, final Location signLocation, final boolean up) {
        Block block = signLocation.getBlock();
        do {
            block = block.getRelative(up ? BlockFace.UP : BlockFace.DOWN);
            if (block.getY() > block.getWorld().getMaxHeight() || block.getY() <= 1) {
                player.sendMessage(this.prefix + "Could not locate the sign " + (up ? "above" : "below"));
                return false;
            }
        } while (!this.isSign(block));
        final boolean underSafe = this.isSafe(block.getRelative(BlockFace.DOWN));
        final boolean overSafe = this.isSafe(block.getRelative(BlockFace.UP));
        if(player.getGameMode().equals(GameMode.CREATIVE)) {
            final Location location = player.getLocation().clone();
            location.setX(block.getX() + 0.5);
            location.setY((double) (block.getY() + (underSafe ? -1 : 0)));
            location.setZ(block.getZ() + 0.5);
            location.setPitch(0.0f);
            player.teleport(location);
            return true;
        }

        Faction at = this.plugin.getFactionManager().getFactionAt(block);
        if(at instanceof PlayerFaction) {
            if (((PlayerFaction) at).getMember(player) == null) {
                player.sendMessage(ChatColor.RED + "You cannot use this in the territory of " + ChatColor.stripColor(at.getDisplayName(player)));
                return false;
            }
        }
        PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
        long remaining = timer.getRemaining(player);
        if ((remaining = (timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player)) > 0L) {
            player.sendMessage(ChatColor.RED + "You can not use this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
            return false;

        }
        if (!underSafe && !overSafe) {
            player.sendMessage(this.prefix + "Could not find a place to teleport by the sign " + (up ? "above" : "below"));
            return false;
        }


        final Location location = player.getLocation().clone();
        location.setX(block.getX() + 0.5);
        location.setY((double) (block.getY() + (underSafe ? -1 : 0)));
        location.setZ(block.getZ() + 0.5);
        location.setPitch(0.0f);
        player.teleport(location);
        return true;
    }

    private boolean isSign(final Block block) {
        if (block.getState() instanceof Sign) {
            final Sign sign = (Sign) block.getState();
            final String[] lines = sign.getLines();
            return lines[0].equals(this.signTitle)
                    && (lines[1].equalsIgnoreCase("Up") || lines[1].equalsIgnoreCase("Down"));
        }
        return false;
    }

    private boolean isSafe(final Block block) {
        return block != null && !block.getType().isSolid() && block.getType() != Material.GLASS && block.getType() != Material.STAINED_GLASS;
    }
}
