package me.xiaozhangup.pigeonbox.data.table

import me.xiaozhangup.pigeonbox.data.DatabaseManager
import me.xiaozhangup.pigeonbox.data.DatabaseManager.dataSource
import me.xiaozhangup.pigeonbox.type.Mail
import me.xiaozhangup.pigeonbox.type.Note
import taboolib.module.database.*

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

    fun delete(uuid: String) {
        table.delete(dataSource) {
            where("id" eq uuid)
        }
    }

}