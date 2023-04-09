package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.type.Mail
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SkipTo
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Stored
import taboolib.platform.util.*
import java.util.*

@SkipTo(LifeCycle.ENABLE)
object MailCommands {

    private val prefix: Component =
        MiniMessage.miniMessage().deserialize("<dark_gray>[<color:#eebebe>邮箱</color>]</dark_gray> ")
    private val mm: MiniMessage = MiniMessage.miniMessage()
    private val background: ItemStack = buildItem(Material.BLACK_STAINED_GLASS_PANE) {
        name = " "
    }

    @Awake(LifeCycle.ENABLE)
    fun regCommand() {
        command("mail", permissionDefault = PermissionDefault.TRUE) {
            literal("all") {

            }
            literal("unreceive") {

            }
            literal("pick", hidden = true) {

            }
            literal("delete", hidden = true) {

            }
            literal("help") {
                execute<Player> { sender, _, _ ->
                    submitAsync {
                        sender.sendMessage(mm.deserialize("<br><br><br>"))
                        sender.sendMessage(mm.deserialize(" <color:#eebebe><b>邮件功能使用方法:</b></color><newline>"))
                        sender.sendMessage(mm.deserialize("  <white>/mail<white> <dark_gray>-</dark_gray> <gray>开始交互式发送邮件</gray>"))
                        sender.sendMessage(mm.deserialize("  <white>/mail unreceive<white> <dark_gray>-</dark_gray> <gray>查看你发出的未被领取的邮件</gray>"))
                        sender.sendMessage(mm.deserialize("  <white>/mail all<white> <dark_gray>-</dark_gray> <gray>查看你的未领取邮件</gray>"))
                        sender.sendMessage(mm.deserialize("<br>"))
                    }
                }
            }

            execute<Player> { sender, _, _ ->
                submitAsync {
                    sender.cancelNextChat()
                    sender.send(softColor("请用聊天输入你要发送给的玩家ID!"))
                    sender.nextChat {
                        val playername = it
                        val uuid = DatabaseManager.tableUser.getUUIDByName(playername)
                        if (playername == sender.name) {
                            sender.send(softColor("你不能给自己发送邮件!"))
                        } else if (uuid.isNullOrEmpty()) {
                            sender.send(softColor("没有找到这个玩家,他加入过游戏吗?"))
                        } else {
                            sender.send("请给发送给${playername}的邮件放入物品")
                            submit {
                                sender.openMenu<Stored>("请给邮件放入物品") {
                                    rows(4)

                                    map(
                                        "--------x",
                                        "         ",
                                        "         ",
                                        "---------"
                                    )

                                    set('-', background)
                                    set('x', buildItem(Material.WRITABLE_BOOK) {
                                        name = "&f关闭菜单即可发送邮件"
                                        lore += ""
                                        lore += "&c留空并关闭则可取消发送"
                                        colored()
                                    })

                                    onClick { event ->
                                        if (event.rawSlot in 0..8) {
                                            event.isCancelled = true
                                        }
                                        else if (event.rawSlot in 27..35) {
                                            event.isCancelled = true
                                        }
                                    }

                                    onClose { event ->
                                        submitAsync {
                                            val items = mutableListOf<String>()
                                            for (slot in 9..26) {
                                                event.inventory.getItem(slot)?.toBase64()
                                                    ?.let { it1 -> items.add(it1) }
                                            }

                                            if (items.size < 1) {
                                                sender.send("邮件发送已取消!".red())
                                            } else {
                                                sender.send("正在发送 ${items.size} 个物品给${playername}...".red())
                                                val mail = MailManager().asNote(sender.uniqueId, UUID.fromString(uuid), items)
                                                DatabaseManager.tableMail.add(mail)
                                                sender.send("发送完成!")
                                            }
                                        }
                                    }
                                }
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
        return "<color:#f9e9e9>${text}</color>"
    }

    private fun String.red(): String {
        return "<color:#e78284>${this}</color>"
    }

    private fun ItemStack.toBase64(): String {
        return Base64.getEncoder().encodeToString(serializeToByteArray())
    }

    private fun String.toItemStack(): ItemStack {
        return Base64.getDecoder().decode(this).deserializeToItemStack()
    }

}