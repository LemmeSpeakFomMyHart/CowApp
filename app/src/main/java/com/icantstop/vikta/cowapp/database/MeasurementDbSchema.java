package com.icantstop.vikta.cowapp.database;

/**
 *Класс для хранения схемы БД, в котором описываются строковые константы для определения таблицы
 */
public class MeasurementDbSchema {
    public static final class MeasurementTable {
        public static final String NAME="measurements";

        /**
         *Определения столбцов таблицы
         */
        public static final class Cols {
            public static final String UUID="uuid";
            public static final String TAG_NUMBER="tag";
            public static final String DATE_OF_MEASUREMENT="date";
            public static final String MILK_YIELD="yield";
            public static final String MILK_FAT_CONTENT="fat";
            public static final String COW_WEIGHT="weight";

        }
    }

}
