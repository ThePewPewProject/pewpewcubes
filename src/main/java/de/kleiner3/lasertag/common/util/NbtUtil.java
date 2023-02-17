package de.kleiner3.lasertag.common.util;

import net.minecraft.nbt.*;

/**
 * Util class for NbtCompound operations
 *
 * @author Étienne Muser
 */
public class NbtUtil {
    /**
     * Converts a NbtCompound deserialized from a .litematic file and converts it into a NbtCompound with the format of a .nbt file
     * @param litematic
     * @param mainRegionName
     * @return
     */
    public static NbtCompound convertLitematicToNbt(NbtCompound litematic, String mainRegionName) {
        // Create new nbt compound
        var nbt = new NbtCompound();

        // Get the regions element
        var regionsElement = litematic.get("Regions");

        // If the regions element is not a nbt compound
        if ((regionsElement instanceof NbtCompound) == false) {
            return null;
        }

        // Cast
        var regionsCompound = (NbtCompound)regionsElement;

        // Get the main region element
        var mainRegionElement = regionsCompound.get(mainRegionName);

        // If the main region element is not a nbt compound
        if ((mainRegionElement instanceof NbtCompound) == false) {
            return null;
        }

        // Cast
        var mainRegionCompound = (NbtCompound)mainRegionElement;

        // Add size nbt list
        if (addSize(nbt, mainRegionCompound) == false) {
            return null;
        }

        // Add entities list
        if (addEntities(nbt, mainRegionCompound) == false) {
            return null;
        }

        // Get size element
        var sizeElement = mainRegionCompound.get("Size");

        // Check
        if ((sizeElement instanceof NbtCompound) == false) {
            return null;
        }

        // Cast
        var sizeCompound = (NbtCompound)sizeElement;

        // Get the palette
        var paletteElement = mainRegionCompound.get("BlockStatePalette");

        // Check
        if ((paletteElement instanceof NbtList) == false) {
            return null;
        }

        // Cast
        var paletteList = (NbtList)paletteElement;

        // Add blocks list
        if (addBlockList(nbt, mainRegionCompound, sizeCompound, paletteList) == false) {
            return null;
        }

        // Add palette list
        nbt.put("palette", paletteElement);

        // Add data version int
        nbt.put("DataVersion", litematic.get("MinecraftDataVersion"));

        return nbt;
    }

    //region convertLitematicToNbt helper

    private static boolean addSize(NbtCompound nbt, NbtCompound litematicMainRegion) {
        // Get size element
        var sizeElement = litematicMainRegion.get("Size");

        // If is not nbt compound
        if ((sizeElement instanceof NbtCompound) == false) {
            return false;
        }

        // Cast
        var sizeCompound = (NbtCompound)sizeElement;

        // Convert to list
        var sizeList = xyzCompoundToList(sizeCompound);

        // Put
        nbt.put("size", sizeList);

        return true;
    }

    private static boolean addEntities(NbtCompound nbt, NbtCompound litematicMainRegion) {
        // Get the entities element
        var entitiesElement = litematicMainRegion.get("Entities");

        // If is not nbt list
        if ((entitiesElement instanceof NbtList) == false) {
            return false;
        }

        // Cast
        var entitiesList = (NbtList)entitiesElement;

        // Create new list
        var list = new NbtList();

        // For every entity
        for (var entity : entitiesList) {
            // If is not nbtCompound
            if ((entity instanceof NbtCompound) == false) {
                return false;
            }

            // Cast
            var entityCompound = (NbtCompound)entity;

            // Add to list
            if (addEntityToList(list, entityCompound) == false) {
                return false;
            }
        }

        // Put
        nbt.put("entities", list);

        return true;
    }

    private static boolean addEntityToList(NbtList list, NbtCompound litematicEntity) {
        // Get pos of entity
        var posElement = litematicEntity.get("Pos");

        // If pos is not nbt list
        if ((posElement instanceof NbtList) == false) {
            return false;
        }

        // Cast
        var posList = (NbtList)posElement;

        // Create new entity
        var entity = new NbtCompound();

        // Add pos to entity
        entity.put("pos", posList);

        // Remove pos from litematica entity
        litematicEntity.remove("Pos");

        // Add nbt to entity
        entity.put("nbt", litematicEntity);

        // Add entity to list
        list.add(entity);

        return true;
    }

    private static boolean addBlockList(NbtCompound nbt, NbtCompound litematicMainRegion, NbtCompound size, NbtList palette) {
        // Create block list
        var list = new NbtList();

        // Get size
        var sizeX = ((NbtInt)size.get("x")).intValue();
        var sizeY = ((NbtInt)size.get("y")).intValue();
        var sizeZ = ((NbtInt)size.get("z")).intValue();

        // Get block state array element
        var blockStateArrayElement = litematicMainRegion.get("BlockStates");

        // If is not long array
        if ((blockStateArrayElement instanceof NbtLongArray) == false) {
            return false;
        }

        // Cast
        var blockStateArray = (NbtLongArray)blockStateArrayElement;

        // Calculate number of bits
        var bits = Math.max(2, Integer.SIZE - Integer.numberOfLeadingZeros(palette.size() - 1));

        // Calculate max entry value
        var maxEntryValue = (1L << bits) - 1L;

        // Iterate over every possible block
        for (long x = 0; x < sizeX; x++) {
            for (long y = 0; y < sizeY; y++) {
                for (long z = 0; z < sizeZ; z++) {
                    // Get index in array
                    var index = (y * sizeX * sizeZ) + (z * sizeX) + x; // TODO: Verify this calculation

                    // Get the block state index in the palette
                    var paletteIndex = getBlockAt(blockStateArray, index, bits, maxEntryValue);

                    // Create block nbt compound
                    var block = new NbtCompound();

                    // Create pos list
                    var pos = new NbtList();
                    pos.add(NbtInt.of((int) x));
                    pos.add(NbtInt.of((int) y));
                    pos.add(NbtInt.of((int) z));

                    // Add pos to block
                    block.put("pos", pos);

                    // Add state to block
                    block.put("state", NbtInt.of(paletteIndex));

                    // Add block to list
                    list.add(block);
                }
            }
        }

        // Add block list to nbt
        nbt.put("blocks", list);

        return true;
    }

    private static int getBlockAt(NbtLongArray array, long index, int bitsPerEntry, long maxEntryValue) {
        var startOffset = index * (long) bitsPerEntry;
        var startArrIndex = (int) (startOffset >> 6); // startOffset / 64
        var endArrIndex = (int) (((index + 1L) * (long) bitsPerEntry - 1L) >> 6);
        var startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64

        var valAtStartIndex = array.get(startArrIndex).longValue();

        if (startArrIndex == endArrIndex)
        {
            return (int) (valAtStartIndex >>> startBitOffset & maxEntryValue);
        }
        else
        {
            var valAtEndIndex = array.get(endArrIndex).longValue();

            var endOffset = 64 - startBitOffset;
            return (int) ((valAtStartIndex >>> startBitOffset | valAtEndIndex << endOffset) & maxEntryValue);
        }
    }

    //endregion

    public static NbtList xyzCompoundToList(NbtCompound compound) {
        // Create list
        var list = new NbtList();

        // Add to list
        list.add(0, compound.get("x"));
        list.add(1, compound.get("y"));
        list.add(2, compound.get("z"));

        return list;
    }
}
