package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import taboolib.module.database.*
import java.util.*

class TableMail : SQLTable {

    override val table: Table<Host<SQL>, SQL> = Table("mail_data", DatabaseManager.host) {
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("data") {
            type(ColumnTypeSQL.TEXT)
        }
    }


    operator fun get(uuid: String): String? {
        return table.select(dataSource) {
            rows("data")
            where("uuid" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("data")
        }
    }

    operator fun get(uuid: UUID): String? =
        get(uuid.toString())

    operator fun set(uuid: String, data: String) {
        if (get(uuid) == null) {
            table.insert(dataSource, "uuid", "data") {
                value(uuid, data)
            }
        } else {
            table.update(dataSource) {
                set("data", data)
                where("uuid" eq uuid)
            }
        }
    }

    operator fun set(uuid: UUID, data: String) {
        set(uuid.toString(), data)
    }

    fun delete(uuid: String) {
        table.delete(dataSource) {
            where("uuid" eq uuid)
        }
    }

    fun delete(uuid: UUID) {
        delete(uuid.toString())
    }

}