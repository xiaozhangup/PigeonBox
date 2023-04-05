package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import taboolib.module.database.*
import java.util.*

class TableUser : SQLTable {

    override val table: Table<Host<SQL>, SQL> = Table("mail_user", DatabaseManager.host) {
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

    operator fun get(name: String): String? {
        return table.select(dataSource) {
            rows("data")
            where("name" eq name)
            limit(1)
        }.firstOrNull {
            getString("data")
        }
    }

    operator fun get(uuid: UUID): String? {
        return table.select(dataSource) {
            rows("data")
            where("uuid" eq uuid.toString())
            limit(1)
        }.firstOrNull {
            getString("data")
        }
    }

    operator fun get(uuid: String, name: String): String? {
        return table.select(dataSource) {
            rows("data")
            where(("uuid" eq uuid) and ("name" eq name))
            limit(1)
        }.firstOrNull {
            getString("data")
        }
    }

    operator fun get(uuid: UUID, name: String): String? =
        get(uuid.toString(), name)

    operator fun set(uuid: String, name: String, data: String) {
        if (get(uuid, name) == null) {
            table.insert(dataSource, "uuid", "name", "data") {
                value(uuid, name, data)
            }
        } else {
            table.update(dataSource) {
                set("data", data)
                where(("uuid" eq uuid) and ("name" eq name))
            }
        }
    }

    operator fun set(uuid: UUID, name: String, data: String) {
        set(uuid.toString(), name, data)
    }

    operator fun set(uuid: UUID, data: String) {
        table.update(dataSource) {
            set("data", data)
            where("uuid" eq uuid.toString())
        }
    }

    operator fun set(name: String, data: String) {
        table.update(dataSource) {
            set("data", data)
            where("name" eq name)
        }
    }

}