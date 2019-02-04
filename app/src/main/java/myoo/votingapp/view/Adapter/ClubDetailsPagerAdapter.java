package myoo.votingapp.view.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import myoo.votingapp.view.Fragment.ListOfClubMembersFragment;

/**
 * Created by MA294214 on 6/22/2017.
 */

public class ClubDetailsPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Members", "Guests " };
    private Context context;




    public ClubDetailsPagerAdapter(FragmentManager fm, Context context ) {
        super(fm);
        this.context = context;
    }



    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return ListOfClubMembersFragment.Companion.newInstance(position + 1  );
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
