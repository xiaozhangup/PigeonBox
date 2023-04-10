package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.PigeonBox
import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import me.xiaozhangup.pigeonbox.type.Mail
import me.xiaozhangup.pigeonbox.type.Note
import taboolib.module.database.*
import java.util.UUID

class TableMail : SQLTable {

    override val table: Table<Host<SQL>, SQL> = Table("mail_mail", DatabaseManager.host) {
        add("id") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("from") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("to") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }

        add("mail") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    fun add(mail: Mail) {
        table.insert(dataSource, "id", "from", "to", "mail") {
            value(
                mail.uuid.toString(),
                mail.from.toString(),
                mail.to.toString(),
                mail.kitsJson()
            )
        }
    }

    fun getByFrom(uuid: String): List<String> {
        val notes = mutableListOf<String>()
        table.select(dataSource) {
            where("from" eq uuid)
        }.forEach {
            notes.add(getString("id"))
        }

        return notes
    }

    fun getByFromTo(uuid: String): List<String> {
        val notes = mutableListOf<String>()
        table.select(dataSource) {
            where("from" eq uuid)
        }.forEach {
            notes.add(getString("to"))
        }

        return notes
    }

    fun getByTo(uuid: String): List<String> {
        val notes = mutableListOf<String>()
        table.select(dataSource) {
            where("to" eq uuid)
        }.forEach {
            notes.add(getString("id"))
        }

        return notes
    }

    fun getByToFrom(uuid: String): List<String> {
        val notes = mutableListOf<String>()
        table.select(dataSource) {
            where("to" eq uuid)
        }.forEach {
            notes.add(getString("from"))
        }

        return notes
    }

    fun getMail(uuid: String): Mail? {
        return table.select(dataSource) {
            where("id" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("id").uuid()?.let {
                getString("from").uuid()?.let { it1 ->
                    getString("to").uuid()?.let { it2 ->
                        Mail(
                            it,
                            it1,
                            it2,
                            PigeonBox.gson.fromJson(getString("mail"), Array<String>::class.java).toList()
                        )
                    }
                }
            }
        }
    }

    fun getFromByID(uuid: String): String? {
        return table.select(dataSource) {
            where("id" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("from")
        }
    }

    fun getToByID(uuid: String): String? {
        return table.select(dataSource) {
            where("id" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("to")
        }
    }

    fun delete(uuid: String) {
        table.delete(dataSource) {
            where("id" eq uuid)
        }
    }

    private fun String.uuid(): UUID? {
        return UUID.fromString(this)
    }

}