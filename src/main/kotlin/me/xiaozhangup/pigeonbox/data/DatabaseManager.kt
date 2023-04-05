package me.xiaozhangup.pigeonbox.data

import me.xiaozhangup.pigeonbox.PigeonBox
import me.xiaozhangup.pigeonbox.data.table.TableMail
import me.xiaozhangup.pigeonbox.data.table.TableNote
import me.xiaozhangup.pigeonbox.data.table.TableUser
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SkipTo
import taboolib.module.database.HostSQL
import javax.sql.DataSource

@SkipTo(LifeCycle.ENABLE)
object DatabaseManager {

    lateinit var host: HostSQL
        private set
    lateinit var dataSource: DataSource
        private set

    @JvmStatic
    lateinit var tableNote: TableNote
        private set

    @JvmStatic
    lateinit var tableMail: TableMail
        private set

    @JvmStatic
    lateinit var tableUser: TableUser
        private set

    private val databaseConfig: taboolib.library.configuration.ConfigurationSection by lazy {
        PigeonBox.config.getConfigurationSection("database")
            ?: throw RuntimeException("Config 'database' does not exist.")
    }


    @Awake(LifeCycle.ENABLE)
    fun init() {
        host = HostSQL(databaseConfig)
        dataSource = host.createDataSource()

        tableNote = TableNote().apply { createTable() }
        tableUser = TableUser().apply { createTable() }
        tableMail = TableMail().apply { createTable() }
    }

}