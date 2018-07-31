package com.icantstop.vikta.cowapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.icantstop.vikta.cowapp.database.MeasurementBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.icantstop.vikta.cowapp.database.MeasurementDbSchema.*;
/**
 *Класс для создания диалога, задающего метрики
 */
public class MeasurementAdderFragment extends DialogFragment {

    public static final String EXTRA_MEASUREMENT = "com.icantstop.vikta.cowapp.measurement";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String ARG_MEASUREMENT = "measurement";


    private static final int REQUEST_DATE = 0;

    private Measurement mMeasurement;
    private Button mDateButton;
    private EditText mYieldEditText;
    private EditText mFatEditText;
    private EditText mWeightEditText;

    public static MeasurementAdderFragment newInstance(Measurement measurement) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEASUREMENT, measurement);
        MeasurementAdderFragment fragment = new MeasurementAdderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMeasurement = (Measurement) getArguments().getSerializable(ARG_MEASUREMENT);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_measurements, null);

        mDateButton = v.findViewById(R.id.cow_measurement_date_button);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                BirthDayPickerFragment dialog = BirthDayPickerFragment
                        .newInstance(mMeasurement.getDate());
                dialog.setTargetFragment(MeasurementAdderFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mYieldEditText = v.findViewById(R.id.cow_yield_editText);
        mYieldEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(editable.length() == 0)) {
                    if (Float.parseFloat(editable.toString()) > 20.0) {
                        float notMoreThan20 = Float.parseFloat(editable.toString()) / 10;
                        mYieldEditText.setText(String.valueOf(notMoreThan20));
                        Toast.makeText(getContext(), "Значение этого поля не может превышать 20",
                                Toast.LENGTH_SHORT);
                    }
                    mMeasurement.setYield(Float.parseFloat(editable.toString()));
                }
            }
        });

        mFatEditText = v.findViewById(R.id.cow_fat_editText);
        mFatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(editable.length() == 0)) {
                    if (Float.parseFloat(editable.toString()) > 100.0) {
                        float notMoreThan100 = Float.parseFloat(editable.toString()) / 10;
                        mFatEditText.setText(String.valueOf(notMoreThan100));
                        Toast.makeText(getContext(), "Значение этого поля не может превышать 100",
                                Toast.LENGTH_SHORT);
                    }
                    mMeasurement.setFatContent(Float.parseFloat(editable.toString()));
                }
            }
        });

        mWeightEditText = v.findViewById(R.id.cow_weight_editText);
        mWeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(editable.length() == 0)) {
                    mMeasurement.setWeight(Float.parseFloat(editable.toString()));
                }
            }
        });


        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.add_metrics)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.cancel, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMeasurement != null) {
                            if (mMeasurement.getYield() != 0 && mMeasurement.getFatContent() != 0 &&
                                    mMeasurement.getWeight() != 0) {
                                if (!MeasurementBaseHelper
                                        .CheckIsDataAlreadyInDBorNot(MeasurementTable.NAME,
                                                MeasurementTable.Cols.TAG_NUMBER,
                                                MeasurementTable.Cols.DATE_OF_MEASUREMENT,
                                                Integer.toString(mMeasurement.getTagNumber()),
                                                "\'" + (new SimpleDateFormat("dd-MM-yyyy")
                                                        .format(mMeasurement.getDate())) + "\'",
                                                getContext())) {
                                    sendResult(Activity.RESULT_OK, mMeasurement);
                                    dialog.dismiss();
                                } else {
                                    AlertDialog.Builder dialogUpdate = new AlertDialog.Builder(getActivity())
                                            .setTitle(R.string.save)
                                            .setMessage(R.string.question_metrics)
                                            .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Cursor cursor = queryMeasurementsUUID(MeasurementTable.NAME,
                                                            MeasurementTable.Cols.TAG_NUMBER,
                                                            MeasurementTable.Cols.DATE_OF_MEASUREMENT,
                                                            Integer.toString(mMeasurement.getTagNumber()),
                                                            "\'" + (new SimpleDateFormat("dd-MM-yyyy")
                                                                    .format(mMeasurement.getDate())) + "\'",
                                                            getContext());
                                                    cursor.moveToFirst();
                                                    String uuidToUpdateString = cursor.getString(cursor.getColumnIndex(MeasurementTable.Cols.UUID));

                                                    UUID mIdToDelete = mMeasurement.getId();
                                                    UUID mIdToUpdate = UUID.fromString(uuidToUpdateString);
                                                    mMeasurement.setId(mIdToUpdate);

                                                    sendResult(Activity.RESULT_OK, mMeasurement);

                                                    mMeasurement.setId(mIdToDelete);
                                                    MeasurementLab.get(getActivity()).deleteMeasurement(mMeasurement);
                                                    dialog.dismiss();
                                                }
                                            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    MeasurementLab.get(getActivity()).deleteMeasurement(mMeasurement);
                                                }
                                            });
                                    dialogUpdate.show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Введите все данные!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        return dialog;

    }

    /**
     *Метод считывающий все поля из БД по двум условиям
     */
    private Cursor queryMeasurementsUUID(String TableName,
                                         String dbfield1, String dbfield2,
                                         String fieldValue1, String fieldValue2,
                                         Context context) {
        SQLiteDatabase mDatabase = new MeasurementBaseHelper(context).getReadableDatabase();


        String Query = "Select * from " + TableName + " where " + dbfield1 + " = " + fieldValue1 +
                " and " + dbfield2 + " = " + fieldValue2;
        Cursor cursor = mDatabase.rawQuery(Query, null);

        return cursor;
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMMMMMM, yyyy");
        mDateButton.setText(sdf.format(mMeasurement.getDate()));
    }

    private void sendResult(int resultCode, Measurement measurement) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_MEASUREMENT, measurement);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(BirthDayPickerFragment.EXTRA_DATE);
            mMeasurement.setDate(date);
            updateDate();
        }

    }
}
