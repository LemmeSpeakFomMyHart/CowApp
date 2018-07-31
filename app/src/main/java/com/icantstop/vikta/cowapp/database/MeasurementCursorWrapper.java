package com.icantstop.vikta.cowapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.icantstop.vikta.cowapp.Measurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static com.icantstop.vikta.cowapp.database.MeasurementDbSchema.*;

/**
 *Класс, который является оберткой для Cursor
 */
public class MeasurementCursorWrapper extends CursorWrapper {


    public MeasurementCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Measurement getMeasurement(){
        String uuidString=getString(getColumnIndex(MeasurementTable.Cols.UUID));
        int cowTagNumber=getInt(getColumnIndex(MeasurementTable.Cols.TAG_NUMBER));
        String date=getString(getColumnIndex(MeasurementTable.Cols.DATE_OF_MEASUREMENT));
        float yield=getInt(getColumnIndex(MeasurementTable.Cols.MILK_YIELD));
        float fat=getInt(getColumnIndex(MeasurementTable.Cols.MILK_FAT_CONTENT));
        float weight=getInt(getColumnIndex(MeasurementTable.Cols.COW_WEIGHT));

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Measurement measurement=new Measurement(UUID.fromString(uuidString));
        measurement.setTagNumber(cowTagNumber);
        try {
            measurement.setDate(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        measurement.setYield(yield);
        measurement.setFatContent(fat);
        measurement.setWeight(weight);

        return measurement;
    }
}
