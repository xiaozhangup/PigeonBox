package me.xiaozhangup.pigeonbox

import com.google.gson.Gson
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin

@RuntimeDependencies(
    RuntimeDependency(value = "mysql:mysql-connector-java:8.0.30")
)
object PigeonBox : Plugin() {

    @JvmStatic
    val plugin: BukkitPlugin by lazy { BukkitPlugin.getInstance() }

    @JvmStatic
    val gson: Gson = Gson()

    @JvmStatic
    @Config
    lateinit var config: Configuration
        private set

    override fun onEnable() {
    }
}