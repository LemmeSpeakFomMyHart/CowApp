package com.icantstop.vikta.cowapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.icantstop.vikta.cowapp.database.MeasurementDbSchema.*;

/**
 *Класс, предназначенный для создания базы данных метрик
 */
public class MeasurementBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "measurementBase.db";

    public MeasurementBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MeasurementTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                MeasurementTable.Cols.UUID + ", " +
                MeasurementTable.Cols.TAG_NUMBER + " integer, " +
                MeasurementTable.Cols.DATE_OF_MEASUREMENT + ", " +
                MeasurementTable.Cols.MILK_YIELD + " real, " +
                MeasurementTable.Cols.MILK_FAT_CONTENT + " real, " +
                MeasurementTable.Cols.COW_WEIGHT + " real)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     *Проверяет есть ли поля с заданным условием в таблице
     */
    public static boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield1, String dbfield2,
                                                      String fieldValue1, String fieldValue2,
                                                      Context context) {
        SQLiteDatabase sqldb = new MeasurementBaseHelper(context).getReadableDatabase();
        String Query = "Select * from " + TableName + " where " + dbfield1 + " = " + fieldValue1 +
                " and "+dbfield2 + " = " + fieldValue2+" and "+MeasurementTable.Cols.MILK_YIELD +
                " != 0";
        Cursor cursor = sqldb.rawQuery(Query, null);
        int i=cursor.getCount();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }
}
