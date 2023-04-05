package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import taboolib.module.database.Host
import taboolib.module.database.SQL
import taboolib.module.database.Table

interface SQLTable {

    val table: Table<Host<SQL>, SQL>


    fun createTable() {
        table.createTable(DatabaseManager.dataSource)
    }

}