package de.kleiner3.lasertag.mixin;

import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplate.class)
public interface IStructureTemplateAccessor {
    @Accessor
    List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists();

    @Accessor
    List<StructureTemplate.StructureEntityInfo> getEntities();
}
