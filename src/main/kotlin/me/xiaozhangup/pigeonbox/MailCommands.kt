package me.xiaozhangup.pigeonbox

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.type.Mail
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.Sound
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
import taboolib.module.ui.type.Linked
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
    private val next: ItemStack = buildItem(Material.ARROW) {
        name = "&f下一页"
        colored()
    }
    private val pre: ItemStack = buildItem(Material.ARROW) {
        name = "&f上一页"
        colored()
    }
    private val nonext: ItemStack = buildItem(Material.FEATHER) {
        name = "&f没有下一页"
        colored()
    }
    private val nopre: ItemStack = buildItem(Material.FEATHER) {
        name = "&f没有上一页"
        colored()
    }

    @Awake(LifeCycle.ENABLE)
    fun regCommand() {
        command("mail", permissionDefault = PermissionDefault.TRUE) {
            literal("all") {
                execute<Player> { sender, _, _ ->
                    sender.openAll()
                }
            }
            literal("unreceive") {
                execute<Player> { sender, _, _ ->
                    sender.openUnread()
                }
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
                                sender.playSound(sender.location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f)
                                sender.send(playername, uuid)
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

    private fun Player.send(playername: String, to: String) {
        openMenu<Stored>("请给邮件放入物品") {
            rows(3)
            handLocked(false)

            map(
                "         ",
                "         ",
                "--c---x--"
            )

            set('-', background)
            set('x', buildItem(Material.WRITABLE_BOOK) {
                name = "&f将发送邮件给: $playername"
                lore += "&7将你的物品放入"
                lore += "&7然后关闭菜单"
                lore += "&7即可将邮件发送"
                colored()
            })
            set('c', buildItem(Material.BOOK) {
                name = "&c如何取消邮件发送?"
                lore += "&7将菜单留空并"
                lore += "&7直接关闭菜单"
                lore += "&7即可取消本次发送"
                colored()
            })

            onClick { event ->
                if (event.rawSlot in 18..26) {
                    event.isCancelled = true
                }
            }

            onClose { event ->
                submitAsync {
                    val items = mutableListOf<String>()
                    for (slot in 0..17) {
                        event.inventory.getItem(slot)?.toBase64()
                            ?.let { it1 -> items.add(it1) }
                    }

                    if (items.size < 1) {
                        send("邮件发送已取消!".red())
                    } else {
                        send("正在发送 ${items.size} 个物品给${playername}...".red())
                        val mail = MailManager().asNote(uniqueId, UUID.fromString(to), items)
                        DatabaseManager.tableMail.add(mail)
                        send("发送完成!")
                    }
                }
            }
        }
    }

    private fun Player.openAll(mes: Boolean = true) {
        playSound(location, Sound.ITEM_BUNDLE_INSERT, 1f, 1f)
        if (mes) send("正在从数据库加载你的邮件...")
        submitAsync {
            val kits = DatabaseManager.tableMail.getByTo(uniqueId.toString())
            openMenu<Linked<String>>("你的邮箱") {
                rows(3)
                virtualize()

                map(
                    "         ",
                    "         ",
                    "-d----pn-"
                )

                slots(mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17).toList())

                elements {
                    kits
                }

                onGenerate(true) { _, element, _, _ ->
                    buildItem(Material.CHEST_MINECART) {
                        val sender = DatabaseManager.tableMail.getFromByID(element)?.let {
                            PigeonBox.name.computeIfAbsent(it) { it1 ->
                                DatabaseManager.tableUser.getNameByUUID(it1)
                            }
                        }
                        name = if (sender.isNullOrEmpty()) {
                            "&f莫名其妙的邮件"
                        } else {
                            "&f${sender}给你的邮件"
                        }
                        colored()
                    }
                }

                setNextPage(25) { _, hasNextPage ->
                    if (hasNextPage) {
                        next
                    } else {
                        nonext
                    }
                }
                setPreviousPage(24) { _, hasNextPage ->
                    if (hasNextPage) {
                        pre
                    } else {
                        nopre
                    }
                }

                onClick { _, element ->
                    submitAsync {
                        val mail = DatabaseManager.tableMail.getMail(element)
                        val sender = PigeonBox.name.computeIfAbsent(mail?.from.toString()) { it1 ->
                            DatabaseManager.tableUser.getNameByUUID(it1)
                        }
                        mail?.let { openMail(it, "${sender}给你的邮件") }
                    }
                }

                set('-', background)
                set('d', buildItem(Material.BOOK) {
                    name = "&f查看你发送的未被领取的邮件"
                    lore += ""
                    lore += "&e单击进入"
                    colored()
                }) {
                    openUnread(false)
                }
            }
        }
    }

    private fun Player.openMail(mail: Mail, title: String) {
        playSound(location, Sound.ITEM_BUNDLE_INSERT, 1f, 1f)
        openMenu<Linked<String>>(title) {
            rows(3)
            virtualize()

            map(
                "         ",
                "         ",
                "-d-----p-"
            )

            slots(mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17).toList())

            elements {
                mail.kits
            }

            onGenerate(true) { _, element, _, _ ->
                element.toItemStack()
            }

            set('-', background)
            set('d', buildItem(Material.BOOK) {
                name = "&f返回你的邮箱"
                lore += ""
                lore += "&e单击进入"
                colored()
            }) {
                openAll(false)
            }
            var slot = 0
            for (item in this@openMail.inventory) {
                if (item.isAir) slot++
            }
            slot -= 5
            if (slot >= mail.kits.size) {
                val uuid = mail.uuid.toString()
                set('p', buildItem(Material.HOPPER) {
                    name = "&f领取此邮件"
                    lore += ""
                    lore += "&e单击领取"
                    colored()
                }) {
                    submitAsync {
                        val push = DatabaseManager.tableMail.getMail(uuid)
                        if (push != null && push.to == uniqueId) {
                            DatabaseManager.tableMail.delete(uuid)
                            submit {
                                for (item in mail.kits) {
                                    this@openMail.inventory.addItem(item.toItemStack())
                                }
                                openAll(false)
                            }
                        } else {
                            send("这个邮件似乎并不存在".red())
                            openAll(false)
                        }
                    }
                }
            } else {
                set('p', buildItem(Material.REDSTONE) {
                    name = "&c没有足够空间领取"
                    lore += ""
                    lore += "&7领取需要: &f${mail.kits.size}格"
                    lore += "&7但你只有: &f${slot}格"
                    colored()
                })
            }

        }
    }

    private fun Player.openDelete(mail: Mail, title: String) {
        playSound(location, Sound.ITEM_BUNDLE_INSERT, 1f, 1f)
        openMenu<Linked<String>>(title) {
            rows(3)
            virtualize()

            map(
                "         ",
                "         ",
                "-d-----r-"
            )

            slots(mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17).toList())

            elements {
                mail.kits
            }

            onGenerate(true) { _, element, _, _ ->
                element.toItemStack()
            }

            set('-', background)
            set('d', buildItem(Material.BOOK) {
                name = "&f返回你发送的未被领取的邮件"
                lore += ""
                lore += "&e单击进入"
                colored()
            }) {
                openUnread(false)
            }
            set('r', buildItem(Material.REDSTONE) {
                name = "&c撤销发送这个邮件"
                lore += ""
                lore += "&f物品将返还到你的邮箱"
                lore += "&c单击撤销此邮件"
                colored()
            }) {
                submitAsync {
                    DatabaseManager.tableMail.resend(mail.uuid.toString(), uniqueId.toString())
                    openAll(false)
                }
            }


        }
    }

    private fun Player.openUnread(mes: Boolean = true) {
        playSound(location, Sound.ITEM_BUNDLE_INSERT, 1f, 1f)
        if (mes) send("正在从数据库加载你的邮件...")
        submitAsync {
            val sended = DatabaseManager.tableMail.getByFrom(uniqueId.toString())
            openMenu<Linked<String>>("你发送的未被领取的邮件") {
                rows(3)
                virtualize()

                map(
                    "         ",
                    "         ",
                    "-d----pn-"
                )

                slots(mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17).toList())

                elements {
                    sended
                }

                onGenerate(true) { _, element, _, _ ->
                    buildItem(Material.CHEST_MINECART) {
                        val sender = DatabaseManager.tableMail.getToByID(element)?.let {
                            PigeonBox.name.computeIfAbsent(it) { it1 ->
                                DatabaseManager.tableUser.getNameByUUID(it1)
                            }
                        }
                        name = if (sender.isNullOrEmpty()) {
                            "&f莫名其妙的邮件"
                        } else {
                            "&f你发给${sender}的邮件"
                        }
                        colored()
                    }
                }

                set('-', background)
                set('d', buildItem(Material.BOOK) {
                    name = "&f查看你的邮箱"
                    lore += ""
                    lore += "&e单击进入"
                    colored()
                }) {
                    openAll(false)
                }

                onClick { _, element ->
                    submitAsync {
                        val mail = DatabaseManager.tableMail.getMail(element)
                        val sender = PigeonBox.name.computeIfAbsent(mail?.to.toString()) { it1 ->
                            DatabaseManager.tableUser.getNameByUUID(it1)
                        }
                        mail?.let { openDelete(it, "你给${sender}的邮件") }
                    }
                }

                setNextPage(25) { _, hasNextPage ->
                    if (hasNextPage) {
                        next
                    } else {
                        nonext
                    }
                }
                setPreviousPage(24) { _, hasNextPage ->
                    if (hasNextPage) {
                        pre
                    } else {
                        nopre
                    }
                }
            }
        }
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