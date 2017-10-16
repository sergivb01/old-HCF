
package net.veilmc.hcf.listener;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.faction.struct.Role;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.PlayerFaction;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;

import java.util.*;
import java.util.stream.Collectors;

public class SignSubclaimListener implements Listener {
    private static final int MAX_SIGN_LINE_CHARS = 16;
    private static final String SUBCLAIM_PREFIX;
    private static final BlockFace[] SIGN_FACES;
    private final HCF plugin;

    public SignSubclaimListener(HCF plugin) {
        this.plugin = plugin;
    }

    private boolean isSubclaimable(Block block) {
        Material type = block.getType();
        return type == Material.FENCE_GATE || type == Material.TRAP_DOOR || block.getState() instanceof InventoryHolder;
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        if(StringUtils.containsIgnoreCase(lines[0], "[subclaim]")) {
            Block block = event.getBlock();
            MaterialData materialData = block.getState().getData();
            if(materialData instanceof Sign) {
                Sign sign = (Sign)materialData;
                Block attatchedBlock = block.getRelative(sign.getAttachedFace());
                if(this.isSubclaimable(attatchedBlock)) {
                    Player player = event.getPlayer();
                    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    Role role;
                    if(playerFaction == null || (role = playerFaction.getMember(player).getRole()) == Role.MEMBER) {
                        return;
                    }

                    Faction factionAt = this.plugin.getFactionManager().getFactionAt(block.getLocation());
                    if(playerFaction.equals(factionAt)) {
                        Collection attachedSigns = this.getAttachedSigns(attatchedBlock);
                        Iterator memberList = attachedSigns.iterator();

                        while(memberList.hasNext()) {
                            org.bukkit.block.Sign leaderChest = (org.bukkit.block.Sign)memberList.next();
                            if(leaderChest.getLine(0).equals(SUBCLAIM_PREFIX)) {
                                player.sendMessage(ChatColor.RED + "There is already a subclaim sign on this " + attatchedBlock.getType().getData().getName() + '.');
                                return;
                            }
                        }

                        ArrayList var16 = new ArrayList(3);

                        for(int var17 = 1; var17 < lines.length; ++var17) {
                            String captainChest = lines[var17];
                            if(StringUtils.isNotBlank(captainChest)) {
                                var16.add(captainChest);
                            }
                        }

                        if(var16.isEmpty()) {
                            event.setLine(1, player.getName());
                            player.sendMessage(ChatColor.YELLOW + "Since no name was specified, this subclaim is now for you.");
                        }

                        boolean var18 = lines[1].equals(Role.LEADER.getAstrix()) || StringUtils.containsIgnoreCase(lines[1], "leader");
                        boolean var19 = lines[1].equals(Role.CAPTAIN.getAstrix()) || StringUtils.containsIgnoreCase(lines[1], "captain");
                        if(var19) {
                            event.setLine(2, null);
                            event.setLine(3, null);
                            event.setLine(1, ChatColor.YELLOW + "Captains Only");
                        }

                        if(var18) {
                            if(role != Role.LEADER) {
                                player.sendMessage(ChatColor.RED + "Only faction leaders can create leader subclaimed objects.");
                                return;
                            }

                            event.setLine(2, null);
                            event.setLine(3, null);
                            event.setLine(1, ChatColor.DARK_RED + "Leaders Only");
                        }

                        event.setLine(0, SUBCLAIM_PREFIX);
                        List actualMembers = (List)var16.stream().filter((member) -> {
                            return playerFaction.getMember((UUID) member) != null;
                        }).collect(Collectors.toList());
                        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + player.getName() + ChatColor.YELLOW + " has created a subclaim on block type " + ChatColor.LIGHT_PURPLE + attatchedBlock.getType().getData().getName() + ChatColor.YELLOW + " at " + ChatColor.WHITE + '[' + attatchedBlock.getX() + ", " + attatchedBlock.getZ() + ']' + ChatColor.YELLOW + " for " + (var18?"leaders":(actualMembers.isEmpty()?"captains":"members " + ChatColor.GRAY + '[' + ChatColor.DARK_GREEN + StringUtils.join(actualMembers, ", ") + ChatColor.GRAY + ']')));
                    }
                }
            }

        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onBlockBreak(BlockBreakEvent event) {
        if(!this.plugin.getEotwHandler().isEndOfTheWorld()) {
            Player player = event.getPlayer();
            if(player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("hcf.faction.protection.bypass")) {
                Block block = event.getBlock();
                BlockState state = block.getState();
                if(state instanceof org.bukkit.block.Sign || this.isSubclaimable(block)) {
                    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    if(playerFaction == null) {
                        return;
                    }

                    boolean hasAccess = playerFaction.getMember(player).getRole() != Role.MEMBER;
                    if(hasAccess) {
                        return;
                    }

                    if(state instanceof org.bukkit.block.Sign) {
                        org.bukkit.block.Sign var14 = (org.bukkit.block.Sign)state;
                        if(var14.getLine(0).equals(SUBCLAIM_PREFIX)) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You cannot break subclaim signs");
                        }

                        return;
                    }

                    Faction factionAt = this.plugin.getFactionManager().getFactionAt(block);
                    String search = this.getShortenedName(player.getName());
                    if(playerFaction.equals(factionAt) && !playerFaction.isRaidable()) {
                        Collection attachedSigns = this.getAttachedSigns(block);
                        Iterator var10 = attachedSigns.iterator();

                        while(true) {
                            String[] lines;
                            do {
                                if(!var10.hasNext()) {
                                    return;
                                }

                                org.bukkit.block.Sign attachedSign = (org.bukkit.block.Sign)var10.next();
                                lines = attachedSign.getLines();
                            } while(!lines[0].equals(SUBCLAIM_PREFIX));

                            for(int i = 1; i < lines.length; ++i) {
                                if(lines[i].contains(search)) {
                                    return;
                                }
                            }

                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You cannot break this subclaimed " + block.getType().getData().getName() + '.');
                        }
                    }
                }

            }
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if(!this.plugin.getEotwHandler().isEndOfTheWorld()) {
            InventoryHolder holder = event.getSource().getHolder();
            Object sourceBlocks;
            if(holder instanceof Chest) {
                sourceBlocks = Collections.singletonList(((Chest)holder).getBlock());
            } else {
                if(!(holder instanceof DoubleChest)) {
                    return;
                }

                DoubleChest doubleChest = (DoubleChest)holder;
                sourceBlocks = Lists.newArrayList(((Chest)doubleChest.getLeftSide()).getBlock(), ((Chest)doubleChest.getRightSide()).getBlock());
            }

            Iterator doubleChest1 = ((Collection)sourceBlocks).iterator();

            while(true) {
                while(doubleChest1.hasNext()) {
                    Block block = (Block)doubleChest1.next();
                    Collection attachedSigns = this.getAttachedSigns(block);
                    Iterator var7 = attachedSigns.iterator();

                    while(var7.hasNext()) {
                        org.bukkit.block.Sign attachedSign = (org.bukkit.block.Sign)var7.next();
                        if(attachedSign.getLine(0).equals(SUBCLAIM_PREFIX)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }

                return;
            }
        }
    }

    private String getShortenedName(String originalName) {
        if(originalName.length() == 16) {
            originalName = originalName.substring(0, 15);
        }

        return originalName;
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if(player.getGameMode() != GameMode.CREATIVE || !player.hasPermission("hcf.faction.protection.bypass")) {
                if(!this.plugin.getEotwHandler().isEndOfTheWorld()) {
                    Block block = event.getClickedBlock();
                    if(this.isSubclaimable(block)) {
                        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                        if(playerFaction == null || playerFaction.isRaidable()) {
                            return;
                        }

                        Role role = playerFaction.getMember(player).getRole();
                        if(role == Role.LEADER) {
                            return;
                        }

                        if(playerFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                            Collection attachedSigns = this.getAttachedSigns(block);
                            if(attachedSigns.isEmpty()) {
                                return;
                            }

                            String search = this.getShortenedName(player.getName());
                            Iterator var8 = attachedSigns.iterator();

                            while(var8.hasNext()) {
                                org.bukkit.block.Sign attachedSign = (org.bukkit.block.Sign)var8.next();
                                String[] lines = attachedSign.getLines();
                                if(lines[0].equals(SUBCLAIM_PREFIX)) {
                                    if(!Role.LEADER.getAstrix().equals(lines[1])) {
                                        for(int i = 1; i < lines.length; ++i) {
                                            if(lines[i].contains(search)) {
                                                return;
                                            }
                                        }
                                    }

                                    if(role == Role.CAPTAIN) {
                                        if(!lines[1].contains("Leader")) {
                                            return;
                                        }

                                        event.setCancelled(true);
                                        player.sendMessage(ChatColor.RED + "You do not have access to this subclaimed " + block.getType().getData().getName() + '.');
                                    } else {
                                        event.setCancelled(true);
                                        player.sendMessage(ChatColor.RED + "You do not have access to this subclaimed " + block.getType().getData().getName() + '.');
                                    }
                                    break;
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    public Collection<org.bukkit.block.Sign> getAttachedSigns(Block block) {
        HashSet results = new HashSet();
        this.getSignsAround(block, results);
        BlockState state = block.getState();
        if(state instanceof Chest) {
            Inventory chestInventory = ((Chest)state).getInventory();
            if(chestInventory instanceof DoubleChestInventory) {
                DoubleChest doubleChest = ((DoubleChestInventory)chestInventory).getHolder();
                Block left = ((Chest)doubleChest.getLeftSide()).getBlock();
                Block right = ((Chest)doubleChest.getRightSide()).getBlock();
                this.getSignsAround(left.equals(block)?right:left, results);
            }
        }

        return results;
    }

    private Set<org.bukkit.block.Sign> getSignsAround(Block block, Set<org.bukkit.block.Sign> results) {
        BlockFace[] var3 = SIGN_FACES;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            BlockFace face = var3[var5];
            Block relative = block.getRelative(face);
            BlockState relativeState = relative.getState();
            if(relativeState instanceof org.bukkit.block.Sign) {
                Sign materialSign = (Sign)relativeState.getData();
                if(relative.getRelative(materialSign.getAttachedFace()).equals(block)) {
                    results.add((org.bukkit.block.Sign)relative.getState());
                }
            }
        }

        return results;
    }

    static {
        SUBCLAIM_PREFIX = ChatColor.YELLOW.toString() + "[Subclaim]";
        SIGN_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
    }
}