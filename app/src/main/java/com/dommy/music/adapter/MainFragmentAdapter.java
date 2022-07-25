package com.dommy.music.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dommy.music.DiskFragment;
import com.dommy.music.LyricFragment;

/**
 * 主界面Fragment适配器
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    public MainFragmentAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = new DiskFragment();
                break;
            case 1:
                fragment = new LyricFragment();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
