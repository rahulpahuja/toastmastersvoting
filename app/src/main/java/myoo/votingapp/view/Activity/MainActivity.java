package myoo.votingapp.view.Activity;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import myoo.votingapp.view.Fragment.ClubDetailsFragment;
import myoo.votingapp.view.Fragment.CreateMeetingOrJoinToVoteFragment;
import myoo.votingapp.view.Fragment.ListOfAllMeetingFragment;

import myoo.votingapp.view.Fragment.MoreFragment;
import myoo.votingapp.R;
import myoo.votingapp.viewmodel.MeetingCandidatesViewModel;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    ViewPager viewPager;
    private ListOfAllMeetingFragment meetingFragment = null;
    private ClubDetailsFragment clubFragment = null;
    private MoreFragment moreFragment = null;
    private CreateMeetingOrJoinToVoteFragment joinAMeeting = null ;

    private MeetingCandidatesViewModel meetingCandidatesViewModel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

       // meetingCandidatesViewModel = ViewModelProviders.of(this).get(MeetingCandidatesViewModel.class);
        init();

    }

        public void init() {
        viewPager =  findViewById(R.id.viewpager);

        this.meetingFragment = new ListOfAllMeetingFragment();
        this.clubFragment = ClubDetailsFragment.newInstance();
        this.moreFragment = MoreFragment.newInstance();
        this.joinAMeeting = new CreateMeetingOrJoinToVoteFragment();

        setupViewPagerMap(viewPager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        this.setupTabIcons();


    }

    public void swipeToMyMeetings(View v){
        viewPager.setCurrentItem(1);
    }



    public void enterMeetingRoom(View v) {

        joinAMeeting.SelectOptionToJoin();

/*
        AlertDialog.Builder dialogue = new AlertDialog.Builder(this);
        dialogue.setTitle(" Enter the meeting oode to join");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        dialogue.setView(input);

        dialogue.setPositiveButton("Enter ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String text = input.getText().toString();
                if(text.equals("")){
                    return;
                }

                meetingCandidatesViewModel.getMeetingStatus(text).subscribe(
                        mybool ->{
                            if(mybool){

                                Intent intent = new Intent(MainActivity.this, JoinToVote.class);
                                intent.putExtra("MeetingNo" ,text);
                                startActivity(intent);
                                dialog.dismiss();
                            }

                        }
                );




            }
        });

        dialogue.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }
        });

        AlertDialog myalertobject = dialogue.create();
        //Show the dialog
        myalertobject.show();

        */


    }
    void showMyMeetings(){


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if (grantResults.length==0  || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Log.i("mytag", "Permission has been denied by user");
            }else{

                ((CreateMeetingOrJoinToVoteFragment)joinAMeeting).nearbyMeetings();
            }

        }
    }


    private void setupViewPagerMap(ViewPager viewPager2) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(this.joinAMeeting , "Join A meeting");
        adapter.addFrag(this.meetingFragment, "My Meeting");
        adapter.addFrag(this.clubFragment, "Club");
        adapter.addFrag(this.moreFragment, "More");

        viewPager2.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void setFrag(int postition , Fragment fragment){
            mFragmentList.set(postition , fragment);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupTabIcons() {
        TextView textJoinToVote = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, (RelativeLayout) findViewById(R.id.rlMain), false);
        textJoinToVote.setText("Vote");
        Drawable img_join_to_vote = getDrawablee(R.drawable.tab_vote);
        img_join_to_vote.setBounds(0, 0, 80, 80);
        textJoinToVote.setCompoundDrawables(null, img_join_to_vote, null, null);
        tabLayout.getTabAt(0).setCustomView(textJoinToVote);

        TextView tabFeeds = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, (RelativeLayout) findViewById(R.id.rlMain), false);
        tabFeeds.setText("Meeting");
        Drawable img = getDrawablee( R.drawable.tab_meeting);
        img.setBounds(0, 0, 80, 80);
        tabFeeds.setCompoundDrawables(null, img, null, null);
        tabLayout.getTabAt(1).setCustomView(tabFeeds);

        TextView tabFavorite = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, (RelativeLayout) findViewById(R.id.rlMain), false);
        tabFavorite.setText("Club");
        Drawable img1 = getDrawablee(R.drawable.tab_club);
        img1.setBounds(0, 0, 80, 80);
        tabFavorite.setCompoundDrawables(null, img1, null, null);
        tabLayout.getTabAt(2).setCustomView(tabFavorite);


        TextView tabMessae = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, (RelativeLayout) findViewById(R.id.rlMain), false);
        tabMessae.setText("More");
        Drawable img3 = getDrawablee(R.drawable.tab_more);
        img3.setBounds(0, 0, 80, 80);
        tabMessae.setCompoundDrawables(null, img3, null, null);
        tabLayout.getTabAt(3).setCustomView(tabMessae);

    }

    private Drawable getDrawablee(int tab_club) {
        return AppCompatResources.getDrawable(this, tab_club);
    }


}
