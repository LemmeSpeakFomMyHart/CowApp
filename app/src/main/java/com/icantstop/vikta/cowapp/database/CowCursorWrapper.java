package com.icantstop.vikta.cowapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.icantstop.vikta.cowapp.Cow;

import java.util.Date;
import java.util.UUID;

import static com.icantstop.vikta.cowapp.database.CowDbSchema.*;

/**
 *Класс, который является оберткой для Cursor
 */
public class CowCursorWrapper extends CursorWrapper {
    public CowCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Cow getCow(){
        String uuidString=getString(getColumnIndex(CowTable.Cols.UUID));
        int cowTagNumber=getInt(getColumnIndex(CowTable.Cols.TAG_NUMBER));
        String breed=getString(getColumnIndex(CowTable.Cols.BREED));
        String color=getString(getColumnIndex(CowTable.Cols.COLOR));
        long date=getLong(getColumnIndex(CowTable.Cols.DATE_OF_BIRTH));
        String father=getString(getColumnIndex(CowTable.Cols.FATHER));
        String mother=getString(getColumnIndex(CowTable.Cols.MOTHER));

        Cow cow=new Cow(UUID.fromString(uuidString));
        cow.setTagNumber(cowTagNumber);
        cow.setBreed(breed);
        cow.setColor(color);
        cow.setDateOfBirth(new Date(date));
        cow.setFather(father);
        cow.setMother(mother);

        return cow;
    }
}
