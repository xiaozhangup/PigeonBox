package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import taboolib.module.database.*
import java.util.*

class TableUser : SQLTable {

    override val table: Table<Host<SQL>, SQL> = Table("user", DatabaseManager.host) {
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("name") {
            type(ColumnTypeSQL.TEXT)
        }

        add("data") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    fun getNameByUUID(uuid: UUID): String? {
        return getNameByUUID(uuid.toString())
    }

    fun getNameByUUID(uuid: String): String? {
        return table.select(dataSource) {
            where("uuid" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("name")
        }
    }

    fun getUUIDByName(name: String): String? {
        return table.select(dataSource) {
            where("name" eq name)
            limit(1)
        }.firstOrNull {
            getString("uuid")
        }
    }


}