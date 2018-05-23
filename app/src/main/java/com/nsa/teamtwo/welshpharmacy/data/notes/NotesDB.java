package com.nsa.teamtwo.welshpharmacy.data.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.jar.Attributes;

/**
 * Created by c1716791 on 21/04/2018.
 */

public class NotesDB extends SQLiteOpenHelper {


    private static final String TAG = "NotesDB";

    private static final String TABLE_NAME = "notes_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "note";

    public NotesDB(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT)";
        db.execSQL(createTable);
    }

    public boolean addNote(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);

        Log.d(TAG, "addData Adding " + item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

//        Checking if the data has been inserted safely
        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;

    }

    public void updateNote(String editedNote, int id, String originalNote){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + editedNote + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + originalNote + "'";

    Log.d(TAG, "UpdatedNote: Query: " + query);
    Log.d(TAG,"UpdatedNote: Setting name to " + editedNote);
    db.execSQL(query);
    }

    public void deleteNote(int id, String note){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + note + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting" + note + " from database.");
        db.execSQL(query);

    }
}
