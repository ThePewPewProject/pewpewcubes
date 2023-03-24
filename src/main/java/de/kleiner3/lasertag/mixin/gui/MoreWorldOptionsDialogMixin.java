package de.kleiner3.lasertag.mixin.gui;

import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaType;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/**
 * Mixin into the MoreOptionsDialog class to implement the arena selection
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
@Mixin(MoreOptionsDialog.class)
public abstract class MoreWorldOptionsDialogMixin {

    private CyclingButtonWidget<ArenaType> arenaTypeButton;

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
                       });
        this.arenaTypeButton.visible = false;

        parent.addDrawableChild(this.arenaTypeButton);
    }

    @Inject(method = "setVisible", at = @At("TAIL"))
    private void setVisibleOverwrite(boolean visible, CallbackInfo ci) {
        this.arenaTypeButton.visible = false;

        // If lasertag arena chunk generator is selected
        if (this.generatorOptionsHolder.generatorOptions().getChunkGenerator() instanceof ArenaChunkGenerator) {
            // Show arena type button
            this.arenaTypeButton.visible = visible;

            // Hide all other buttons
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;
        }
    }

    private static Text getArenaTypeText(ArenaType arenaType) {
        return Text.translatable(arenaType.translatableName);
    }
}
