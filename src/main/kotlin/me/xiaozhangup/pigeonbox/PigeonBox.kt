package me.xiaozhangup.pigeonbox

import com.google.gson.Gson
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin

@RuntimeDependencies(
    RuntimeDependency(value = "mysql:mysql-connector-java:8.0.30")
)
object PigeonBox : Plugin() {

    val plugin: BukkitPlugin by lazy { BukkitPlugin.getInstance() }

    val gson: Gson = Gson()

    @Config
    lateinit var config: Configuration
        private set

    override fun onEnable() {
    }
}