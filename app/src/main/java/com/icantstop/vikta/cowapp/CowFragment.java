package com.icantstop.vikta.cowapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.icantstop.vikta.cowapp.database.CowBaseHelper;
import com.icantstop.vikta.cowapp.database.MeasurementBaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static com.icantstop.vikta.cowapp.database.CowDbSchema.*;
import static com.icantstop.vikta.cowapp.database.MeasurementDbSchema.*;
/**
 *Класс для отображения данных определенной коровы
 */
public class CowFragment extends Fragment {

    private static final String ARG_COW_ID = "cow_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_METRICS = "DialogMetrics";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_METRICS = 1;

    private Cow mCow;
    private Measurement mMeasurement;

    private int posSelectedInFatherSpinnner;
    private int posSelectedInMotherSpinnner;

    private EditText mTagEditText;
    private Spinner mBreedSpinner;
    private Spinner mColorSpinner;
    private Button mBirthDayButton;
    private Spinner mFatherSpinner;
    private Spinner mMotherSpinner;
    private Button mAddMetricButton;

    private LineChart mYieldLineChart;
    private LineChart mFatLineChart;
    private LineChart mWeightLineChart;


    public static CowFragment newInstance(UUID cowId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_COW_ID, cowId);

        CowFragment fragment = new CowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID cowId = (UUID) getArguments().getSerializable(ARG_COW_ID);
        mCow = CowLab.get(getActivity()).getCow(cowId);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCow.getTagNumber() == 0) {
            CowLab.get(getActivity()).deleteCow(mCow);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cow, container, false);


        mTagEditText = v.findViewById(R.id.cow_tag_editText);
        mTagEditText.setText(Integer.toString(mCow.getTagNumber()));
        mTagEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(editable.length() == 0)) {
                    mCow.setTagNumber(Integer.parseInt(editable.toString()));
                }
            }
        });

        mBreedSpinner = v.findViewById(R.id.cow_breed_spinner);
        ArrayAdapter<?> adapterBreedSpinner = ArrayAdapter.createFromResource(getActivity(), R.array.breeds,
                android.R.layout.simple_spinner_item);
        adapterBreedSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBreedSpinner.setAdapter(adapterBreedSpinner);
        mBreedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCow.setBreed(mBreedSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mCow.getBreed() != null) {
            mBreedSpinner.setSelection(getIndexFromArray(mBreedSpinner, mCow.getBreed()));
        }

        mColorSpinner = v.findViewById(R.id.cow_color_spinner);
        ArrayAdapter<?> adapterColorSpinner = ArrayAdapter.createFromResource(getActivity(), R.array.colors,
                android.R.layout.simple_spinner_item);
        adapterColorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mColorSpinner.setAdapter(adapterColorSpinner);
        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCow.setColor(mColorSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mCow.getColor() != null) {
            mColorSpinner.setSelection(getIndexFromArray(mColorSpinner, mCow.getColor()));
        }

        mFatherSpinner = v.findViewById(R.id.cow_father_spinner);

        Cursor cursor = queryCowsColumn(getContext(), CowTable.Cols.TAG_NUMBER,CowTable.Cols.TAG_NUMBER + " != ?",
                new String[]{Integer.toString(mCow.getTagNumber())});
        MatrixCursor extras = new MatrixCursor(new String[]{"_id", CowTable.Cols.TAG_NUMBER});
        extras.addRow(new String[]
                {
                        "-1", "Нет родителя"
                });
        Cursor[] cursors = {extras, cursor};
        Cursor extendedCursor = new MergeCursor(cursors);

        SimpleCursorAdapter adapterFatherSpinner = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_spinner_item, extendedCursor,
                new String[]{CowTable.Cols.TAG_NUMBER},
                new int[]{android.R.id.text1}) {
            @Override
            public boolean isEnabled(int position) {
                if (position == posSelectedInMotherSpinnner && position != 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == posSelectedInMotherSpinnner && position != 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapterFatherSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFatherSpinner.setAdapter(adapterFatherSpinner);
        mFatherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posSelectedInFatherSpinnner = i;
                Cursor cursorItem = (Cursor) adapterView.getAdapter().getItem(i);
                mCow.setFather(cursorItem.getString(1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (mCow.getFather() != null) {
            mFatherSpinner.setSelection(getIndexFromDB(mFatherSpinner, mCow.getFather(),
                    adapterFatherSpinner));
        }


        mMotherSpinner = v.findViewById(R.id.cow_mother_spinner);

        SimpleCursorAdapter adapterMotherSpinner = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_spinner_item, extendedCursor,
                new String[]{CowTable.Cols.TAG_NUMBER},
                new int[]{android.R.id.text1}) {
            @Override
            public boolean isEnabled(int position) {
                if (position == posSelectedInFatherSpinnner && position != 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == posSelectedInFatherSpinnner && position != 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapterMotherSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMotherSpinner.setAdapter(adapterMotherSpinner);
        mMotherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posSelectedInMotherSpinnner = i;
                mCow.setMother(mMotherSpinner.getSelectedItem().toString());
                Cursor cursorItem = (Cursor) adapterView.getAdapter().getItem(i);
                mCow.setMother(cursorItem.getString(1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mCow.getMother() != null) {
            mMotherSpinner.setSelection(getIndexFromDB(mMotherSpinner, mCow.getMother(),
                    adapterFatherSpinner));
        }

        mBirthDayButton = v.findViewById(R.id.cow_birthDay_button);

        updateDate();
        mBirthDayButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                BirthDayPickerFragment dialog = BirthDayPickerFragment.newInstance(mCow.getDateOfBirth());
                dialog.setTargetFragment(CowFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mAddMetricButton = v.findViewById(R.id.cow_add_metrics_button);
        mAddMetricButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCow.getTagNumber() != 0) {
                    if (CowBaseHelper.CheckIsDataAlreadyInDBorNot(CowTable.NAME,
                            CowTable.Cols.TAG_NUMBER,
                            Integer.toString(mCow.getTagNumber()),
                            getContext()) && !
                            checkUUIDequality(mCow)) {
                        mCow.setTagNumber(0);
                        Toast.makeText(getActivity(), "Такой номер уже используется, выберите другой",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mMeasurement = new Measurement();
                        mMeasurement.setTagNumber(mCow.getTagNumber());
                        MeasurementLab.get(getActivity()).addMeasurement(mMeasurement);

                        FragmentManager manager = getFragmentManager();
                        MeasurementAdderFragment dialog = MeasurementAdderFragment
                                .newInstance(mMeasurement);
                        dialog.setTargetFragment(CowFragment.this, REQUEST_METRICS);
                        dialog.show(manager, DIALOG_METRICS);
                    }
                } else {
                    Toast.makeText(getActivity(), "Сначала введите номер бирки!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mYieldLineChart = v.findViewById(R.id.line_chart_milk_yield);
        mYieldLineChart=setUpLineChart(getContext(),MeasurementTable.Cols.MILK_YIELD,
                "Надой, л/сут", mCow,mYieldLineChart);
        mYieldLineChart.invalidate();

        mFatLineChart = v.findViewById(R.id.line_chart_milk_fat);
        mFatLineChart=setUpLineChart(getContext(),MeasurementTable.Cols.MILK_FAT_CONTENT,
                "Жирность, %", mCow,mFatLineChart);
        mFatLineChart.invalidate();

        mWeightLineChart = v.findViewById(R.id.line_chart_cow_weight);
        mWeightLineChart=setUpLineChart(getContext(),MeasurementTable.Cols.COW_WEIGHT,
                "Вес коровы, кг", mCow,mWeightLineChart);
        mWeightLineChart.invalidate();

        return v;
    }

    /**
     *Метод задает характеристики LineChart
     */
    private LineChart setUpLineChart(Context context,String floatColumnName,String nameOfLine,Cow cow,LineChart lineChart){
        if (lineChart!=null) {
            lineChart.clear();
        }else {
            lineChart=new LineChart(context);
        }
        ArrayList dataArray=new ArrayList<>(getData(context,cow, floatColumnName,
                MeasurementTable.Cols.DATE_OF_MEASUREMENT));
        LineDataSet dataSet=new LineDataSet(dataArray, nameOfLine);

        if (floatColumnName==MeasurementTable.Cols.MILK_YIELD){
            lineChart.getAxisLeft().setAxisMaximum(20f);
        }else if (floatColumnName==MeasurementTable.Cols.MILK_FAT_CONTENT){
            lineChart.getAxisLeft().setAxisMaximum(100f);
            dataSet.setColor(getResources().getColor(R.color.orange));
        }else {
            lineChart.getAxisLeft().setAxisMaximum(600f);
            dataSet.setColor(getResources().getColor(R.color.toolbar));
        }
        LineData lineData=new LineData();
        if (!dataArray.isEmpty()){
            lineData.addDataSet(dataSet);
        }
        lineChart.setData(lineData);
        lineChart.getDescription().setText("");
        lineChart.getXAxis().setValueFormatter(new XAxisDateFormatter());
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setAxisMaximum(System.currentTimeMillis());
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setLabelRotationAngle(90f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);

        return lineChart;
    }

    /**
     *Метод для считывания бирок коров по заданному условию
     */
    private Cursor queryCowsColumn(Context context, String columnName,String whereClause, String[] whereArgs) {
        SQLiteDatabase mDatabase = new CowBaseHelper(context).getReadableDatabase();
        Cursor cursor = mDatabase.query(CowTable.NAME,
                new String[]{"_id", columnName},
                whereClause,
                whereArgs,
                null,
                null,
                CowTable.Cols.TAG_NUMBER + " ASC");

        return cursor;
    }

    /**
     *Метод определяет данные для LineChart
     */
    private ArrayList<Entry> getData(Context context, Cow mCow, String floatColumnName, String dateColumnName) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SQLiteDatabase mDB = new MeasurementBaseHelper(context).getReadableDatabase();
        String[] columns = {floatColumnName, dateColumnName};
        Cursor cursor = mDB.query(MeasurementTable.NAME, columns,
                MeasurementTable.Cols.TAG_NUMBER + " = ?", new String[]{Integer.toString(mCow.getTagNumber())},
                null, null, null);
        cursor.moveToFirst();
        int i = 0;
        long time = 0;
        ArrayList<Entry> entries = new ArrayList<>();
        while (i < cursor.getCount()) {
            cursor.moveToPosition(i);
            try {
                Date date = format.parse(cursor.getString(cursor.getColumnIndex(dateColumnName)));
                time = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            float value = cursor.getFloat(cursor.getColumnIndex(floatColumnName));
            entries.add(new Entry(time, value));
            i++;
        }
        Collections.sort(entries,new EntryXComparator());
        return entries;
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        mBirthDayButton.setText(sdf.format(mCow.getDateOfBirth()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(BirthDayPickerFragment.EXTRA_DATE);
            mCow.setDateOfBirth(date);
            updateDate();
        }

        if (requestCode == REQUEST_METRICS) {
            mMeasurement = (Measurement) data.getSerializableExtra(MeasurementAdderFragment.EXTRA_MEASUREMENT);
            MeasurementLab.get(getActivity()).updateMeasurement(mMeasurement);

            mYieldLineChart=setUpLineChart(getContext(),MeasurementTable.Cols.MILK_YIELD,"Надой, л/сут", mCow,mYieldLineChart);
            mYieldLineChart.invalidate();

            mFatLineChart=setUpLineChart(getContext(),MeasurementTable.Cols.MILK_FAT_CONTENT,
                    "Жирность, %", mCow,mFatLineChart);
            mFatLineChart.invalidate();

            mWeightLineChart=setUpLineChart(getContext(),MeasurementTable.Cols.COW_WEIGHT,
                    "Вес коровы, кг", mCow,mWeightLineChart);
            mWeightLineChart.invalidate();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_cow, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_cow:
                if (mCow != null) {
                    Toast.makeText(getActivity(), "Удаляем корову...", Toast.LENGTH_SHORT).show();
                    CowLab.get(getActivity()).deleteCow(mCow);
                    MeasurementLab.get(getActivity()).deleteMeasurementsByTag(mCow.getTagNumber());
                    getActivity().finish();
                }
                return true;
            case R.id.menu_item_save:
                if (mCow != null) {
                    if (mCow.getTagNumber() != 0) {
                        if (CowBaseHelper.CheckIsDataAlreadyInDBorNot(CowTable.NAME,
                                CowTable.Cols.TAG_NUMBER,
                                Integer.toString(mCow.getTagNumber()),
                                getContext()) && !checkUUIDequality(mCow)) {
                            mCow.setTagNumber(0);
                            Toast.makeText(getActivity(), "Такой номер уже используется, выберите другой",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            CowLab.get(getActivity()).updateCow(mCow);
                            getActivity().finish();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Некорректное значение бирки!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case R.id.menu_item_cancel:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *Метод, сравнивающий Id коров, с одинаковыми бирками
     */
    private boolean checkUUIDequality(Cow mCow) {
        Cursor cursor = queryCowsColumn(getContext(),CowTable.Cols.UUID,
                CowTable.Cols.TAG_NUMBER + " = ?",
                new String[]{Integer.toString(mCow.getTagNumber())});
        cursor.moveToFirst();
        String uuidToUpdateString = cursor.getString(cursor.getColumnIndex(CowTable.Cols.UUID));
        UUID mId = UUID.fromString(uuidToUpdateString);
        if (mCow.getId().equals(mId)) {
            return true;
        }
        return false;
    }

    /**
     *Определяет индекс спиннера, который равен заданной строке
     */
    private int getIndexFromArray(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return -1;
    }


    /**
     *Определяет индекс спиннера, который равен заданной строке из БД
     */
    private int getIndexFromDB(Spinner spinner, String myString, CursorAdapter cursorAdapter) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor cursorItem = (Cursor) cursorAdapter.getItem(i);
            if (cursorItem.getString(1).equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return -1;
    }
}
