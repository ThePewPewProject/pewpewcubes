package de.kleiner3.lasertag.mixin;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.Bootstrap;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Mixin into the DedicatedServerWatchdog.class to disable the watchdog during map load
 *
 * @author Étienne Muser
 */
@Mixin(DedicatedServerWatchdog.class)
public abstract class DedicatedServerWatchdogMixin {

    private boolean isIgnoring = false;

    @Inject(method = "run()V", at = @At("HEAD"), cancellable = true)
    private void runOverride(CallbackInfo ci) {

        var thisWD = ((DedicatedServerWatchdog) (Object) this);

        while (thisWD.server.isRunning()) {

            // Get the game managers
            var gameManager = thisWD.server.getOverworld().getServerLasertagManager();
            var arenaManager = gameManager.getArenaManager();

            // If map is loading, ignore
            if (arenaManager.isLoading()) {
                this.isIgnoring = true;
            }

            long lastServerTimeReference = ((DedicatedServerWatchdog) (Object) this).server.getTimeReference();
            long now = Util.getMeasuringTimeMs();


            long difference = now - lastServerTimeReference;
            if (difference > ((DedicatedServerWatchdog) (Object) this).maxTickTime) {
                if (!this.isIgnoring) {

                    DedicatedServerWatchdog.LOGGER.error(LogUtils.FATAL_MARKER, "A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float) difference / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
                    DedicatedServerWatchdog.LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
                    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                    ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
                    StringBuilder stringBuilder = new StringBuilder();
                    Error error = new Error("Watchdog");
                    ThreadInfo[] var11 = threadInfos;
                    int var12 = threadInfos.length;

                    for (int var13 = 0; var13 < var12; ++var13) {
                        ThreadInfo threadInfo = var11[var13];
                        if (threadInfo.getThreadId() == ((DedicatedServerWatchdog) (Object) this).server.getThread().getId()) {
                            error.setStackTrace(threadInfo.getStackTrace());
                        }

                        stringBuilder.append(threadInfo);
                        stringBuilder.append("\n");
                    }

                    CrashReport crashReport = new CrashReport("Watching Server", error);
                    ((DedicatedServerWatchdog) (Object) this).server.addSystemDetails(crashReport.getSystemDetailsSection());
                    CrashReportSection crashReportSection = crashReport.addElement("Thread Dump");
                    crashReportSection.add("Threads", stringBuilder);
                    CrashReportSection crashReportSection2 = crashReport.addElement("Performance stats");
                    crashReportSection2.add("Random tick rate", () -> ((DedicatedServerWatchdog) (Object) this).server.getSaveProperties().getGameRules().get(GameRules.RANDOM_TICK_SPEED).toString());
                    crashReportSection2.add("Level stats", () -> Streams.stream(((DedicatedServerWatchdog) (Object) this).server.getWorlds()).map((serverWorld) -> {
                        RegistryKey var10000 = serverWorld.getRegistryKey();
                        return "" + var10000 + ": " + serverWorld.getDebugString();
                    }).collect(Collectors.joining(",\n")));
                    Bootstrap.println("Crash report:\n" + crashReport.asString());
                    File file = new File(new File(((DedicatedServerWatchdog) (Object) this).server.getRunDirectory(), "crash-reports"), "crash-" + Util.getFormattedCurrentTime() + "-server.txt");
                    if (crashReport.writeToFile(file)) {
                        DedicatedServerWatchdog.LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
                    } else {
                        DedicatedServerWatchdog.LOGGER.error("We were unable to save this crash report to disk.");
                    }

                    ((DedicatedServerWatchdog) (Object) this).shutdown();
                } else {
                    // Only ignore the first time
                    this.isIgnoring = false;
                    LasertagMod.LOGGER.info("Watchdog now not ignoring anymore.");
                }
            }

            try {
                Thread.sleep(((DedicatedServerWatchdog) (Object) this).maxTickTime);
            } catch (InterruptedException ignored) {
            }
        }

        ci.cancel();
    }
}
