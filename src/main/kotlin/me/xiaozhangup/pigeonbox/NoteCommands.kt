package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SkipTo
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.cancelNextChat
import taboolib.platform.util.nextChat
import java.util.*

@SkipTo(LifeCycle.ENABLE)
object NoteCommands {

    private val prefix: Component =
        MiniMessage.miniMessage().deserialize("<dark_gray>[<color:#ef9f76>留言</color>]</dark_gray> ")
    private val mm: MiniMessage = MiniMessage.miniMessage()

    @Awake(LifeCycle.ENABLE)
    fun regCommand() {
        command("note", permissionDefault = PermissionDefault.TRUE) {
            literal("all") {
                execute<Player> { sender, _, _ ->
                    submitAsync {
                        sender.send(softColor("正在从数据库加载你的留言..."))
                        val book = NoteManager().getAll(sender)
                        if (book.pages().size < 1) {
                            sender.send("<color:#e78284>你没有还未读的留言</color>")
                        } else {
                            sender.openBook(book)
                        }
                    }
                }
            }
            literal("unread") {
                execute<Player> { sender, _, _ ->
                    submitAsync {
                        sender.send(softColor("正在从数据库加载你的留言..."))
                        val book = NoteManager().getUnread(sender)
                        if (book.pages().size < 1) {
                            sender.send("<color:#e78284>你没有还未被读的留言</color>")
                        } else {
                            sender.openBook(book)
                        }
                    }
                }
            }
            literal("check", hidden = true) {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        submitAsync {
                            DatabaseManager.tableNote.canDelete(argument, sender.uniqueId.toString())?. let {
                                if (it) {
                                    DatabaseManager.tableNote.delete(argument)
                                    val book = NoteManager().getAll(sender)
                                    if (book.pages().size < 1) {
                                        sender.send("<color:#e78284>所有留言阅读完毕!</color>")
                                    } else {
                                        sender.openBook(book)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            literal("delete", hidden = true) {
                dynamic {
                    execute<Player> { sender, _, argument ->
                        submitAsync {
                            DatabaseManager.tableNote.canUnsend(argument, sender.uniqueId.toString())?. let {
                                if (it) {
                                    DatabaseManager.tableNote.delete(argument)
                                    val book = NoteManager().getUnread(sender) // TODO: Change
                                    if (book.pages().size < 1) {
                                        sender.send("<color:#e78284>你没有任何未被读留言!</color>")
                                    } else {
                                        sender.openBook(book)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            literal("help") {
                execute<Player> { sender, context, argument ->
                    submitAsync {
                        sender.sendMessage(mm.deserialize("<br><br><br>"))
                        sender.sendMessage(mm.deserialize(" <color:#ef9f76><b>留言功能使用方法:</b></color><newline>"))
                        sender.sendMessage(mm.deserialize("  <white>/note<white> <dark_gray>-</dark_gray> <gray>开始交互式留言</gray>"))
                        sender.sendMessage(mm.deserialize("  <white>/note unread<white> <dark_gray>-</dark_gray> <gray>查看你发出的未被读留言</gray>"))
                        sender.sendMessage(mm.deserialize("  <white>/note all<white> <dark_gray>-</dark_gray> <gray>查看你的未读留言</gray>"))
                        sender.sendMessage(mm.deserialize("<br>"))
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