package com.customhcf.hcf.faction.argument.staff;

import com.customhcf.base.BasePlugin;
import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.faction.type.PlayerFaction;
import com.customhcf.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class FactionManageArgument
        extends CommandArgument implements Listener {
    public Inventory page1;
    public Inventory page2 = Bukkit.createInventory(null, 27, "Faction Manager | Members");
    public Faction faction;
    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
    private final HCF plugin;

    public FactionManageArgument(HCF plugin) {
        super("manage", "Manage a faction", new String[]{"man"});
        this.plugin = plugin;
    }

    public String getUsage(String label) {
        return "" + '/' + label + ' ' + this.getName() + " <player|faction>";
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage((Object) ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }

//        this.namedFaction = this.plugin.getFactionManager().getFaction(args[1]);
//        if (Bukkit.getPlayer((String)args[1]) != null) {
//            this.namedFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getPlayer((String)args[1]));
//        } else if (Bukkit.getOfflinePlayer((String)args[1]).hasPlayedBefore()) {
//            this.namedFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getOfflinePlayer((String)args[1]).getUniqueId());
//        }
        this.faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (this.faction == null) {
            sender.sendMessage((Object) ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (!(this.faction instanceof PlayerFaction)) {
            sender.sendMessage((Object) ChatColor.RED + "You can only manage player factions.");
            return true;
        }
        Page1((Player)sender);
        return true;
    }

    public void Page1(final Player sender) {
        Player player = (Player) sender;
        this.page1 = Bukkit.createInventory(null, 27, "Faction Manager");
        this.page2 = Bukkit.createInventory(null, 36, "Faction Manager | DTR");
        player.openInventory(page1);
        ItemStack dtr = new ItemStack(Material.IRON_BLOCK, 1, (short) 3);
        ItemMeta metadtr = dtr.getItemMeta();
        metadtr.setLore((Arrays.asList((" "), (ChatColor.YELLOW + "Left-click:" + ChatColor.GRAY + " +1 DTR"), (ChatColor.YELLOW + "Right-Click:" + ChatColor.GRAY + " -1 DTR"))));
        metadtr.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "DTR");
        dtr.setItemMeta(metadtr);
        page1.setItem(11, dtr);

        ItemStack regen = new ItemStack(Material.GOLD_BLOCK, 1, (short) 3);
        ItemMeta metaregen = dtr.getItemMeta();
        metaregen.setLore((Arrays.asList((ChatColor.YELLOW + "Click to remove DTR regen"))));
        metaregen.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Regen");
        regen.setItemMeta(metaregen);
        page1.setItem(13, regen);

        ItemStack members = new ItemStack(Material.DIAMOND_BLOCK, 1, (short) 3);
        ItemMeta metamembers = dtr.getItemMeta();
        metamembers.setLore((Arrays.asList((ChatColor.YELLOW + "Click to view faction members"))));
        metamembers.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Members");
        members.setItemMeta(metamembers);
        page1.setItem(15, members);

        ItemStack info = new ItemStack(Material.REDSTONE_BLOCK, 1, (short) 3);
        ItemMeta metainfo = dtr.getItemMeta();
        metainfo.setLore((Arrays.asList((ChatColor.GRAY + "Managing faction"))));
        metainfo.setDisplayName(ChatColor.YELLOW + "" + faction.getDisplayName(sender));
        info.setItemMeta(metainfo);
        page1.setItem(0, info);
        }

    public void Page2(final Player sender) {
        PlayerFaction pf = (PlayerFaction)faction;
        for (Player p : pf.getOnlinePlayers()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(p.getName());
            skull.setItemMeta(meta);
            page2.addItem(skull);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        Inventory inventory = event.getInventory();

        if (inventory.getName().equals("Faction Manager")) {


            if (clicked.getType() == Material.IRON_BLOCK) {
                if (event.getClick() == ClickType.LEFT) {
                    event.setCancelled(true);
                }
                if (event.getClick() == ClickType.RIGHT) {
                    event.setCancelled(true);
                }
            }
            if(clicked.getType() == Material.DIAMOND_BLOCK) {
                player.closeInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Page2(player);
//                        for (Player p : faction.getOnlinePlayers()) {
//                            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
//                            ItemMeta meta = skull.getItemMeta();
//                            meta.setLore((Arrays.asList(("abc"))));
//                            meta.setDisplayName(p.getName());
//                            skull.setItemMeta(meta);
//                            page2.addItem(skull);
//                        }
                    }
                }.runTaskLater(BasePlugin.getPlugin(), 1);
            }
            event.setCancelled(true);
        }
    }

}