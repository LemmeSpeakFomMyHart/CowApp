package com.icantstop.vikta.cowapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.icantstop.vikta.cowapp.database.CowDbSchema.CowTable;

/**
 *Класс, предназначенный для создания базы данных коров
 */
public class CowBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "cowBase.db";

    public CowBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CowTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CowTable.Cols.UUID + ", " +
                CowTable.Cols.TAG_NUMBER + " integer, " +
                CowTable.Cols.BREED + ", " +
                CowTable.Cols.COLOR + ", " +
                CowTable.Cols.DATE_OF_BIRTH + ", " +
                CowTable.Cols.FATHER + ", " +
                CowTable.Cols.MOTHER + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     *Проверяет есть ли поля с заданным условием в таблице
     */
    public static boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield, String fieldValue, Context context) {
        SQLiteDatabase sqldb = new CowBaseHelper(context).getReadableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
