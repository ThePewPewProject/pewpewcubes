package de.pewpewproject.pewpewcubes.core.exceptions

/**
 * Exception type for when there is something wrong with the lasertag configuration
 * @author Étienne Muser
 */
class ConfigurationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)