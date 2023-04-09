package me.xiaozhangup.pigeonbox.type

import me.xiaozhangup.pigeonbox.PigeonBox
import java.util.*

data class Mail(val uuid: UUID, val from: UUID, val to: UUID, val kits: List<String>) {
    fun kitsJson(): String {
        return PigeonBox.gson.toJson(this.kits)
    }
}
