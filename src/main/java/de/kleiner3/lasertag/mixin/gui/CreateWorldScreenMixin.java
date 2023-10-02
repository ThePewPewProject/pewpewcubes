package de.kleiner3.lasertag.mixin.gui;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.resource.*;
import net.minecraft.server.SaveLoading;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

/**
 * Mixin into the CreateWorldScreen.class to set the cheats enabled by default.
 *
 * @author Étienne Muser
 */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    @Shadow
    private static void showMessage(MinecraftClient client, Text text) {
    }

    @Shadow
    @Final
    private static Text PREPARING_TEXT;

    @Shadow
    private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings) {
        return null;
    }

    @Inject(method = "create(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"),cancellable = true)
    private static void setCheatsEnabledTrue(MinecraftClient client, Screen parent, CallbackInfo ci) {

        showMessage(client, PREPARING_TEXT);
        ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider());
        SaveLoading.ServerConfig serverConfig = createServerConfig(resourcePackManager, DataPackSettings.SAFE_MODE);
        CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(serverConfig, (resourceManager, dataPackSettings) -> {
            DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.createAndLoad().toImmutable();
            GeneratorOptions generatorOptions = WorldPresets.createDefaultOptions(immutable);
            return Pair.of(generatorOptions, immutable);
        }, (resourceManager, dataPackContents, dynamicRegistryManager, generatorOptions) -> {
            resourceManager.close();
            return new GeneratorOptionsHolder(generatorOptions, Lifecycle.stable(), dynamicRegistryManager, dataPackContents);
        }, Util.getMainWorkerExecutor(), client);
        Objects.requireNonNull(completableFuture);
        client.runTasks(completableFuture::isDone);
        var createWorldScreen = new CreateWorldScreen(parent, DataPackSettings.SAFE_MODE, new MoreOptionsDialog(completableFuture.join(), Optional.of(WorldPresets.DEFAULT), OptionalLong.empty()));
        createWorldScreen.cheatsEnabled = true;
        createWorldScreen.tweakedCheats = true;
        createWorldScreen.levelName = I18n.translate("selectWorld.newArena");
        client.setScreen(createWorldScreen);

        ci.cancel();
    }
}
