package org.theberdakh

object Config {
    val BOT_TOKEN = System.getenv("BOT_TOKEN") ?: "6585317294:AAHUwUPGf9UwNacAm_FD9_zed2HiT-sygjk"
    val WEBHOOK_URL = System.getenv("WEBHOOK_URL") ?: "https://bot-two-ruddy.vercel.app/"
    val PORT = System.getenv("PORT")?.toIntOrNull() ?: 8080
}