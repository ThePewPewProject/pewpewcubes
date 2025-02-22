package de.pewpewproject.pewpewcubes.mojangapiaccess

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HttpMojangApiAccessTest {

    @Test
    fun testGetSkinUrlFibs() {
        // Create the test object
        val testee = HttpMojangApiAccess()

        // Call the get skin url
        val skinUrl = testee.getSkinUrl("fibs__")

        assertEquals(
            "http://textures.minecraft.net/texture/90e75cd429ba6331cd210b9bd19399527ee3bab467b5a9f61cb8a27b177f6789",
            skinUrl
        )
    }

    @Test
    fun testGetSkinUrlFbs() {
        // Create the test object
        val testee = HttpMojangApiAccess()

        // Call the get skin url
        val skinUrl = testee.getSkinUrl("Der_fbs")

        assertEquals(
            "http://textures.minecraft.net/texture/1927d9865b235baf4e5cdc3717a4cd7528fa68882492df7c4134300cfb81717f",
            skinUrl
        )
    }

    @Test
    fun testGetSkinUrlInvalidUrl() {
        // Create the test object
        val testee = HttpMojangApiAccess()

        // Call the get skin url
        val skinUrl = testee.getSkinUrl("")

        assertEquals(null, skinUrl)
    }

    @Test
    fun testGetSkinUrlTooLong() {
        // Create the test object
        val testee = HttpMojangApiAccess()

        // Call the get skin url
        val skinUrl = testee.getSkinUrl("jslishiserjfklsefjlsjfsjfefjlsejfiejflsjfilesnflesf")

        assertEquals(null, skinUrl)
    }

    @Test
    fun testGetSkinUrlNonexistent() {
        // Create the test object
        val testee = HttpMojangApiAccess()

        // Call the get skin url
        val skinUrl = testee.getSkinUrl("jslishiserjfklsefjlsjfsjf")

        assertEquals(null, skinUrl)
    }
}