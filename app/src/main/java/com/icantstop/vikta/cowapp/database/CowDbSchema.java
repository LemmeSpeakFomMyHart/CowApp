package com.icantstop.vikta.cowapp.database;

/**
 *Класс для хранения схемы БД, в котором описываются строковые константы для определения таблицы
 */
public class CowDbSchema {
    public static final class CowTable {
        public static final String NAME="cows";

        /**
         *Определения столбцов таблицы
         */
        public static final class Cols {
            public static final String UUID="uuid";
            public static final String TAG_NUMBER="tag";
            public static final String BREED="breed";
            public static final String COLOR="color";
            public static final String DATE_OF_BIRTH="date";
            public static final String FATHER="father";
            public static final String MOTHER="mother";
        }
    }
}
