package com.customhcf.hcf.faction.argument.staff;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.Arrays;

public class FactionManageArgument
        extends CommandArgument implements Listener {
    public Inventory page1 = Bukkit.createInventory(null, 9, "Faction Manager");
    public Faction faction;
    public PlayerFaction pf;
    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
    private final HCF plugin;

    public FactionManageArgument(HCF plugin) {
        super("manage", "Manage a faction", new String[]{"man"});
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <faction>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Player command only.");
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        Player p = (Player)sender;
        this.faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction with the name " + args[1] + " not found.");
            return false;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can not manage system factions.");
            return false;
        }

        this.pf = (PlayerFaction)faction;
        sender.sendMessage(ChatColor.RED + "" + this.faction);
        p.openInventory(page1);
        ItemStack a = new ItemStack(351, 1, (short) 1);
        ItemMeta am = a.getItemMeta();
        am.setLore(Arrays.asList((ChatColor.WHITE + "Left Click "+ ChatColor.GREEN + "+1"), (ChatColor.WHITE + "Right Click" + ChatColor.RED + " -1")));
        am.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Deaths Until Raidable");
        a.setItemMeta(am);
        page1.setItem(2, a);

        ItemStack b = new ItemStack(351, 1, (short) 11);
        ItemMeta bm = b.getItemMeta();
        bm.setLore(Arrays.asList(ChatColor.WHITE + "Click to remove regeneration."));
        bm.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Regeneration");
        b.setItemMeta(bm);
        page1.setItem(4, b);

        ItemStack c = new ItemStack(351, 1, (short) 14);
        ItemMeta cm = c.getItemMeta();
        cm.setLore(Arrays.asList(ChatColor.WHITE + "Click to view member list."));
        cm.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Member List");
        c.setItemMeta(cm);
        page1.setItem(6, c);


        return true;
    }

    @EventHandler
    public void onClick (InventoryClickEvent e) {
        if(e.getInventory().getName().equals("Faction Manager")) {
            Player p = (Player)e.getWhoClicked();
            ItemStack clicked = e.getCurrentItem();
            if(clicked.getType().equals(Material.INK_SACK)) {
                Dye dye = (Dye) clicked.getData();
                if (dye.getColor().equals(DyeColor.RED)) {
                    p.sendMessage(pf.getDisplayName(p) + pf.getDeathsUntilRaidable() + 1);
                    Bukkit.dispatchCommand(p, "/f setdtr " + ChatColor.stripColor(pf.getDisplayName(p)) + " " + pf.getDeathsUntilRaidable() + 1);
                    e.setCancelled(true);
                }
            }
            e.setCancelled(true);
        }
    }
}