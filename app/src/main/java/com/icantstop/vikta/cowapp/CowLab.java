package com.icantstop.vikta.cowapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icantstop.vikta.cowapp.database.CowBaseHelper;
import com.icantstop.vikta.cowapp.database.CowCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.icantstop.vikta.cowapp.database.CowDbSchema.*;

/**
 *Синглет для хранения массива-списка коров, взаимодействующий с БД
 */
public class CowLab {
    private static CowLab sCowLab;

    private Context mContext;
    private SQLiteDatabase mCowDatabase;

    public static CowLab get(Context context) {
        if (sCowLab == null) {
            sCowLab = new CowLab(context);
        }
        return sCowLab;
    }

    private CowLab(Context context) {
        mContext = context.getApplicationContext();
        mCowDatabase = new CowBaseHelper(mContext).getWritableDatabase();
    }

    /**
     *Добавляет корову в БД
     */
    public void addCow(Cow c) {
        ContentValues values = getContentValues(c);

        mCowDatabase.insert(CowTable.NAME, null, values);
    }

    /**
     *Удаляет корову из БД
     */
    public void deleteCow(Cow c) {

        mCowDatabase.delete(CowTable.NAME, "uuid = ?", new String[]{c.getId().toString()});
    }

    /**
     *Вовращает список коров
     */
    public List<Cow> getCows() {
        List<Cow> cows = new ArrayList<>();

        CowCursorWrapper cursor = queryCows(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cows.add(cursor.getCow());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return cows;
    }

    /**
     *Возвращает корову с заданным идентификатором
     */
    public Cow getCow(UUID id) {
        CowCursorWrapper cursor = queryCows(CowTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        try {
            if (cursor.getCount()==0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCow();
        } finally {
            cursor.close();
        }
    }

    /**
     *Обновляет поля коровы в БД
     */
    public void updateCow(Cow cow) {
        String uuidString = cow.getId().toString();
        ContentValues values = getContentValues(cow);

        mCowDatabase.update(CowTable.NAME, values, CowTable.Cols.UUID +
                " = ?", new String[]{uuidString});
    }

    /**
     *Создает объект ContentValues, который предназначен для хранения типов данных, к-ые могут
     * содержаться в БД SQLite
     */
    private static ContentValues getContentValues(Cow cow) {
        ContentValues values = new ContentValues();
        values.put(CowTable.Cols.UUID,cow.getId().toString());
        values.put(CowTable.Cols.TAG_NUMBER, cow.getTagNumber());
        values.put(CowTable.Cols.BREED, cow.getBreed());
        values.put(CowTable.Cols.COLOR, cow.getColor());
        values.put(CowTable.Cols.DATE_OF_BIRTH, cow.getDateOfBirth().getTime());
        values.put(CowTable.Cols.FATHER, cow.getFather());
        values.put(CowTable.Cols.MOTHER, cow.getMother());

        return values;
    }

    /**
     *Считывает данные из БД
     */
    private CowCursorWrapper queryCows(String whereClause, String[] whereArgs) {
       Cursor cursor = mCowDatabase.query(CowTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return new CowCursorWrapper(cursor);
    }
}
