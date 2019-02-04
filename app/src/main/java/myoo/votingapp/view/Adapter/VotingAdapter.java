package myoo.votingapp.view.Adapter;

import android.content.Context;

import java.util.ArrayList;



public class VotingAdapter {

    public CustomArrayAdapter adapter;

    public VotingAdapter(Context context, ArrayList<String> names_list,
                         ArrayList<String> title_list, ArrayList<String> project_list, ArrayList<String> photo_list, String voting_item,ArrayList<Boolean> disabled_list,Boolean myMeeting) {
        adapter = new CustomArrayAdapter(context, names_list, title_list, project_list, photo_list, voting_item,disabled_list,myMeeting);
        adapter.is_it_the_voting_process = true;
    }

}
