package de.pewpewproject.lasertag.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class with utility methods for working with threads
 *
 * @author Étienne Muser
 */
public class ThreadUtil {
    /**
     * Correctly shuts down a executor service with timeout = 3 seconds
     * <p>
     * ALWAYS DO THIS AS THE LAST OPERATION! This method blocks for 3 seconds!
     * Do everything you need to do before calling this method!
     * @param service The executor service to shut down
     */
    public static void attemptShutdown(ExecutorService service) {
        attemptShutdown(service, 3L);
    }

    /**
     * Correctly shuts down a executor service
     * <p>
     * ALWAYS DO THIS AS THE LAST OPERATION! This method blocks for <code>timeout</code> seconds!
     * Do everything you need to do before calling this method!
     * @param service The executor service to shut down
     * @param timeout The timeout in seconds to use when waiting for termination
     */
    public static void attemptShutdown(ExecutorService service, long timeout) {
        service.shutdown();

        boolean successful;
        try {
            successful = service.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException var3) {
            successful = false;
        }

        if (!successful) {
            service.shutdownNow();
        }
    }

    /**
     * Create a scheduled executor service with the given format as the name specifier for the threads
     * @param format The name format for the created threads
     * @return
     */
    public static ScheduledExecutorService createScheduledExecutor(String format) {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(format).setDaemon(true).build());
    }
}
