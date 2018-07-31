package com.icantstop.vikta.cowapp;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *Класс, для форматирования оси абсциисс (оси Х)
 */
public class XAxisDateFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date((long) value));
    }
}
