package gcm.play.android.samples.com.gcmquickstart;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * Created by dk on 7/6/2015.
 */
public class Contacts_Activity extends ActionBarActivity implements ActionBar.TabListener{

    ViewPager mViewPager;
    AppPageAdapter appPageadapter;
    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_activity);

        Log.d("action bar ", "" + getSupportActionBar());
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        appPageadapter = new AppPageAdapter(getSupportFragmentManager());
        Log.d("Contacts Activity", " " + appPageadapter);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        Log.d(TAG,"miewpager "+mViewPager);
        mViewPager.setAdapter(appPageadapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
//                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < appPageadapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(appPageadapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
