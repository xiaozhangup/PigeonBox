package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.cancelNextChat
import taboolib.platform.util.nextChat
import java.util.*

@Awake(LifeCycle.ENABLE)
object Commands {

    private val prefix: Component =
        MiniMessage.miniMessage().deserialize("<dark_gray>[<color:#ef9f76>留言</color>]</dark_gray> ")

    @Awake(LifeCycle.ENABLE)
    fun regCommand() {
        command("note", permissionDefault = PermissionDefault.TRUE) {
            literal("all") {
                execute<Player> { sender, _, _ ->
                    submitAsync {
                        sender.send(softColor("正在从数据库加载你的留言..."))
                        val book = NoteManager().getAll(sender)
                        if (book.pages().size < 1) {
                            sender.send("<color:#e78284>你没有未读的留言</color>")
                        } else {
                            sender.openBook(book)
                        }
                    }
                }
            }
            literal("check") {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        submitAsync {
                            DatabaseManager.tableNote.delete(argument)
                            val book = NoteManager().getAll(sender)
                            if (book.pages().size < 1) {
                                sender.send("<color:#e78284>所有留言阅读完毕!</color>")
                                sender.closeInventory()
                            } else {
                                sender.openBook(book)
                            }
                        }
                    }
                }
            }

            execute<Player> { sender, _, _ ->
                submitAsync {
                    sender.cancelNextChat()
                    sender.send(softColor("请用聊天输入你要留言给的玩家ID!"))
                    sender.nextChat {
                        val playername = it
                        val uuid = DatabaseManager.tableUser.getUUIDByName(playername)
                        if (playername == sender.name) {
                            sender.send(softColor("你不能给自己留言!"))
                        } else if (uuid.isNullOrEmpty()) {
                            sender.send(softColor("没有找到这个玩家,他加入过游戏吗?"))
                        } else {
                            sender.send(softColor("你将留言给 $playername ,请再用聊天输入留言内容!"))
                            sender.cancelNextChat()
                            sender.nextChat { message: String ->
                                val note = NoteManager().asNote(sender.uniqueId, UUID.fromString(uuid), message)
                                //sender.send(softColor(NoteManager().asNote(sender.uniqueId, UUID.fromString(uuid), message).asJson()))
                                DatabaseManager.tableNote.add(note)
                                sender.send(softColor("给 $playername 留言成功!"))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Player.send(message: String) {
        sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(message)))
    }

    private fun softColor(text: String): String {
        return "<color:#fbeae1>${text}</color>"
    }

}