package myoo.votingapp.viewmodel

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log

import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap


import myoo.votingapp.Model.EvaluatorDetail
import myoo.votingapp.Model.PreparedSpeakerDetail
import myoo.votingapp.Model.RoleTakerDetail
import myoo.votingapp.Model.TableTopicDetail
import myoo.votingapp.Model.TagTeamDetail
import myoo.votingapp.R

/**
 * Created by MA294214 on 7/9/2017.
 */

class DBHelper(internal var context: Context) : SQLiteOpenHelper(context, Environment.getExternalStorageDirectory().absolutePath + "/voting app /" + DATABASE_NAME, null, 3) {
    internal val COLUMN_NAME = "name"
    internal val COL_NAME = "name "
    internal val COL_PROJECT = "project "
    internal val COL_ROLE = "role "
    internal val COL_TITLE = "title"
    internal val COL_NUMBEROFVOTES = "number_of_votes"
    /*
    + "voting_item " + " text ,"
            + "name " + " text ,"
            + "project " + " text , "
            + "role " + " text ,"
            + "title " + " text "
            + "number_of_votes " + " integer "

    */
    internal val COL_VOTING_ITEM = "voting_item "
    internal val COL_IS_DISABLED = "is_disabled "


    // The table to query
    // The columns to return
    // The columns for the WHERE clause
    // The values for the WHERE clause
    // don't group the rows
    // don't filter by row groups
    // The sort order
    val members: ArrayList<String>?
        get() {

            val TABLE_NAME = "members"

            val candidates = ArrayList<String>()

            try {
                val db = this.writableDatabase
                db.execSQL("create table if not exists $TABLE_NAME (_id integer primary key autoincrement,name text);")
                val projection = arrayOf("name")

                val cursor = db.query(
                        TABLE_NAME,
                        projection, // don't filter by row groups
                        null                                 // The sort order
                        , null, null, null, null
                )

                cursor.moveToFirst()

                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    candidates.add(name)
                    cursor.moveToNext()
                }


            } catch (e: Exception) {

                return null
            }

