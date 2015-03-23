package progsoul.opendata.leccebybike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.fragments.BikeSharingStationsListFragment;
import progsoul.opendata.leccebybike.fragments.BikeSharingStationsMapFragment;
import progsoul.opendata.leccebybike.libs.residemenu.ResideMenu;
import progsoul.opendata.leccebybike.libs.residemenu.ResideMenuItem;
import progsoul.opendata.leccebybike.utils.Constants;

public class MainActivity extends FragmentActivity {
    private ResideMenu resideMenu;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpMenu();
        if (savedInstanceState == null)
            changeFragment(new BikeSharingStationsMapFragment());
    }

    private void setUpMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        addResideMenuItems();

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.click_animation));

                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
    }

    /**
     * called to add ResideMenuItem to the main ResideMenu
     */
    private void addResideMenuItems() {
        String[] fragmentTitles = getResources().getStringArray(R.array.fragment_titles);
        String[] fragmentTags = getResources().getStringArray(R.array.fragment_tags);
        String[] fragmentSubTitles = getResources().getStringArray(R.array.fragment_sub_titles);

        for (int i = 0; i < fragmentTitles.length; i++) {
            ResideMenuItem resideMenuItem = new ResideMenuItem(this, fragmentSubTitles[i], fragmentTitles[i]);
            resideMenuItem.setItemTag(fragmentTags[i]);
            resideMenuItem.setOnClickListener(resideMenuItemClickListener);
            resideMenu.addMenuItem(resideMenuItem, ResideMenu.DIRECTION_LEFT);
        }
    }

    private View.OnClickListener resideMenuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.click_animation));

            ResideMenuItem resideMenuItem = (ResideMenuItem) v;
            switch(resideMenuItem.getItemTag()) {
                case Constants.STATIONS_LIST_FRAGMENT_TAG:
                    changeFragment(new BikeSharingStationsListFragment());
                    break;
                case Constants.STATIONS_MAP_FRAGMENT_TAG:
                    changeFragment(new BikeSharingStationsMapFragment());
                    break;
            }

            resideMenu.closeMenu();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result", "Back on Main Activity with result code: " + resultCode);
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu() {
        return resideMenu;
    }
}
