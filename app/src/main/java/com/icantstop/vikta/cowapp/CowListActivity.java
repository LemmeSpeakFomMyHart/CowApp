package com.icantstop.vikta.cowapp;

import android.support.v4.app.Fragment;

/**
 *Класс, предназначенный для хостинга CowListFragment
 */
public class CowListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CowListFragment();
    }
}
