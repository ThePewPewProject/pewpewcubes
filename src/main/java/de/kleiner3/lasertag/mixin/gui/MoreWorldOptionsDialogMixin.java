package de.kleiner3.lasertag.mixin.gui;

import de.kleiner3.lasertag.worldgen.WorldPresets;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ProceduralArenaType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Random;

/**
 * Mixin into the MoreOptionsDialog class to implement the arena selection
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
@Mixin(MoreOptionsDialog.class)
public abstract class MoreWorldOptionsDialogMixin {

    private CyclingButtonWidget<ArenaType> arenaTypeButton;

    private CyclingButtonWidget<ProceduralArenaType> proceduralArenaTypeButton;

    @Shadow
    private GeneratorOptionsHolder generatorOptionsHolder;

    @Shadow
    private CyclingButtonWidget<Boolean> mapFeaturesButton;
    @Shadow
    private CyclingButtonWidget<Boolean> bonusItemsButton;
    @Shadow
    private ButtonWidget customizeTypeButton;
    @Shadow
    private ButtonWidget importSettingsButton;

    @Shadow
    private Optional<RegistryEntry<WorldPreset>> presetEntry;

    @Inject(method = "init", at = @At("HEAD"))
    private void initCustomButtons(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer, CallbackInfo ci) {
        int leftPadding = parent.width / 2 - 155;
        int standardButtonWidth = 150;
        int standardButtonHeight = 20;

        this.arenaTypeButton = CyclingButtonWidget
                .builder(MoreWorldOptionsDialogMixin::getArenaTypeText)
                .values(Arrays.stream(ArenaType.values()).toList())
                .build(leftPadding, 100,
                       standardButtonWidth, standardButtonHeight,
                       Text.translatable("selectWorld.arenaType"),

                       // Button click handler
                       (button, arenaType) -> {
                           // Get the chunk generator
                           var chunkGenerator = this.generatorOptionsHolder.generatorOptions().getChunkGenerator();

                           // Cast to ArenaChunkGenerator
                           var arenaChunkGenerator = (ArenaChunkGenerator)chunkGenerator;

                           // Set arena type
                           arenaChunkGenerator.getConfig().setType(arenaType.ordinal());

                           parent.setMoreOptionsOpen();
                       });
        this.arenaTypeButton.visible = false;

        this.proceduralArenaTypeButton = CyclingButtonWidget
                .builder(MoreWorldOptionsDialogMixin::getProceduralArenaTypeText)
                .values(Arrays.stream(ProceduralArenaType.values()).toList())
                .build(leftPadding, 120,
                        standardButtonWidth, standardButtonHeight,
                        Text.translatable("selectWorld.proceduralType"),

                        // Button click handler
                        (button, arenaType) -> {
                            // Get the chunk generator
                            var chunkGenerator = this.generatorOptionsHolder.generatorOptions().getChunkGenerator();

                            // Cast to ArenaChunkGenerator
                            var arenaChunkGenerator = (ArenaChunkGenerator)chunkGenerator;

                            // Set arena type
                            arenaChunkGenerator.getConfig().setProceduralType(arenaType.ordinal());

                            // Set to random seed
                            arenaChunkGenerator.getConfig().setSeed((new Random()).nextLong());
                        });
        this.proceduralArenaTypeButton.visible = false;

        parent.addDrawableChild(this.arenaTypeButton);
        parent.addDrawableChild(this.proceduralArenaTypeButton);
    }

    @Inject(method = "setVisible", at = @At("TAIL"))
    private void setVisibleOverwrite(boolean visible, CallbackInfo ci) {
        this.arenaTypeButton.visible = false;
        this.proceduralArenaTypeButton.visible = false;

        // If lasertag arena chunk generator is selected
        if (this.generatorOptionsHolder.generatorOptions().getChunkGenerator() instanceof ArenaChunkGenerator arenaChunkGenerator) {
            // Show arena type button
            this.arenaTypeButton.visible = visible;

            // Hide all other buttons
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;

            // If procedural arena is selected
            if(arenaChunkGenerator.getConfig().getType() == ArenaType.PROCEDURAL) {
                this.proceduralArenaTypeButton.visible = visible;
            }
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/client/world/GeneratorOptionsHolder;Ljava/util/Optional;Ljava/util/OptionalLong;)V", at = @At("TAIL"))
    private void createArenaPresetEntryIfDefault(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> presetKey, OptionalLong seed, CallbackInfo ci) {
        presetKey.ifPresent(presetKeyValue -> {
            if (presetKeyValue.equals(net.minecraft.world.gen.WorldPresets.DEFAULT)) {

                var entryOptional = Optional.of(WorldPresets.ARENA).flatMap((key) -> this.generatorOptionsHolder.dynamicRegistryManager().get(Registry.WORLD_PRESET_KEY).getEntry(key));

                entryOptional.ifPresent(entry -> {
                    this.generatorOptionsHolder = this.generatorOptionsHolder.apply(genOpt -> entry.value().createGeneratorOptions(genOpt));
                    this.presetEntry = entryOptional;
                });
            }
        });
    }

    private static Text getArenaTypeText(ArenaType arenaType) {
        return Text.translatable(arenaType.translatableName);
    }

    private static Text getProceduralArenaTypeText(ProceduralArenaType proceduralArenaType) { return Text.translatable(proceduralArenaType.translatableName); }
}
