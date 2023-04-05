package me.xiaozhangup.pigeonbox.type

import me.xiaozhangup.pigeonbox.PigeonBox
import java.util.UUID

data class Note(val uuid: UUID, val from: UUID, val to: UUID, val message: String) {
    fun asJson(): String {
        return PigeonBox.gson.toJson(this)
    }

}