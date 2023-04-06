package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import me.xiaozhangup.pigeonbox.type.Note
import taboolib.module.database.*

class TableNote : SQLTable {

    override val table: Table<Host<SQL>, SQL> = Table("mail_note", DatabaseManager.host) {
        add("id") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("from") {
            type(ColumnTypeSQL.VARCHAR, 36)
        }

        add("to") {
            type(ColumnTypeSQL.VARCHAR, 36)
        }

        add("note") {
            type(ColumnTypeSQL.TEXT)
        }
    }


    fun getByFrom(uuid: String): List<List<String>> {
        val notes = mutableListOf<List<String>>()
        table.select(dataSource) {
            where("from" eq uuid)
            limit(8)
        }.forEach {
            notes.add(listOf(getString("id"), getString("to"), getString("note")))
        }

        return notes
    }

    fun getByTo(uuid: String): List<List<String>> {
        val notes = mutableListOf<List<String>>()
        table.select(dataSource) {
            where("to" eq uuid)
            limit(8)
        }.forEach {
            notes.add(listOf(getString("id"), getString("from"), getString("note")))
        }

        return notes
    }

    fun add(note: Note) {
        table.insert(dataSource, "id", "from", "to", "note") {
            value(
                note.uuid.toString(),
                note.from.toString(),
                note.to.toString(),
                note.message
            )
        }
    }

    fun delete(uuid: String) {
        table.delete(dataSource) {
            where("id" eq uuid)
        }
    }

    fun canDelete(uuid: String, to: String): Boolean? {
        return table.select(dataSource) {
            where("id" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("to").equals(to)
        }
    }

    fun canUnsend(uuid: String, from: String): Boolean? {
        return table.select(dataSource) {
            where("id" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("from").equals(from)
        }
    }

}