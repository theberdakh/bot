package org.theberdakh

import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand

const val TOKEN = "6585317294:AAG9DyVCJhdQlwK2qrNb3d2vaLbt1pRdiq4"
suspend fun main() {
    val bot = telegramBot(TOKEN)
    bot.buildBehaviourWithLongPolling {
        println(getMe())

        onCommand("start") {
            reply(it, "Hi:)")
        }
    }.join()
}