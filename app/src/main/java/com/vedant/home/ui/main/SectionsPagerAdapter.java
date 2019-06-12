package com.vedant.home.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vedant.home.R;
import com.vedant.home.fragments.settings.SettingFragment;
import com.vedant.home.fragments.things.ThingFragment;
import com.vedant.home.helpers.AppData;
import com.vedant.home.helpers.PahoMQTTClient;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private PahoMQTTClient pahoMQTTClient = null;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public SectionsPagerAdapter(Context context, FragmentManager fm, PahoMQTTClient mqttClient) {
        super(fm);
        mContext = context;
        pahoMQTTClient = mqttClient;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = SettingFragment.newInstance(1);
        switch (position){
            case 0:
                fragment = ThingFragment.newInstance(1, AppData.ROOMS_KEY);
                break;
            case 1:
                fragment = ThingFragment.newInstance(2, AppData.ROOM_ID);
                break;
            case 2:
                fragment = SettingFragment.newInstance(1);
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}