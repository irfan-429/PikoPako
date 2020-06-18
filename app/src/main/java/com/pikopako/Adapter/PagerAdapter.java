package com.pikopako.Adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pikopako.Fragment.RestroInfoServices;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RestroInfoServices tab1 = new RestroInfoServices();
                return tab1;

//            case 1:
//                Tab tab2 = new Tab();
//                return tab2;
//            case 2:
//                Tab3 tab3 = new Tab3();
//                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}