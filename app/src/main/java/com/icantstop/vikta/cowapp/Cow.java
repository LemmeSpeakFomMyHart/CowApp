package com.icantstop.vikta.cowapp;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 *Класс модели коровы
 */
public class Cow {

    private UUID mId;
    private int mTagNumber;
    private String mBreed;
    private String mColor;
    private String mAge;
    private String mFather;
    private String mMother;
    private Date mDateOfBirth;

    public Cow() {
        this(UUID.randomUUID());
    }

    public Cow(UUID id){
        mId=id;
        mDateOfBirth=new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public int getTagNumber() {
        return mTagNumber;
    }

    public void setTagNumber(int tagNumber) {
        mTagNumber = tagNumber;
    }

    public String getBreed() {
        return mBreed;
    }

    public void setBreed(String breed) {
        mBreed = breed;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }


    public String getFather() {
        return mFather;
    }

    public void setFather(String father) {
        mFather = father;
    }

    public String getMother() {
        return mMother;
    }

    public void setMother(String mother) {
        mMother = mother;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    /**
     *Метод возвращает возраст коровы
     */
    public String getAge() {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        int ageYear = 0;
        int ageMonth = 0;
        int ageDays = 0;

        dob.setTime(mDateOfBirth);
        int dobYear = dob.get(Calendar.YEAR);
        int dobMonth = dob.get(Calendar.MONTH);
        int dobDay = dob.get(Calendar.DAY_OF_MONTH);

        if (today.get(Calendar.YEAR) > dobYear) {
            /**
             *текущий год > года рождения
             */
            ageYear = today.get(Calendar.YEAR) - dobYear;

            if (today.get(Calendar.MONTH) > dobMonth) {
                /**
                 * текущий год > года рождения и текущий месяц > месяца рождения
                 */
                ageMonth = today.get(Calendar.MONTH) - dobMonth;

                if (today.get(Calendar.DAY_OF_MONTH) > dobDay) {
                    /**
                     * текущий год > года рождения и текущий месяц > месяца рождения и текущий день > дня рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                } else if (today.get(Calendar.DAY_OF_MONTH) < dobDay) {
                    /**
                     * текущий год > года рождения и текущий месяц > месяца рождения и текущий день < дня рождения
                     */
                    if (ageMonth > 0) {
                        ageMonth--;
                    }
                    int preMonthTotalDays = today.getActualMaximum(Calendar.DAY_OF_MONTH - 1);
                    int preMonthCountDays = preMonthTotalDays - dobDay;
                    ageDays = preMonthCountDays + today.get(Calendar.DAY_OF_MONTH);

                } else {
                    /**
                     * текущий год > года рождения и текущий месяц > месяца рождения и текущий день = дню рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                }

            } else if (today.get(Calendar.MONTH) < dobMonth) {
                /**
                 * текущий год > года рождения и текущий месяц < месяца рождения
                 */
                if (ageYear > 0) {
                    ageYear--;
                }
                int preYearCompletedMonth = 11 - dobMonth;
                int currentYearCompleteMonth = today.get(Calendar.MONTH);
                ageMonth = preYearCompletedMonth + currentYearCompleteMonth;

                if (today.get(Calendar.DAY_OF_MONTH) > dobDay) {
                    /**
                     * текущий год > года рождения и текущий месяц < месяца рождения и текущий день > дня рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                } else if (today.get(Calendar.DAY_OF_MONTH) < dobDay) {
                    /**
                     * текущий год > года рождения и текущий месяц < месяца рождения и текущий день < дня рождения
                     */
                    if (Calendar.MONTH == 2) {
                        /**
                         * предыдущий месяц февраль и день рождение 30-го января
                         */
                        ageDays = today.get(Calendar.DAY_OF_MONTH);
                    } else {
                        /**
                         * предыдущий месяц не февраль
                         */
                        if (ageMonth > 0) {
                            ageMonth--;
                        }
                        int preMonthTotalDays = today.getActualMaximum(Calendar.DAY_OF_MONTH - 1);
                        int preMonthCountDays = preMonthTotalDays - dobDay;
                        ageDays = preMonthCountDays + today.get(Calendar.DAY_OF_MONTH);
                    }
                } else {
                    /**
                     * текущий год > года рождения и текущий месяц < месяца рождения и текущий день = дню рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                }
            } else {
                /**
                 * текущий год > года рождения и текущий месяц = месяцу рождения
                 */
                ageMonth = today.get(Calendar.MONTH) - dobMonth;

                if (today.get(Calendar.DAY_OF_MONTH) > dobDay) {
                    /**
                     * текущий год > года рождения и текущий месяц = месяцу рождения и текущий день > дня рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                } else if (today.get(Calendar.DAY_OF_MONTH) < dobDay) {
                    /**
                     * текущий год > года рождения и текущий месяц = месяцу рождения и текущий день < дня рождения
                     */
                    if (ageYear > 0) {
                        ageYear--;
                    }
                    ageMonth = 11;
                    int preMonthTotalDays = today.getActualMaximum(Calendar.DAY_OF_MONTH - 1);
                    int preMonthCountDays = preMonthTotalDays - dobDay;
                    ageDays = preMonthCountDays + today.get(Calendar.DAY_OF_MONTH);
                } else {
                    /**
                     * текущий год > года рождения и текущий месяц = месяцу рождения и текущий день = дню рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                }
            }
        } else if (today.get(Calendar.YEAR) == dobYear) {
            /**
             * Тот же год
             */
            ageYear = today.get(Calendar.YEAR) - dobYear;

            if (today.get(Calendar.MONTH) > dobMonth) {
                /**
                 * Тот же год и текущий месяц > месяца рождения
                 */
                ageMonth = today.get(Calendar.MONTH) - dobMonth;

                if (today.get(Calendar.DAY_OF_MONTH) > dobDay) {
                    /**
                     * Тот же год и текущий месяц > месяца рождения и текущий день > дня рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                } else if (today.get(Calendar.DAY_OF_MONTH) < dobDay) {
                    /**
                     * Тот же год и текущий месяц > месяца рождения и текущий день < дня рождения
                     */
                    if (ageMonth > 0) {
                        ageMonth--;
                    }

                    if (Calendar.MONTH == 2) {
                        /**
                         * предыдущий месяц февраль и день рождение 30-го января
                         */
                        ageDays = today.get(Calendar.DAY_OF_MONTH);
                    } else {
                        /**
                         * предыдущий месяц не февраль
                         */
                        int preMonthTotalDays = today.getActualMaximum(Calendar.DAY_OF_MONTH - 1);
                        int preMonthCountDays = preMonthTotalDays - dobDay;
                        ageDays = preMonthCountDays + today.get(Calendar.DAY_OF_MONTH);
                    }
                } else {
                    /**
                     * Тот же год и текущий месяц > месяца рождения и текущий день = дню рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                }
            } else {
                /**
                 * Тот же год и тот же месяц
                 */
                ageMonth = today.get(Calendar.MONTH) - dobMonth;

                if (today.get(Calendar.DAY_OF_MONTH) > dobDay) {
                    /**
                     * Тот же год и тот же месяц и текущий день > дня рождения
                     */
                    ageDays = today.get(Calendar.DAY_OF_MONTH) - dobDay;
                } else {
                    /**
                     * Тот же год и тот же месяц и текущий день = дню рождения
                     */
                    ageDays=today.get(Calendar.DAY_OF_MONTH)-dobDay;
                }
            }
        }

        String mAge=ageYear+" г "+ageMonth+" м "+ageDays+" д" ;

        return mAge;
    }
}
