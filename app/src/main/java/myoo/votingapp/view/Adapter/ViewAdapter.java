package myoo.votingapp.view.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import myoo.votingapp.view.Fragment.EnterOtp;
import myoo.votingapp.view.Fragment.Phone;


public class ViewAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments = new Fragment[2];

    public ViewAdapter(FragmentManager fm) {
        super(fm);

        fragments[0] = new Phone();
        fragments[1] = new EnterOtp();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}

