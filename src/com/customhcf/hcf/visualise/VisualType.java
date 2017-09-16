
package com.customhcf.hcf.visualise;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.customhcf.hcf.HCF;
import com.customhcf.hcf.faction.FactionManager;
import com.customhcf.hcf.faction.struct.Relation;
import com.customhcf.hcf.faction.type.Faction;
import com.customhcf.hcf.visualise.BlockFiller;
import com.customhcf.hcf.visualise.VisualBlockData;

public enum VisualType
{
    SPAWN_BORDER {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    CLAIM_BORDER {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    SUBCLAIM_MAP {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.LOG, (byte) 1);
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    CLAIM_MAP {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                private final Material[] types = { Material.SNOW_BLOCK, Material.SANDSTONE, Material.FURNACE, Material.NETHERRACK, Material.GLOWSTONE, Material.LAPIS_BLOCK, Material.NETHER_BRICK, Material.DIAMOND_ORE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE };
                private int materialCounter = 0;

                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    final int y = location.getBlockY();
                    if (y == 0 || y % 3 == 0) {
                        return new VisualBlockData(this.types[this.materialCounter]);
                    }
                    final Faction faction = HCF.getPlugin().getFactionManager().getFactionAt(location);
                    return new VisualBlockData(Material.STAINED_GLASS, ((faction != null) ? faction.getRelation((CommandSender)player) : Relation.ENEMY).toDyeColour().getData());
                }

                @Override
                ArrayList<VisualBlockData> bulkGenerate(final Player player, final Iterable<Location> locations) {
                    final ArrayList<VisualBlockData> result = super.bulkGenerate(player, locations);
                    if (++this.materialCounter == this.types.length) {
                        this.materialCounter = 0;
                    }
                    return result;
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    CREATE_CLAIM_SELECTION {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    if(location.getBlockY() % 3 == 0){
                        return new VisualBlockData(Material.STAINED_GLASS, DyeColor.YELLOW.getData());
                    }
                    else{
                        return new VisualBlockData(Material.GLOWSTONE);
                    }
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    },
    WORLD_BORDER {
        private final BlockFiller blockFiller;

        {
            this.blockFiller = new BlockFiller() {
                @Override
                VisualBlockData generate(final Player player, final Location location) {
                    return new VisualBlockData(Material.STAINED_GLASS, DyeColor.RED.getData());
                }
            };
        }

        @Override
        BlockFiller blockFiller() {
            return this.blockFiller;
        }
    };

    abstract BlockFiller blockFiller();
}