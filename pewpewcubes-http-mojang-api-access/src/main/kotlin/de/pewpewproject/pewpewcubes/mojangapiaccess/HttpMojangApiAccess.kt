package de.pewpewproject.pewpewcubes.mojangapiaccess

import com.google.gson.Gson
import de.pewpewproject.pewpewcubes.mojangapiaccess.dtos.PlayerInfoDto
import de.pewpewproject.pewpewcubes.mojangapiaccess.dtos.PlayerSessionProfileDto
import de.pewpewproject.pewpewcubes.mojangapiaccess.dtos.ProfileTextureDto
import de.pewpewproject.pewpewcubes.ports.mojangapiaccess.IMojangApiAccess
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Constant for the mojang api base url
 */
const val MOJANGAPI_BASE_URL = "https://api.mojang.com"

/**
 * Constant for the mojang sessionserver base url
 */
const val MOJANGSESSIONSERVER_BASE_URL = "https://sessionserver.mojang.com"

/**
 * Constant for the absolute path to the minecraft player info resource collection
 */
const val PLAYERINFO_PATH = "/users/profiles/minecraft"

/**
 * Constant for the absolute path to the minecraft player session resource collection
 */
const val PLAYERSESSIONPROFILE_PATH = "/session/minecraft/profile"

/**
 * Class implementing the mojang api access interface using the java http client
 *
 * @author Étienne Muser
 */
class HttpMojangApiAccess : IMojangApiAccess {

    override fun getSkinUrl(playerName: String): String? {

        // Get the player info
        val playerInfo = getPlayerInfo(playerName) ?: return null

        // Get the player session profile
        val playerSessionProfile = getPlayerSessionProfile(playerInfo.id) ?: return null

        // Get the texture object from the profile
        val playerTexture = getProfileTextureFromPlayerSessionProfile(playerSessionProfile)

        // Get the skin url
        val skinUrl = playerTexture.textures["SKIN"]?.url

        return skinUrl
    }

    private fun getPlayerInfo(playerName: String): PlayerInfoDto? {
        // Build the http request
        val request = HttpRequest.newBuilder()
            .uri(URI("${MOJANGAPI_BASE_URL}${PLAYERINFO_PATH}/${playerName}"))
            .GET()
            .build()

        // Make the http call
        val response = HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // If the http call was not successful
        if (response.statusCode() != 200) {
            return null
        }

        // Get the uuid of the player
        val playerInfo = Gson()
            .fromJson(response.body(), PlayerInfoDto::class.java)

        return playerInfo
    }

    private fun getPlayerSessionProfile(playerUuid: String): PlayerSessionProfileDto? {
        // Build the http request
        val request = HttpRequest.newBuilder()
            .uri(URI("${MOJANGSESSIONSERVER_BASE_URL}${PLAYERSESSIONPROFILE_PATH}/${playerUuid}"))
            .GET()
            .build()

        // Make the http call
        val response = HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString())

        // If the http call was not successful
        if (response.statusCode() != 200) {
            return null
        }

        // Get the player session profile
        val playerSessionProfile = Gson()
            .fromJson(response.body(), PlayerSessionProfileDto::class.java)

        return playerSessionProfile
    }

    private fun getProfileTextureFromPlayerSessionProfile(playerSessionProfile: PlayerSessionProfileDto): ProfileTextureDto {
        // Get the base64 encoded texture json
        val encodedTextureJson = playerSessionProfile.properties[0].value

        // Decode
        val decodedTextureJson = String(Base64.getDecoder().decode(encodedTextureJson), StandardCharsets.UTF_8)

        // Deserialize
        val profileTextureObject = Gson()
            .fromJson(decodedTextureJson, ProfileTextureDto::class.java)

        return profileTextureObject
    }
}