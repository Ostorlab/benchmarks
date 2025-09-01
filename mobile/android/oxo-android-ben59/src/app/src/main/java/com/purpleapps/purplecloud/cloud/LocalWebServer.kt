package com.purpleapps.purplecloud.cloud

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.purpleapps.purplecloud.persistance.DirectoryManager
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File
import java.net.Inet4Address

/**
 * A local web server for "Wi-Fi Sharing".
 *
 * VULNERABILITY: This server is vulnerable to path traversal. It does not sanitize the
 * incoming path from the URL. An attacker on the same local network can craft a URL
 * with `../` sequences to access files outside the user's intended sharing directory,
 * potentially reading sensitive data from the app's private storage.
 */
class LocalWebServer(private val context: Context) {

    var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    private val port = 8080
    private var verificationCode: String? = null

    fun start(): Pair<String?, String?> {
        if (server != null) {
            return getUrl() to verificationCode
        }

        verificationCode = (100000..999999).random().toString()
        Log.i("LocalWebServer", "Generated verification code: $verificationCode")

        val userRootDir = DirectoryManager.getRoot(context)

        try {
            server = embeddedServer(Netty, port = port, host = "127.0.0.1") {
                routing {
                    get("/file") {
                        val providedCode = call.request.queryParameters["code"]
                        if (providedCode != verificationCode) {
                            call.respondText(
                                "Unauthorized. Invalid or missing verification code.",
                                status = io.ktor.http.HttpStatusCode.Unauthorized
                            )
                            return@get
                        }

                        val requestedPath = call.request.queryParameters["path"]
                        if (requestedPath == null) {
                            call.respondText(
                                "Bad request.",
                                status = io.ktor.http.HttpStatusCode.BadRequest
                            )
                            return@get
                        }

                        val file = File(userRootDir, requestedPath)

                        if (file.exists() && file.isFile) {
                            call.respondFile(file)
                        } else {
                            call.respondText(
                                "File not found or is a directory.",
                                status = io.ktor.http.HttpStatusCode.NotFound
                            )
                        }
                    }
                    get("/") {
                        call.respondText("PurpleCloud Wi-Fi Sharing is active.")
                    }
                }
            }.start(wait = false)

        } catch (e: Exception) {
            return "Error starting server." to null
        }

        return getUrl() to verificationCode
    }

    fun stop() {
        if (server != null) {
            server?.stop(1000, 2000)
            server = null
            verificationCode = null
            Log.i("LocalWebServer", "Server stopped.")
        }
    }

    private fun getUrl(): String? {
        return getLocalIpAddress()?.let { "http://$it:$port" }
    }

    @Suppress("DEPRECATION")
    private fun getLocalIpAddress(): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        if (ipAddress == 0) return "Device not connected to Wi-Fi"
        val ipBytes = byteArrayOf(
            (ipAddress and 0xff).toByte(),
            (ipAddress shr 8 and 0xff).toByte(),
            (ipAddress shr 16 and 0xff).toByte(),
            (ipAddress shr 24 and 0xff).toByte()
        )
        return try {
            Inet4Address.getByAddress(ipBytes).hostAddress
        } catch (e: Exception) {
            Log.e("LocalWebServer", "Error getting IP", e)
            "Error getting IP"
        }
    }
}
