package com.icantstop.vikta.cowapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icantstop.vikta.cowapp.database.MeasurementBaseHelper;
import com.icantstop.vikta.cowapp.database.MeasurementCursorWrapper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.icantstop.vikta.cowapp.database.MeasurementDbSchema.*;

/**
 *Класс, аналогичный CowLab, только взаимодействующий с метриками
 */
public class MeasurementLab {
    private static MeasurementLab sMeasurementLab;

    private Context mContext;
    private SQLiteDatabase mMeasurementDatabase;

    public static MeasurementLab get(Context context) {
        if (sMeasurementLab == null) {
            sMeasurementLab = new MeasurementLab(context);
        }
        return sMeasurementLab;
    }

    private MeasurementLab(Context context) {
        mContext = context.getApplicationContext();
        mMeasurementDatabase = new MeasurementBaseHelper(mContext).getWritableDatabase();
    }

    public void addMeasurement(Measurement m) {
        ContentValues values = getContentValues(m);

        mMeasurementDatabase.insert(MeasurementTable.NAME, null, values);
    }

    public void deleteMeasurement (Measurement m) {

        mMeasurementDatabase.delete(MeasurementTable.NAME, MeasurementTable.Cols.UUID+" = ?",
                new String[]{String.valueOf(m.getId())});


    }

    public void deleteMeasurementsByTag (int tagNumber) {

        mMeasurementDatabase.delete(MeasurementTable.NAME, MeasurementTable.Cols.TAG_NUMBER+" = ?",
                new String[]{String.valueOf(tagNumber)});


    }

    public List<Measurement> getMeasurements() {
        List<Measurement> measurements = new ArrayList<>();

        MeasurementCursorWrapper cursor = queryMeasurements(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                measurements.add(cursor.getMeasurement());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return measurements;
    }


    public Measurement getMeasurement(UUID id) {
        MeasurementCursorWrapper cursor = queryMeasurements(MeasurementTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        try {
            if (cursor.getCount()==0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getMeasurement();
        } finally {
            cursor.close();
        }
    }

    public void updateMeasurement(Measurement measurement) {
        String uuidString = measurement.getId().toString();
        ContentValues values = getContentValues(measurement);

        mMeasurementDatabase.update(MeasurementTable.NAME, values,
                MeasurementTable.Cols.UUID +
                " = ?", new String[]{uuidString});
    }

    private static ContentValues getContentValues(Measurement measurement) {
        ContentValues values = new ContentValues();
        String date=new SimpleDateFormat("dd-MM-yyyy").format(measurement.getDate());

        values.put(MeasurementTable.Cols.UUID,measurement.getId().toString());
        values.put(MeasurementTable.Cols.TAG_NUMBER, measurement.getTagNumber());
        values.put(MeasurementTable.Cols.DATE_OF_MEASUREMENT, date);
        values.put(MeasurementTable.Cols.MILK_YIELD, measurement.getYield());
        values.put(MeasurementTable.Cols.MILK_FAT_CONTENT, measurement.getFatContent());
        values.put(MeasurementTable.Cols.COW_WEIGHT, measurement.getWeight());

        return values;
    }

    private MeasurementCursorWrapper queryMeasurements(String whereClause, String[] whereArgs) {
        Cursor cursor = mMeasurementDatabase.query(MeasurementTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return new MeasurementCursorWrapper(cursor);
    }
}

