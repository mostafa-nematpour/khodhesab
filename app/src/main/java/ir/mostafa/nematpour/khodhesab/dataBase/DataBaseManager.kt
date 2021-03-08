package ir.mostafa.nematpour.khodhesab.dataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ir.mostafa.nematpour.khodhesab.model.Part
import ir.mostafa.nematpour.khodhesab.model.Person
import ir.mostafa.nematpour.khodhesab.model.Spent

class DataBaseManager(
    context: Context?,
    name: String? = "Database.db",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    private val tableName = "firstList"
    private val dID = "id"
    private val dName = "name"
    private val dMoney = "money"
    private val dImageLink = "imageLink"

    private val tableSpent = "SpentList"
    private val dPersonIdSpent = "personId"
    private val dTime = "time"
    private val dDate = "date"
    private val dAbout = "about"

    private val tableListOfParts = "SpentListOfParts"
    private val dPersonId = "personId"
    private val dSpentId = "spentId"
    override fun onCreate(db: SQLiteDatabase?) {
        val cQuery =
            "CREATE TABLE $tableName ( $dID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $dName VARCHAR, $dMoney INTEGER, $dImageLink TEXT);"
        val cQuery2 =
            "CREATE TABLE $tableSpent ( $dID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $dPersonIdSpent INTEGER, $dMoney INTEGER, $dDate VARCHAR, $dTime VARCHAR, $dAbout TEXT);"
        val cQuery3 =
            "CREATE TABLE $tableListOfParts ( $dID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, $dSpentId INTEGER, $dPersonId INTEGER );"
        db?.execSQL(cQuery)
        db?.execSQL(cQuery2)
        db?.execSQL(cQuery3)
        Log.d("db", "onCreate")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertParts(part: Part) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(dPersonId, part.personId)
        icv.put(dSpentId, part.spentId)
        idb.insert(tableListOfParts, null, icv)
        idb.close()
    }

    fun insertSpent(spent: Spent) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(dPersonIdSpent, spent.buyerId)
        icv.put(dMoney, spent.money)
        icv.put(dDate, spent.date)
        icv.put(dTime, spent.time)
        icv.put(dAbout, spent.about)
        idb.insert(tableSpent, null, icv)
        idb.close()
    }


    fun insertPerson(person: Person) {
        val idb = this.writableDatabase
        val icv = ContentValues()
        icv.put(dName, person.name)
        icv.put(dMoney, person.money)
        icv.put(dImageLink, person.imageLink)
        idb.insert(tableName, null, icv)
        idb.close()
    }

    private fun getPartBySpentId(i: Int): MutableList<Person> {
        val items = mutableListOf<Person>()
        val gdb = this.readableDatabase
        val gQuery =
            "SELECT * FROM $tableListOfParts WHERE $dSpentId = $i"

        //SELECT * FROM SpentListOfParts WHERE spentId = 4
        val cursor: Cursor = gdb.rawQuery(gQuery, null)

        if (cursor.moveToFirst()) {
            do {

                val person = this.getPerson(cursor.getString(2))
                if (person != null) {
                    items.add(person)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }


    fun getPerson(gID: String): Person? {
        var gPrs: Person? = null
        val gdb = this.readableDatabase
        val gQuery =
            "SELECT * FROM $tableName WHERE $dID=$gID"
        val gCur: Cursor = gdb.rawQuery(gQuery, null)
        if (gCur.moveToFirst()) {
            gPrs = Person(
                gCur.getString(0).toInt(),
                gCur.getString(1),
                gCur.getString(2).toInt(),
                gCur.getString(3)
            )
        }
        gCur.close()
        return gPrs
    }


    fun getSpent(gID: Int): Spent? {
        var gPrs: Spent? = null
        val gdb = this.readableDatabase
        val gQuery =
            "SELECT * FROM $tableSpent WHERE $dID=$gID"
        val cursor: Cursor = gdb.rawQuery(gQuery, null)
        if (cursor.moveToFirst()) {
            gPrs = Spent(
                cursor.getString(0).toInt(),
                cursor.getString(1).toInt(),
                cursor.getString(2).toInt(),
                this.getPartBySpentId(cursor.getString(0).toInt()),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
            )
        }
        cursor.close()
        return gPrs
    }


    fun getSpents(): MutableList<Spent> {
        val items = mutableListOf<Spent>()
        val gdb = this.readableDatabase
        val cursor: Cursor = gdb.query(
            tableSpent,
            arrayOf(dID, dPersonIdSpent, dMoney, dDate, dTime, dAbout),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val spent =
                    Spent(
                        cursor.getString(0).toInt(),
                        cursor.getString(1).toInt(),
                        cursor.getString(2).toInt(),
                        null,
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                    )
                items.add(spent)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun getPersons(): MutableList<Person> {
        val items = mutableListOf<Person>()
        val gdb = this.readableDatabase
//        val gQuery = "SELECT * FROM $tableName"
        val cursor: Cursor = gdb.query(
            tableName,
            arrayOf(dID, dName, dMoney, dImageLink),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val person =
                    Person(
                        cursor.getString(0).toInt(),
                        cursor.getString(1),
                        cursor.getString(2).toInt(),
                        cursor.getString(3)
                    )
                items.add(person)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun updateSpent(spent: Spent) {
        if (spent.list != null && spent.list!!.size > 0) {
            val udb = this.writableDatabase
            val ucv = ContentValues()
            ucv.put(dPersonIdSpent, spent.buyerId)
            ucv.put(dMoney, spent.money)
            ucv.put(dAbout, spent.about)
            ucv.put(dTime, spent.time)
            ucv.put(dDate, spent.date)
            ucv.put(dAbout, spent.about)
            udb.update(
                tableSpent,
                ucv,
                "$dID = ?",
                arrayOf(java.lang.String.valueOf(spent.id))
            )
            udb.execSQL("DELETE FROM $tableListOfParts WHERE spentId = ${spent.id}")

            for (p in spent.list!!) {
                if (p.flag)
                    this.insertParts(Part(0, spent.id, p.id))
            }


        }
        Log.i("Mahdi", spent.money.toString())
    }
/*
     fun deletePerson(dprs: Person): Boolean {
         val ddb = this.writableDatabase
         val dResult = ddb.delete(
             TableName,
             "$dID=?",
             arrayOf(java.lang.String.valueOf(dprs.pID))
         ).toLong()
         Log.i("Mahdi", "deletePerson Method")
         return if (dResult == 0L) false else true
     }*/

    fun personCount(): Int {
        val gQuery = "SELECT * FROM $tableName"
        val gdb = this.readableDatabase
        val gCur: Cursor = gdb.rawQuery(gQuery, null)
        val a = gCur.count
        gCur.close()
        return a
    }

    fun deleteAll() {
        val query = "DELETE FROM $tableName"
        val query1 = "DELETE FROM sqlite_sequence WHERE name='$tableName'"
        val queryS = "DELETE FROM $tableSpent"
        val queryS1 = "DELETE FROM sqlite_sequence WHERE name='$tableSpent'"

        val gdb = this.writableDatabase
        gdb.execSQL(query)
        gdb.execSQL(query1)
        gdb.execSQL(queryS)
        gdb.execSQL(queryS1)

    }
}