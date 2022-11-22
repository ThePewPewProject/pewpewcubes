package de.kleiner3.lasertag.item;

import net.minecraft.item.ItemGroup;

/**
 * Class for registering all item groups
 *
 * @author Étienne Muser
 */
public class ItemGroups {
    // Item group for lasertag items
    public static final ItemGroup LASERTAG_ITEM_GROUP = LasertagItemGroupBuilder.build();
}
