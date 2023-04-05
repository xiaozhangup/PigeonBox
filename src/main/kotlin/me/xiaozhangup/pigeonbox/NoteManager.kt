package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.type.Note
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.UUID

class NoteManager {

    fun getAll(player: Player): Book {
        val book = Book.builder()
        book.author(Component.empty())
        book.title(Component.empty())

        for (note in DatabaseManager.tableNote.getByTo(player.uniqueId.toString())) {
            val sender = DatabaseManager.tableUser.getNameByUUID(note[1])
            if (sender.isNullOrEmpty()) continue
            val uid = note[0]
            val readed = MiniMessage.miniMessage().deserialize("<click:run_command:'/note check ${uid}'><color:#946249><b>标记为已读</b></color></click>")
            book.addPage(Component.text(sender + "的留言:\n").append(readed).append(Component.text("\n\n" + note[2])))
        }

        return book.build()
    }

    fun readed(uuid: UUID) {

    }

    fun asNote(from: UUID, to: UUID, message: String): Note {
        return Note(UUID.randomUUID(), from, to, message)
    }

}