            return candidates
        }

    // The table to query
    // The columns to return
    // The columns for the WHERE clause
    // The values for the WHERE clause
    // don't group the rows
    // don't filter by row groups
    // The sort order
    val guests: ArrayList<String>?
        get() {

            val TABLE_NAME = "guests"

            val candidates = ArrayList<String>()

            try {
                val db = this.writableDatabase
                db.execSQL("create table if not exists $TABLE_NAME (_id integer primary key autoincrement,name text);")
                val projection = arrayOf("name")

                val cursor = db.query(
                        TABLE_NAME,
                        projection, null, null, null, null, null
                )

                cursor.moveToFirst()

                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    candidates.add(name)
                    cursor.moveToNext()
                }


            } catch (e: Exception) {

                return null
            }

            return candidates
        }

    val listOfTables: ArrayList<String>
        get() {

            val tables = ArrayList<String>()

            val query = "SELECT name  FROM sqlite_master where type='table'"

            val db = this.writableDatabase

            val cursor = db.rawQuery(query, null)

            cursor.moveToFirst()
            cursor.moveToNext()

            while (cursor.isAfterLast == false) {
                val table_name = cursor.getString(0)

                tables.add(cursor.getString(0))
                cursor.moveToNext()
            }
            return tables

        }

    val listOfMeetings: ArrayList<String>
        get() {

            val table_list = listOfTables
            val meeting_list = ArrayList<String>()
            try {

                for (i in table_list.indices) {
                    val table_name = table_list[i]
                    if (table_name.contains("Voting_Items")) {

                        val position = table_name.lastIndexOf("_")
                        val meeting_number = table_name.substring("Voting_Items_".length)
                        meeting_list.add(meeting_number)

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            return meeting_list
        }

    /**
     * Method used to initialize [SQLiteDatabase] object
     */
    @Throws(SQLException::class)
    fun openClose(): SQLiteDatabase? {
        var db: SQLiteDatabase? = null
        try {
            db = this.writableDatabase
        } catch (e: Exception) {

        }

        if (db != null) db.close()
        return db

    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        //we have to  change some database schema to add new functionality like getUsersListRef disqualify
        //and also new table called member
        val allTab = db
                .rawQuery(
                        "select name from sqlite_master where type = 'table'", null)
        if (allTab != null && allTab.count > 0) {

            allTab.moveToFirst()
            do {
                try {
                    if (allTab.getString(0).startsWith("Voting_Items_")) {
                        //"is_disabled"
                        db.execSQL("ALTER TABLE " + allTab.getString(0) + " ADD COLUMN is_disabled boolean DEFAULT false")
                    }
                } catch (e: Exception) {
                    // TODO: handle exception

                }

            } while (allTab.moveToNext())
        }
        db.execSQL("create table if not exists members (_id integer primary key autoincrement,name text);")
    }


    fun addVotingItem(candidate_details: PreparedSpeakerDetail, voting_item: String, meeting_number: String, votes: Int): Boolean {

        try {
            val TABLE_NAME = "Voting_Items_$meeting_number"
            val contentValues = ContentValues()
            contentValues.put(COL_VOTING_ITEM, voting_item)
            contentValues.put(COL_NAME, candidate_details.name)
            contentValues.put(COL_TITLE, candidate_details.title)
            contentValues.put(COL_PROJECT, candidate_details.project)
            contentValues.put(COL_NUMBEROFVOTES, votes)
            contentValues.put(COL_IS_DISABLED, candidate_details.isDisabled)

            val db = this.writableDatabase
            db.insert(TABLE_NAME, null, contentValues)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


        return true
    }

    fun addVotingItem(candidate_details: TableTopicDetail, voting_item: String, meeting_number: String, votes: Int): Boolean {

        try {
            val TABLE_NAME = "Voting_Items_$meeting_number"
            val contentValues = ContentValues()
            contentValues.put(COL_VOTING_ITEM, voting_item)
            contentValues.put(COL_NAME, candidate_details.name)
            contentValues.put(COL_NUMBEROFVOTES, votes)
            contentValues.put(COL_IS_DISABLED, candidate_details.isDisabled)
            val db = this.writableDatabase
            db.insert(TABLE_NAME, null, contentValues)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


        return true
    }

    fun addVotingItem(candidate_details: RoleTakerDetail, voting_item: String, meeting_number: String, votes: Int): Boolean {

        try {
            val TABLE_NAME = "Voting_Items_$meeting_number"
            val contentValues = ContentValues()
            contentValues.put(COL_VOTING_ITEM, voting_item)
            contentValues.put(COL_NAME, candidate_details.name)
            contentValues.put(COL_NUMBEROFVOTES, votes)
            contentValues.put(COL_ROLE, candidate_details.role)
            contentValues.put(COL_IS_DISABLED, candidate_details.isDisabled)
            val db = this.writableDatabase
            db.insert(TABLE_NAME, null, contentValues)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


        return true
    }


    fun addVotingItem(candidate_details: TagTeamDetail, voting_item: String, meeting_number: String, votes: Int): Boolean {

        try {
            val TABLE_NAME = "Voting_Items_$meeting_number"
            val contentValues = ContentValues()
            contentValues.put(COL_VOTING_ITEM, voting_item)
            contentValues.put(COL_NAME, candidate_details.name)
            contentValues.put(COL_NUMBEROFVOTES, votes)
            contentValues.put(COL_ROLE, candidate_details.role)
            contentValues.put(COL_IS_DISABLED, candidate_details.isDisabled)
            val db = this.writableDatabase
            db.insert(TABLE_NAME, null, contentValues)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


        return true
    }

    fun addVotingItem(candidate_details: EvaluatorDetail, voting_item: String, meeting_number: String, votes: Int): Boolean {

        try {
            val TABLE_NAME = "Voting_Items_$meeting_number"
            val contentValues = ContentValues()
            contentValues.put("voting_item", voting_item)
            contentValues.put("name", candidate_details.name)
            contentValues.put(COL_NUMBEROFVOTES, votes)
            contentValues.put(COL_IS_DISABLED, candidate_details.isDisabled)
            val db = this.writableDatabase
            db.insert(TABLE_NAME, null, contentValues)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


        return true
    }

    fun createVotingItemTable(meeting_number: String): Boolean {
        val TABLE_NAME = "Voting_Items_$meeting_number"
        try {
            val db = this.writableDatabase

            db.execSQL(
                    "drop table if exists $TABLE_NAME"
            )

            db.execSQL(
                    "create table " + TABLE_NAME +
                            "(id integer primary key, "
                            + "voting_item " + " text ,"
                            + "name " + " text ,"
                            + "project " + " text , "
                            + "role " + " text ,"
                            + "title " + " text ,"
                            + "number_of_votes " + " integer ,"
                            + "is_disabled boolean default false" +
                            "  )"
            )


        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true

    }

    /**
     * Method to inert new Member @see [] for implementation
     * @param name new member
     */
    fun insertMembers(name: String): Boolean {
        val TABLE_NAME = "members"
        try {
            val db = this.writableDatabase


            db.execSQL("create table if not exists $TABLE_NAME (_id integer primary key autoincrement,name text);")

            val initialValues = ContentValues()
            initialValues.put("name", name)
            return db.insert(TABLE_NAME, null, initialValues) > 0

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


    }

    /**
     * Method to set members table after deletion and upation on a member
     * @param nameList
     */
    fun setMembers(nameList: ArrayList<String>): Boolean {
        val TABLE_NAME = "members"
        try {
            val db = this.writableDatabase
            db.delete(TABLE_NAME, null, null)
            db.execSQL("create table if not exists $TABLE_NAME (_id integer primary key autoincrement,name text);")
            val initialValues = ContentValues()
            for (name in nameList) {


                initialValues.put("name", name)
                db.insert(TABLE_NAME, null, initialValues)
            }


            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


    }

    /**
     * Method to inert new Member @see [] for implementation
     * @param name new member
     */
    fun insertGuests(name: String): Boolean {
        val TABLE_NAME = "guests"
        try {
            val db = this.writableDatabase


            db.execSQL("create table if not exists $TABLE_NAME (_id integer primary key autoincrement,name text);")

            val initialValues = ContentValues()
            initialValues.put("name", name)
            return db.insert(TABLE_NAME, null, initialValues) > 0

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


    }

    /**
     * Method to set members table after deletion and upation on a member
     * @param nameList
     */
    fun setguests(nameList: ArrayList<String>): Boolean {
        val TABLE_NAME = "guests"
        try {
            val db = this.writableDatabase
            db.delete(TABLE_NAME, null, null)
            db.execSQL("create table if not exists $TABLE_NAME (_id integer primary key autoincrement,name text);")
            val initialValues = ContentValues()
            for (name in nameList) {


                initialValues.put("name", name)
                db.insert(TABLE_NAME, null, initialValues)
            }


            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }


    }

    fun getCandidates(meeting_number: String, voting_item: String): ArrayList<Any>? {

        val TABLE_NAME = "Voting_Items_$meeting_number"

        val candidates = ArrayList<Any>()

        try {
            val db = this.writableDatabase

            val projection = arrayOf(COLUMN_NAME, COL_PROJECT, COL_TITLE, COL_ROLE, COL_IS_DISABLED)

            val cursor = db.query(
                    TABLE_NAME, // The table to query
                    projection, // The columns to return
                    "$COL_VOTING_ITEM=?", // The columns for the WHERE clause
                    arrayOf(voting_item), null, null, null// The sort order
            )// The values for the WHERE clause
            // don't group the rows
            // don't filter by row groups

            cursor.moveToFirst()

            if (voting_item == context.resources.getText(R.string.prepared_speaker))
                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    val project = cursor.getString(1)
                    val title = cursor.getString(2)
                    val preparedSpeakerDetail = PreparedSpeakerDetail(name,
                            title, project, cursor.getInt(4) == 1, null, "", "")
                    candidates.add(preparedSpeakerDetail)
                    cursor.moveToNext()
                }
            if (voting_item == context.resources.getText(R.string.role_takers))
                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    val role = cursor.getString(3)
                    val roleTakerDetail = RoleTakerDetail(name, role,
                            cursor.getInt(4) == 1)
                    candidates.add(roleTakerDetail)
                    cursor.moveToNext()
                }
            if (voting_item == context.resources.getText(R.string.tag_team))
                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    val role = cursor.getString(3)
                    val tagTeamDetail = TagTeamDetail(name, role, cursor.getInt(4) == 1)
                    candidates.add(tagTeamDetail)
                    cursor.moveToNext()
                }
            if (voting_item == context.resources.getText(R.string.table_topics))
                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    val tableTopicDetail = TableTopicDetail(name, cursor.getInt(4) == 1)
                    candidates.add(tableTopicDetail)
                    cursor.moveToNext()
                }
            if (voting_item == context.resources.getText(R.string.evaluators))
                while (cursor.isAfterLast == false) {

                    val name = cursor.getString(0)
                    val evaluatorDetail = EvaluatorDetail(name,
                            cursor.getInt(4) == 1)
                    candidates.add(evaluatorDetail)
                    cursor.moveToNext()
                }

        } catch (e: Exception) {

            return null
        }

        return candidates
    }

    fun getCandidates(meeting_number: String): ArrayList<Any>? {

        val TABLE_NAME = "Voting_Items_$meeting_number"

        val candidates = ArrayList<Any>()

        try {
            val db = this.writableDatabase

            val projection = arrayOf(COLUMN_NAME, COL_PROJECT, COL_TITLE, COL_ROLE, COL_IS_DISABLED, COL_VOTING_ITEM)

            val cursor = db.query(
                    TABLE_NAME, // The table to query
                    projection, null, null, null, null, null// The sort order
            )// The columns to return
            // The columns for the WHERE clause
            // The values for the WHERE clause
            // don't group the rows
            // don't filter by row groups

            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {
                if (cursor.getString(5) == context.resources.getText(R.string.prepared_speaker)) {
                    val name = cursor.getString(0)
                    val project = cursor.getString(1)
                    val title = cursor.getString(2)
                    val preparedSpeakerDetail = PreparedSpeakerDetail(name,
                            title, project, cursor.getInt(4) == 1)
                    candidates.add(preparedSpeakerDetail)

                }
                if (cursor.getString(5) == context.resources.getText(R.string.role_takers)) {

                    val name = cursor.getString(0)
                    val role = cursor.getString(3)
                    val roleTakerDetail = RoleTakerDetail(name, role,
                            cursor.getInt(4) == 1)
                    candidates.add(roleTakerDetail)
                }
                if (cursor.getString(5) == context.resources.getText(R.string.tag_team)) {

                    val name = cursor.getString(0)
                    val role = cursor.getString(3)
                    val tagTeamDetailDetail = TagTeamDetail(name, role, cursor.getInt(4) == 1)
                    candidates.add(tagTeamDetailDetail)
                }
                if (cursor.getString(5) == context.resources.getText(R.string.table_topics)) {
                    val name = cursor.getString(0)
                    val tableTopicDetail = TableTopicDetail(name,
                            cursor.getInt(4) == 1)
                    candidates.add(tableTopicDetail)
                }
                if (cursor.getString(5) == context.resources.getText(R.string.evaluators)) {
                    val name = cursor.getString(0)
                    val evaluatorDetail = EvaluatorDetail(name,
                            cursor.getInt(4) == 1)
                    candidates.add(evaluatorDetail)
                }
                cursor.moveToNext()
            }
        } catch (e: Exception) {

            return null
        }

        return candidates
    }

    fun getNumberOfVotes(meeting_number: String, voting_item: String): ArrayList<Int> {

        val number_of_votes = ArrayList<Int>()
        val TABLE_NAME = "Voting_Items_$meeting_number"


        try {
            val db = this.writableDatabase

            val projection = arrayOf(COL_NUMBEROFVOTES)

            val cursor = db.query(
                    TABLE_NAME, // The table to query
                    projection, // The columns to return
                    "$COL_VOTING_ITEM =?", // The columns for the WHERE clause
                    arrayOf(voting_item), null, null, null// The sort order
            )// The values for the WHERE clause
            // don't group the rows
            // don't filter by row groups

            cursor.moveToFirst()

            while (cursor.isAfterLast == false) {

                val votes = cursor.getInt(0)
                number_of_votes.add(votes)
                cursor.moveToNext()
            }


        } catch (e: Exception) {

            return arrayListOf()
        }

        return number_of_votes
    }

    fun getCandidatesAndVotes(meeting_number: String, voting_item: String): HashMap<String, Any> {

        val map = HashMap<String, Any>()

        Log.d("checkcandidates", "voting_item was" + voting_item + "end")
        // voting_item = "Prepared Speakers";
        Log.d("checkcandidates", "voting_item  is" + voting_item + "end")
        val candidates = getCandidates(meeting_number, voting_item)
        Log.d("checkcandidates", "got the candidates list of size " + candidates?.size)

        val number_of_votes = getNumberOfVotes(meeting_number, voting_item)

        map["candidates"] = candidates ?: arrayListOf<Any>()
        map["number_of_votes"] = number_of_votes


        return map
    }

    fun setVotingCandidates(candidates: ArrayList<Any>, meeting_number: String, voting_item: String): Boolean {

        val TABLE_NAME = "Voting_Items_$meeting_number"

        val db = this.writableDatabase

        val whereClause = "$COL_VOTING_ITEM=?"
        val whereArgs = arrayOf(voting_item)
        db.delete(TABLE_NAME, whereClause, whereArgs)

        for (i in candidates.indices) {
            if (voting_item == context.resources.getText(R.string.prepared_speaker)) {
                addVotingItem(candidates[i] as PreparedSpeakerDetail, voting_item, meeting_number, 0)
            }
            if (voting_item == context.resources.getText(R.string.table_topics)) {
                addVotingItem(candidates[i] as TableTopicDetail, voting_item, meeting_number, 0)
            }
            if (voting_item == context.resources.getText(R.string.role_takers)) {
                addVotingItem(candidates[i] as RoleTakerDetail, voting_item, meeting_number, 0)
            }
            if (voting_item == context.resources.getText(R.string.evaluators)) {
                addVotingItem(candidates[i] as EvaluatorDetail, voting_item, meeting_number, 0)
            }
            if (voting_item == context.resources.getText(R.string.tag_team)) {
                addVotingItem(candidates[i] as TagTeamDetail, voting_item, meeting_number, 0)
            }


        }


        return true
    }

    fun setVotingCandidatesDisqualified(candidates: ArrayList<Any>, meeting_number: String, voting_item: String): Boolean {
        val TABLE_NAME = "Voting_Items_$meeting_number"

        val db = this.writableDatabase

        val whereClause = "$COL_VOTING_ITEM=?"
        val whereArgs = arrayOf(voting_item)
        db.delete(TABLE_NAME, whereClause, whereArgs)

        for (i in candidates.indices) {
            if (candidates[i] is PreparedSpeakerDetail && voting_item == context.resources.getText(R.string.prepared_speaker)) {
                addVotingItem(candidates[i] as PreparedSpeakerDetail, voting_item, meeting_number, 0)
            }
            if (candidates[i] is TableTopicDetail && voting_item == context.resources.getText(R.string.table_topics)) {
                addVotingItem(candidates[i] as TableTopicDetail, voting_item, meeting_number, 0)
            }
            if (candidates[i] is RoleTakerDetail && voting_item == context.resources.getText(R.string.role_takers)) {
                addVotingItem(candidates[i] as RoleTakerDetail, voting_item, meeting_number, 0)
            }
            if (candidates[i] is EvaluatorDetail && voting_item == context.resources.getText(R.string.evaluators)) {
                addVotingItem(candidates[i] as EvaluatorDetail, voting_item, meeting_number, 0)
            }
            if (candidates[i] is TagTeamDetail && voting_item == context.resources.getText(R.string.tag_team)) {
                addVotingItem(candidates[i] as TagTeamDetail, voting_item, meeting_number, 0)
            }


        }


        return true
    }


    fun setVotingCandidatesAndVotes(candidates: ArrayList<Any>, meeting_number: String, voting_item: String, votes_list: ArrayList<Int>): Boolean {

        val TABLE_NAME = "Voting_Items_$meeting_number"

        val db = this.writableDatabase

        val whereClause = "$COL_VOTING_ITEM=?"
        val whereArgs = arrayOf(voting_item)
        db.delete(TABLE_NAME, whereClause, whereArgs)

        for (i in candidates.indices) {
            if (voting_item == context.resources.getText(R.string.prepared_speaker)) {
                Log.d("checkvotes", "In dbhelper votes_list.get(i)  is" + votes_list[i])
                addVotingItem(candidates[i] as PreparedSpeakerDetail, voting_item, meeting_number, votes_list[i])
            }
            if (voting_item == context.resources.getText(R.string.table_topics)) {
                addVotingItem(candidates[i] as TableTopicDetail, voting_item, meeting_number, votes_list[i])
            }
            if (voting_item == context.resources.getText(R.string.role_takers)) {
                addVotingItem(candidates[i] as RoleTakerDetail, voting_item, meeting_number, votes_list[i])
            }
            if (voting_item == context.resources.getText(R.string.evaluators)) {
                addVotingItem(candidates[i] as EvaluatorDetail, voting_item, meeting_number, votes_list[i])
            }
            if (voting_item == context.resources.getText(R.string.tag_team)) {
                addVotingItem(candidates[i] as TagTeamDetail, voting_item, meeting_number, votes_list[i])
            }


        }


        return true
    }

    fun getListOfMembers(club_name: String): ArrayList<*>? {

        val members = ArrayList<String>()
        try {
            val query = "SELECT name  FROM sqlite_master where type='table'"

            val db = this.writableDatabase

            val projection = arrayOf(COLUMN_NAME)

            val cursor = db.query(
                    club_name, // The table to query
                    projection, null, null, null, null, null
            )// The columns to return
            // The columns for the WHERE clause
            // The values for the WHERE clause
            // don't group the rows

            cursor.moveToFirst()

            while (cursor.isAfterLast == false) {
                members.add(cursor.getString(0))
                cursor.moveToNext()
            }

        } catch (e: Exception) {

            return null
        }

        return members

    }

    companion object {

        val DATABASE_NAME = "votingapp.db"
    }
}

