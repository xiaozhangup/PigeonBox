package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.type.Mail
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.*

class MailManager {

    fun asNote(from: UUID, to: UUID, kits: List<String>): Mail {
        return Mail(UUID.randomUUID(), from, to, kits)
    }

}