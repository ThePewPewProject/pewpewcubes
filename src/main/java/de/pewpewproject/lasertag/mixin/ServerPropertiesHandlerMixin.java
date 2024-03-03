package de.pewpewproject.lasertag.mixin;

import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the ServerPropertiesHandler.WorldGenProperties.class to force the load
 * of a lasertag arena on dedicated servers
 *
 * @author Étienne Muser
 */
@Mixin(ServerPropertiesHandler.WorldGenProperties.class)
public abstract class ServerPropertiesHandlerMixin {

    @Inject(method = "createGeneratorOptions(Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/world/gen/GeneratorOptions;", at = @At("HEAD"))
    private void onCreateGeneratorOptions(DynamicRegistryManager dynamicRegistryManager, CallbackInfoReturnable<GeneratorOptions> cir) {

        // Overwrite the level type to force the load of a lasertag arena
        ((ServerPropertiesHandler.WorldGenProperties)(Object)this).levelType = "lasertag:lasertag_arena";
    }
}
