
package com.customhcf.hcf.crate.type;

import com.customhcf.hcf.Utils.ConfigurationService;
import com.customhcf.hcf.crate.EnderChestKey;
import com.customhcf.hcf.crowbar.Crowbar;
import com.customhcf.util.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KothKey
extends EnderChestKey {
    public KothKey() {
        super("Koth", 6);
        this.setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, ConfigurationService.ENCHANTMENT_LIMITS.get((Object)Enchantment.ARROW_DAMAGE).intValue()).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.DURABILITY, 3).lore(new String[]{(Object)ChatColor.RED + "unrepairable"}).displayName((Object)ChatColor.RED + "KOTH Bow").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get((Object)Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).displayName((Object)ChatColor.RED + "KOTH Helmet").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get((Object)Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).displayName((Object)ChatColor.RED + "KOTH Chestplate").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get((Object)Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).displayName((Object)ChatColor.RED + "KOTH Leggings").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get((Object)Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).displayName((Object)ChatColor.RED + "KOTH Boots").build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 2).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get((Object)Enchantment.DAMAGE_ALL).intValue()).lore(new String[]{(Object)ChatColor.RED + "unrepairable"}).displayName((Object)ChatColor.GREEN + "KOTH Fire").build(), 10);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.KNOCKBACK, 1).displayName((Object)ChatColor.GREEN + "KOTH Knockback").lore(new String[]{(Object)ChatColor.RED + "unrepairable"}).build(), 10);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE).data((short) 1).build(), 3);
        this.setupRarity(new ItemStack(Material.GLOWSTONE, 48), 2);
        this.setupRarity(new ItemStack(Material.GLOWSTONE, 24), 2);
        this.setupRarity(new ItemStack(Material.SULPHUR, 48), 4);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 24), 4);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 13), 6);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 9), 6);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 24), 4);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 13), 6);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 9), 6);
        this.setupRarity(new ItemStack(Material.EMERALD_BLOCK, 24), 4);
        this.setupRarity(new ItemStack(Material.EMERALD_BLOCK, 13), 6);
        this.setupRarity(new ItemStack(Material.EMERALD_BLOCK, 9), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.LOOT_BONUS_MOBS, 5).displayName((Object)ChatColor.RED + "KOTH Looting").lore(new String[]{(Object)ChatColor.RED + "unrepairable"}).build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).lore(new String[]{(Object)ChatColor.RED + "unrepairable"}).displayName((Object)ChatColor.RED + "KOTH Fortune").build(), 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.DIG_SPEED, 5).lore(new String[]{(Object)ChatColor.RED + "unrepairable"}).displayName((Object)ChatColor.RED + "KOTH Pickaxe").build(), 4);
        this.setupRarity(new ItemStack(Material.NETHER_STAR), 12);
        this.setupRarity(new Crowbar().getItemIfPresent(), 4);
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.GREEN;
    }

    @Override
    public boolean getBroadcastItems() {
        return true;
    }
}

