package com.example.gamefest.data.remote

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import androidx.core.content.edit

/**
 * Un CookieJar qui persiste les cookies dans SharedPreferences,
 * afin que la session JWT survive aux redémarrages de l'application.
 */
class PersistentCookieJar(context: Context) : CookieJar {

    private val prefs = context.getSharedPreferences("cookies_prefs", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        prefs.edit {
            for (cookie in cookies) {
                // Sauvegarde chaque cookie sous la forme "host|name" -> valeur encodée
                val key = "${url.host}|${cookie.name}"
                putString(key, encodeCookie(cookie))
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = mutableListOf<Cookie>()
        val host = url.host
        for ((key, value) in prefs.all) {
            if (key.startsWith("$host|") && value is String) {
                val cookie = decodeCookie(value, url)
                if (cookie != null) {
                    cookies.add(cookie)
                }
            }
        }
        return cookies
    }

    fun clearAll() {
        prefs.edit { clear() }
    }

    private fun encodeCookie(cookie: Cookie): String {
        // Sérialisation simple : name=value; domain; path; secure; httpOnly; expiresAt
        return buildString {
            append(cookie.name).append("=").append(cookie.value)
            append(";domain=").append(cookie.domain)
            append(";path=").append(cookie.path)
            if (cookie.secure) append(";secure")
            if (cookie.httpOnly) append(";httpOnly")
            append(";expires=").append(cookie.expiresAt)
        }
    }

    private fun decodeCookie(encoded: String, url: HttpUrl): Cookie? {
        return try {
            val parts = encoded.split(";")
            val (name, value) = parts[0].split("=", limit = 2)
            var domain = url.host
            var path = "/"
            var secure = false
            var httpOnly = false
            var expiresAt = Long.MAX_VALUE

            for (i in 1 until parts.size) {
                val part = parts[i].trim()
                when {
                    part.startsWith("domain=") -> domain = part.removePrefix("domain=")
                    part.startsWith("path=") -> path = part.removePrefix("path=")
                    part == "secure" -> secure = true
                    part == "httpOnly" -> httpOnly = true
                    part.startsWith("expires=") -> expiresAt = part.removePrefix("expires=").toLongOrNull() ?: Long.MAX_VALUE
                }
            }

            Cookie.Builder()
                .name(name)
                .value(value)
                .domain(domain)
                .path(path)
                .apply {
                    if (secure) secure()
                    if (httpOnly) httpOnly()
                    if (expiresAt != Long.MAX_VALUE) expiresAt(expiresAt)
                }
                .build()
        } catch (_: Exception) {
            null
        }
    }
}
