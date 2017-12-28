package boyko.alex.easy_way.frontend.bookings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 27.12.2017.
 * <p>
 * This is adapter for 2 pages - My bookings and Bookings to my offers
 */

class BookingsViewPagerAdapter extends FragmentPagerAdapter {
    BookingsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            Bundle bundle = new Bundle();
            bundle.putInt("mode", BookingsViewFragment.MODE_MY_BOOKINGS);
            fragment = new BookingsViewFragment();
            fragment.setArguments(bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("mode", BookingsViewFragment.MODE_BOOKINGS_TO_MY_OFFERS);
            fragment = new BookingsViewFragment();
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return ApplicationController.getInstance().getString(R.string.my_bookings);
        } else {
            return ApplicationController.getInstance().getString(R.string.my_offers_bookings);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
