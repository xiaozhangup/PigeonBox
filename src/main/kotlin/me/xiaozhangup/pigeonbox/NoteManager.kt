package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.type.Note
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.*

class NoteManager {

    private val name: WeakHashMap<String, String> = WeakHashMap<String, String>()

    fun getAll(player: Player): Book {
        val book = Book.builder()
        book.author(Component.empty())
        book.title(Component.empty())

        for (note in DatabaseManager.tableNote.getByTo(player.uniqueId.toString())) {
            val sender = name.computeIfAbsent(note[1]) {
                DatabaseManager.tableUser.getNameByUUID(note[1])
            }

            if (sender.isNullOrEmpty()) continue
            val uid = note[0]
            val readed = MiniMessage.miniMessage()
                .deserialize("<click:run_command:'/note check ${uid}'><color:#946249><b><u>标为已读</u></b></color></click>")
            book.addPage(Component.text(sender + "的留言:\n").append(readed).append(Component.text("\n\n" + note[2])))
        }

        return book.build()
    }

    fun getUnread(player: Player): Book {
        val book = Book.builder()
        book.author(Component.empty())
        book.title(Component.empty())

        for (note in DatabaseManager.tableNote.getByFrom(player.uniqueId.toString())) {
            val sender = name.computeIfAbsent(note[1]) {
                DatabaseManager.tableUser.getNameByUUID(note[1])
            }
            if (sender.isNullOrEmpty()) continue
            val uid = note[0]
            val readed = MiniMessage.miniMessage()
                .deserialize("<click:run_command:'/note delete ${uid}'><color:#d9272a><b><u>撤回留言</u></b></color></click>")
            book.addPage(Component.text("给" + sender + "的留言:\n").append(readed).append(Component.text("\n\n" + note[2])))
        }

        return book.build()
    }

    fun asNote(from: UUID, to: UUID, message: String): Note {
        return Note(UUID.randomUUID(), from, to, message)
    }

}