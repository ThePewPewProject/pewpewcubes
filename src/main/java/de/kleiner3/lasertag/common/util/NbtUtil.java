package de.kleiner3.lasertag.common.util;

import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;

/**
 * Util class for NbtCompound operations
 *
 * @author Étienne Muser
 */
public class NbtUtil {
    /**
     * Converts a NbtCompound deserialized from a .litematic file and converts it into a NbtCompound with the format of a .nbt file
     *
     * @param litematic      The litematic to convert
     * @param mainRegionName The name of the main region
     * @param offset         The vector by which to offset the converted litematic
     * @return The converted nbt
     */
    public static NbtCompound convertLitematicToNbt(NbtCompound litematic, String mainRegionName, Vec3i offset) {

        try {
            // Create new nbt compound
            var nbt = new NbtCompound();

            // Get the regions element
            var regionsElement = litematic.get("Regions");

            // If the regions element is not a nbt compound
            if (!(regionsElement instanceof NbtCompound regionsCompound)) {
                return null;
            }

            // Get the main region element
            var mainRegionCompound = regionsCompound.getCompound(mainRegionName);

            // Add size nbt list
            if (!addSize(nbt, mainRegionCompound)) {
                return null;
            }

            // Add entities list
            if (!addEntities(nbt, mainRegionCompound, offset)) {
                return null;
            }

            // Get size element
            var sizeCompound = mainRegionCompound.getCompound("Size");

            // Get the palette
            var paletteList = mainRegionCompound.getList("BlockStatePalette", NbtElement.COMPOUND_TYPE);

            // Add blocks list
            if (!addBlockList(nbt, mainRegionCompound, sizeCompound, paletteList, offset)) {
                return null;
            }

            // Add palette list
            nbt.put("palette", paletteList);

            // Add data version int
            nbt.put("DataVersion", litematic.get("MinecraftDataVersion"));

            return nbt;
        } catch (Exception ex) {

            LasertagMod.LOGGER.error("Could not convert litematic to nbt:", ex);
            return null;
        }
    }

    //region convertLitematicToNbt helper

    private static boolean addSize(NbtCompound nbt, NbtCompound litematicMainRegion) {

        // Get size element
        var sizeCompound = litematicMainRegion.getCompound("Size");

        // Convert to list
        var sizeList = xyzCompoundToList(sizeCompound);

        // Check that size must be positive
        if (sizeList.getInt(0) <= 0 ||
                sizeList.getInt(1) <= 0 ||
                sizeList.getInt(2) <= 0) {

            throw new IllegalArgumentException("All dimensions of arena must be positive. Size: " + sizeList);
        }

        // Put
        nbt.put("size", sizeList);

        return true;
    }

    private static boolean addEntities(NbtCompound nbt, NbtCompound litematicMainRegion, Vec3i offset) {

        // Get the entities element
        var entitiesList = litematicMainRegion.getList("Entities", NbtElement.COMPOUND_TYPE);

        // Create new list
        var list = new NbtList();

        // For every entity
        for (var entity : entitiesList) {
            // If is not nbtCompound
            if (!(entity instanceof NbtCompound entityCompound)) {
                return false;
            }

            // Add to list
            if (!addEntityToList(list, entityCompound, offset)) {
                return false;
            }
        }

        // Put
        nbt.put("entities", list);

        return true;
    }

    private static boolean addEntityToList(NbtList list, NbtCompound litematicEntity, Vec3i offset) {

        // Get pos of entity
        var posList = litematicEntity.getList("Pos", NbtElement.DOUBLE_TYPE);

        // Apply offset to position
        posList.set(0, NbtDouble.of(posList.getDouble(0) - offset.getX()));
        posList.set(1, NbtDouble.of(posList.getDouble(1) - offset.getY()));
        posList.set(2, NbtDouble.of(posList.getDouble(2) - offset.getZ()));

        // If has leash
        if (litematicEntity.contains("Leash")) {
            // Get the leash compound
            var leash = litematicEntity.getCompound("Leash");
            // Apply offset to leash
            leash.putInt("X", litematicEntity.getInt("X") - offset.getX());
            leash.putInt("Y", litematicEntity.getInt("Y") - offset.getY());
            leash.putInt("Z", litematicEntity.getInt("Z") - offset.getZ());
        }

        // If has tile xyz
        if (litematicEntity.contains("TileX")) {
            // Apply offset to tile xyz
            litematicEntity.putInt("TileX", litematicEntity.getInt("TileX") - offset.getX());
            litematicEntity.putInt("TileY", litematicEntity.getInt("TileY") - offset.getY());
            litematicEntity.putInt("TileZ", litematicEntity.getInt("TileZ") - offset.getZ());
        }

        // Create new entity
        var entity = new NbtCompound();

        // Add pos to entity
        entity.put("pos", posList);
        litematicEntity.put("Pos", posList);

        // Add nbt to entity
        entity.put("nbt", litematicEntity);

        // Add entity to list
        list.add(entity);

        return true;
    }

