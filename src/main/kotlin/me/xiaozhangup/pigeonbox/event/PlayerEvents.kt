package me.xiaozhangup.pigeonbox.event

import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.sendToast
import taboolib.module.nms.type.ToastBackground
import taboolib.module.nms.type.ToastFrame

object PlayerEvents {

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        e.player.sendToast(Material.ACACIA_BOAT, e.player.name, ToastFrame.TASK, ToastBackground.ADVENTURE)
    }

}