package org.theberdakh

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.api.webhook.setWebhookInfo
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.setWebhookInfoAndStartListenWebhooks
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.serialization.json.Json

class TelegramBotHandler {
    private val bot = telegramBot(Config.BOT_TOKEN)

    suspend fun setupWebhook() {
        try {
            bot.setWebhookInfo(Config.WEBHOOK_URL)
            println("Webhook set successfully to: ${Config.WEBHOOK_URL}")
        } catch (e: Exception) {
            println("Failed to set webhook: ${e.message}")
        }
    }

    private suspend fun handleUpdate(update: Update) {
        try {
            when (update) {
                is MessageUpdate -> handleMessage(update)
                else -> println("Unhandled update type: ${update::class.simpleName}")
            }
        } catch (e: Exception) {
            println("Error handling update: ${e.message}")
        }
    }

    private suspend fun handleMessage(messageUpdate: MessageUpdate) {
        val message = messageUpdate.data
        if (message is CommonMessage<*> && message.content is TextContent) {
            val text = (message.content as TextContent).text
            val chatId = message.chat.id

            when {
                text.startsWith("/start") -> {
                    bot.sendTextMessage(
                        chatId = chatId,
                        text = "Hi! 👋 Welcome to the bot!\n\nAvailable commands:\n• /start - Show this message\n• /help - Get help\n• /ping - Check if bot is alive"
                    )
                }
                text.startsWith("/help") -> {
                    bot.sendTextMessage(
                        chatId = chatId,
                        text = "🤖 Bot Help\n\nThis is a simple Telegram bot deployed on Vercel.\n\nCommands:\n• /start - Welcome message\n• /help - Show this help\n• /ping - Ping the bot\n\nJust send me any message and I'll echo it back!"
                    )
                }
                text.startsWith("/ping") -> {
                    bot.sendTextMessage(
                        chatId = chatId,
                        text = "🏓 Pong! Bot is alive and running on Vercel!"
                    )
                }
                else -> {
                    bot.sendTextMessage(
                        chatId = chatId,
                        text = "Echo: $text\n\nTry /help for available commands!"
                    )
                }
            }
        }
    }

    suspend fun processWebhookUpdate(updateJson: String): Boolean {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val update = json.decodeFromString<Update>(updateJson)
            handleUpdate(update)
            true
        } catch (e: Exception) {
            println("Error processing webhook update: ${e.message}")
            false
        }
    }
}