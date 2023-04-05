package me.xiaozhangup.pigeonbox

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object PigeonBox : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}