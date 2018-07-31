package com.icantstop.vikta.cowapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 *Класс, предназначенный для хостинга CowFragment, использующий виждет ViewPager,
 * к-ый позволяет "листать" элементы списка, проводя пальцем по экрану
 */
public class CowPagerActivity extends AppCompatActivity {

    private static final String EXTRA_COW_ID="com.icantstop.android.cowapp.cow_id";

    private ViewPager mViewPager;
    private List<Cow> mCows;

    public static Intent newIntent(Context packageContext, UUID cowId){
        Intent intent=new Intent(packageContext,CowPagerActivity.class);
        intent.putExtra(EXTRA_COW_ID, cowId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_pager);

        UUID cowId=(UUID) getIntent().getSerializableExtra(EXTRA_COW_ID);

        mViewPager= findViewById(R.id.activity_cow_pager_view_pager);

        mCows=CowLab.get(this).getCows();
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Cow cow=mCows.get(position);
                return CowFragment.newInstance(cow.getId());
            }

            @Override
            public int getCount() {
                return mCows.size();
            }
        });

        for (int i=0;i<mCows.size();i++){
            if (mCows.get(i).getId().equals(cowId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
