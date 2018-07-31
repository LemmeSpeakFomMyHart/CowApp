package com.icantstop.vikta.cowapp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *Класс модели метрики
 */
public class Measurement implements Serializable {
    private UUID mId;
    private int mTagNumber;
    private Date mDate;
    private float mYield;
    private float mFatContent;
    private float mWeight;

    public Measurement(){
        this(UUID.randomUUID());
    }

    public Measurement(UUID id){
        mId=id;
        mDate=new Date();
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public float getYield() {
        return mYield;
    }

    public void setYield(float yield) {
        mYield = yield;
    }

    public float getFatContent() {
        return mFatContent;
    }

    public void setFatContent(float fatContent) {
        mFatContent = fatContent;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }
}