    /**
     * Adds the blocks (and their tile entities) skips air blocks
     *
     * @param nbt
     * @param litematicMainRegion
     * @param size
     * @param palette
     * @return
     */
    private static boolean addBlockList(NbtCompound nbt, NbtCompound litematicMainRegion, NbtCompound size, NbtList palette, Vec3i offset) {

        // Create block list
        var list = new NbtList();

        // Get size
        var sizeX = size.getInt("x");
        var sizeY = size.getInt("y");
        var sizeZ = size.getInt("z");

        // Get block state array element
        var blockStateArrayElement = litematicMainRegion.get("BlockStates");

        // If is not long array
        if (!(blockStateArrayElement instanceof NbtLongArray blockStateArray)) {
            return false;
        }

        // Get the tile entities element
        var tileEntitiesList = litematicMainRegion.getList("TileEntities", NbtElement.COMPOUND_TYPE);

        // Get the index of air in the palette
        var airIndex = palette.stream()
                .map(NbtCompound.class::cast)
                .map(el -> el.getString("Name"))
                .toList()
                .indexOf("minecraft:air");

        // Calculate number of bits
        var bits = Math.max(2, Integer.SIZE - Integer.numberOfLeadingZeros(palette.size() - 1));

        // Calculate max entry value
        var maxEntryValue = (1L << bits) - 1L;

        // Get the tile entity iterator.
        // Order by calculated index so that no entity is
        // missed when iterating over all blocks later.
        var tileEntityIterator = tileEntitiesList.stream()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .sorted(Comparator.comparingInt(te -> (te.getInt("x") * sizeZ * sizeY) +
                        (te.getInt("y") * sizeZ) +
                        te.getInt("z")))
                .iterator();

        // Save the next tile entity pos and
        BlockPos nextTileEntityPos = null;
        NbtCompound nextTileEntityCompound = null;

        // If there are tile entities - Load the first tile entity
        if (tileEntityIterator.hasNext()) {
            nextTileEntityCompound = tileEntityIterator.next();
            nextTileEntityPos = tileEntityCompoundToBlockPos(nextTileEntityCompound, offset);
        }

        // Iterate over every possible block
        // DO NOT CHANGE THE ORDER OF THIS NESTED LOOP OR IT WILL BREAK THE ADD TILE ENTITY METHOD
        // Start at 0. DON'T add offset as tileEntityCompoundToBlockPos expects this to not use the offset
        for (long x = 0; x < sizeX; x++) {
            for (long y = 0; y < sizeY; y++) {
                for (long z = 0; z < sizeZ; z++) {

                    // Get index in array
                    var index = (y * sizeX * sizeZ) + (z * sizeX) + x;

                    // Get the block state index in the palette
                    var paletteIndex = getBlockAt(blockStateArray.getLongArray(), index, bits, maxEntryValue);

                    // Skip air
                    if (paletteIndex == airIndex) {
                        continue;
                    }

                    // Create block nbt compound
                    var block = new NbtCompound();

                    // Create pos list
                    var pos = new NbtList();
                    pos.add(NbtInt.of((int) x - offset.getX()));
                    pos.add(NbtInt.of((int) y - offset.getY()));
                    pos.add(NbtInt.of((int) z - offset.getZ()));

                    // Add pos to block
                    block.put("pos", pos);

                    // Add state to block
                    block.put("state", NbtInt.of(paletteIndex));

                    // If this block is the next tile entity
                    if (nextTileEntityPos != null && nextTileEntityPos.equals(new BlockPos(x, y, z))) {
                        addTileEntityToBlock(block, nextTileEntityCompound);

                        // If there is a next tile entity - Load the next tile entity
                        if (tileEntityIterator.hasNext()) {
                            nextTileEntityCompound = tileEntityIterator.next();
                            nextTileEntityPos = tileEntityCompoundToBlockPos(nextTileEntityCompound, offset);

                            // Otherwise set the next tile entity to null as there is no next tile entity
                        } else {
                            nextTileEntityCompound = null;
                            nextTileEntityPos = null;
                        }
                    }

                    // Add block to list
                    list.add(block);
                }
            }
        }

        // Add block list to nbt
        nbt.put("blocks", list);

        return true;
    }

    private static BlockPos tileEntityCompoundToBlockPos(NbtCompound tileEntity, Vec3i offset) {

        // Get pos of entity
        // Offset not necessary as loop looping over all possible block positions
        // also doesn't use the offset
        var xPos = tileEntity.getInt("x");
        var yPos = tileEntity.getInt("y");
        var zPos = tileEntity.getInt("z");

        return new BlockPos(xPos, yPos, zPos);
    }

    private static void addTileEntityToBlock(NbtCompound block, NbtCompound litematicTileEntity) {

        // Remove pos from litematic entity
        litematicTileEntity.remove("x");
        litematicTileEntity.remove("y");
        litematicTileEntity.remove("z");

        block.put("nbt", litematicTileEntity);
    }

    private static int getBlockAt(long[] array, long index, int bitsPerEntry, long maxEntryValue) {
        var startOffset = index * (long) bitsPerEntry;
        var startArrIndex = (int) (startOffset >> 6); // startOffset / 64
        var endArrIndex = (int) (((index + 1L) * (long) bitsPerEntry - 1L) >> 6);
        var startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64

        var valAtStartIndex = array[startArrIndex];

        if (startArrIndex == endArrIndex) {
            return (int) (valAtStartIndex >>> startBitOffset & maxEntryValue);
        } else {
            var valAtEndIndex = array[endArrIndex];

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
