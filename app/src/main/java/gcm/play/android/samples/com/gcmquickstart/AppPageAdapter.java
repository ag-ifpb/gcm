package gcm.play.android.samples.com.gcmquickstart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dk on 7/6/2015.
 */
public class AppPageAdapter extends FragmentPagerAdapter {

    String[] tabs = {"Contacts","Chats"};

    public AppPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                //first tab
                return new Friends();
            default:
                //none selected
                // The other sections of the app are dummy placeholders.
                Fragment fragment = new DummyFragment();
                Bundle args = new Bundle();
                args.putInt(DummyFragment.ARG_SECTION_NUMBER, position + 1);
                fragment.setArguments(args);
                return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);
//        return " Section "+(position+1);
        return tabs[position];
    }
}
