package myoo.votingapp.view.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import myoo.votingapp.view.Adapter.ClubDetailsPagerAdapter;
import myoo.votingapp.R;


/**
 * A login screen that offers login via email/password.
 */
public class ClubDetailsFragment extends Fragment {


    public static ClubDetailsFragment newInstance() {
        ClubDetailsFragment feedsFragment = new ClubDetailsFragment();
        return feedsFragment;
    }


    View rootView;
    ViewPager viewPager;
    TextView txt_header;
    TabLayout tabLayout;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_club_details_page, container, false);

        txt_header = (TextView) rootView.findViewById(R.id.txt_header);
        txt_header.setText("Club Details");

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_club_details);
        tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs_club_details);

        setFragments();

        return rootView;
    }

    void setFragments(){
        viewPager.setAdapter(new ClubDetailsPagerAdapter(getActivity().getSupportFragmentManager(),
                getActivity() ));

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }



}

