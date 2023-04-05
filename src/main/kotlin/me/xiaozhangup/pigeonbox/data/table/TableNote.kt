package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import me.xiaozhangup.pigeonbox.type.Note
import taboolib.module.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("note") {
            type(ColumnTypeSQL.TEXT)
        }
    }


    fun getByFrom(uuid: String): Map<String, String> {
        val notes = mutableMapOf<String, String>()
        table.select(dataSource) {
            where("from" eq uuid)
        }.forEach {
            notes[getString("id")] = getString("note")
        }

        return notes
    }

    fun getByTo(uuid: String): List<List<String>> {
        val notes = mutableListOf<List<String>>()
        table.select(dataSource) {
            where("to" eq uuid)
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

}