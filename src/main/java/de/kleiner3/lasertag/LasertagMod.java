package de.kleiner3.lasertag;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class initializes the mod.
 * 
 * @author Étienne Muser
 *
 */
public class LasertagMod implements ModInitializer {
	
	// Log4j logger instance for this mod
	public static final Logger LOGGER = LoggerFactory.getLogger("lasertag-mod");
	
	@Override
	public void onInitialize() {
		LOGGER.info("Lasertag mod initialized!");
	}
}
