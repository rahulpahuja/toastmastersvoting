package myoo.votingapp.view.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import myoo.votingapp.view.Activity.ResultActivity;
import myoo.votingapp.view.Adapter.AdapterForShowingVotes;
import myoo.votingapp.Model.EvaluatorDetail;
import myoo.votingapp.Model.NamesAndVotes;
import myoo.votingapp.Model.PreparedSpeakerDetail;
import myoo.votingapp.Model.RoleTakerDetail;
import myoo.votingapp.Model.TableTopicDetail;
import myoo.votingapp.R;

/**
 * Created by MA294214 on 4/30/2017.
 */

public class VotingResultFragemnt extends Fragment {

    public static final String ARG_PAGE = "page_number";
    public static String meeting_number;
   static ArrayList<String> prepared_speaker_name_list = new ArrayList<>();
   static ArrayList<String> role_taker_name_list = new ArrayList<>();
    static ArrayList<String> table_topic_name_list = new ArrayList<>();
    static ArrayList<String> evaluators_name_list = new ArrayList<>();
    static ArrayList<Integer> prepared_speaker_votes_list = new ArrayList<>();
    static ArrayList<Integer> role_taker_votes_list = new ArrayList<>();
    static ArrayList<Integer> table_topic_speaker_votes_list = new ArrayList<>();
    static ArrayList<Integer> evaluators_votes_list = new ArrayList<>();
    static HashMap<String ,ArrayList<Object>> cadidates_map = new HashMap<>();
    static  HashMap<String ,ArrayList<Integer>> votes_map = new HashMap<>();
    static HashMap<String ,ArrayList<String>> names_map = new HashMap<>();
   static ArrayList<String> voting_item_list= new ArrayList<>();
    static NamesAndVotes namesAndVotes;
    View view;
    String best_prepared_speaker, best_role_taker, best_table_topic_speaker, best_evaulator;
    Bitmap best_prep_speaker_photo, best_role_taker_photo, best_table_topic_photo, best_evaluator_photo;
    private int mPage;

    public static void setMeeting_number(String meeting_number) {
        VotingResultFragemnt.meeting_number = meeting_number;
    }

    public static void setNamesAndVotes(NamesAndVotes namesAndVotes) {
        VotingResultFragemnt.namesAndVotes = namesAndVotes;

        prepared_speaker_name_list = namesAndVotes.prepared_speaker_name_list;
        role_taker_name_list = namesAndVotes.role_taker_name_list;
       table_topic_name_list = namesAndVotes.table_topic_name_list;
        evaluators_name_list = namesAndVotes.evaluators_name_list;
        prepared_speaker_votes_list = namesAndVotes.prepared_speaker_votes_list;
        role_taker_votes_list = namesAndVotes.role_taker_votes_list;
        table_topic_speaker_votes_list = namesAndVotes.table_topic_speaker_votes_list;
        evaluators_votes_list = namesAndVotes.evaluators_votes_list;
    }

    public static void setCandidatesAndVotesMap(HashMap candidates , HashMap votes ,ArrayList<String> voting_item_list ) {

        cadidates_map = candidates ;
        votes_map = votes;

        VotingResultFragemnt.voting_item_list = voting_item_list;

        for(int i =0;i<voting_item_list.size();i++){
            String voting_item = voting_item_list.get(i);

            ArrayList<Object> candidate_list = cadidates_map.get(voting_item);
            ArrayList<String> name_list = new ArrayList<>();

          if(i == 0){
              for(int j = 0;j< candidate_list.size();j++){
                  PreparedSpeakerDetail detail = (PreparedSpeakerDetail) candidate_list.get(j);
                  name_list.add(detail.getName());
              }
              names_map.put(voting_item ,name_list);
          }
            if(i == 1){
                for(int j = 0;j< candidate_list.size();j++){
                    RoleTakerDetail detail = (RoleTakerDetail) candidate_list.get(j);
                    name_list.add(detail.getName());
                }
                names_map.put(voting_item ,name_list);
            } if(i == 2){
                for(int j = 0;j< candidate_list.size();j++){
                    TableTopicDetail detail = (TableTopicDetail) candidate_list.get(j);
                    name_list.add(detail.getName());
                }
                names_map.put(voting_item ,name_list);
            } if(i == 3){
                for(int j = 0;j< candidate_list.size();j++){
                    EvaluatorDetail detail = (EvaluatorDetail) candidate_list.get(j);
                    name_list.add(detail.getName());
                }
                names_map.put(voting_item ,name_list);
            }

        }

    }

    public static VotingResultFragemnt newInstance(int page  ) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        VotingResultFragemnt fragment = new VotingResultFragemnt();
        fragment.setArguments(args);

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        Log.d("checkfragment" ,"in on create of fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("checkfragment" ,"in on createview  of fragment");
        switch (mPage){

            case 1:
                view = inflater.inflate(R.layout.activity_result_screen, container, false);

                break;

            case 2:
            case 3:
            case 4:
            case 5:
                view = inflater.inflate(R.layout.activity_list_of_candidates, container, false);

                break;

        }

        initData();

        return view;
    }

   public void initData(){
        if (!cadidates_map.isEmpty()) {
            if (mPage == 1) {
                setWinnersFragment();
            } else {
                setVotesFragment(mPage);
            }
        }

    }

    public void shareIt(String message) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");


        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WInner results");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    void setWinnersFragment(){

        prepared_speaker_name_list = names_map.get(VotingResultFragemnt.voting_item_list.get(0));
        role_taker_name_list = names_map.get(VotingResultFragemnt.voting_item_list.get(1));
        table_topic_name_list = names_map.get(VotingResultFragemnt.voting_item_list.get(2));
        evaluators_name_list = names_map.get(VotingResultFragemnt.voting_item_list.get(3));

        prepared_speaker_votes_list = votes_map.get(VotingResultFragemnt.voting_item_list.get(0));
        role_taker_votes_list = votes_map.get(VotingResultFragemnt.voting_item_list.get(1));
        table_topic_speaker_votes_list = votes_map.get(VotingResultFragemnt.voting_item_list.get(2));
        evaluators_votes_list = votes_map.get(VotingResultFragemnt.voting_item_list.get(3));

        int winner_index = -1;
             try{
                 if(prepared_speaker_votes_list.size() >0){
                     winner_index = getIndexOfMaxVotes(prepared_speaker_votes_list);
                     if(winner_index== -2)
                         view.findViewById(R.id.best_prep_speakerTie).setVisibility(View.VISIBLE);

                     view.findViewById(R.id.best_prep_speakerTie).findViewById(R.id.tapHere).setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Log.d("mytag" , "in onClick " );
                             ((ResultActivity) getActivity()).swipeToPreparedSpeakersVotes();
                         }
                     });

                     best_prepared_speaker = (winner_index >-1)?prepared_speaker_name_list.get(winner_index):null;
                 }
                 if(table_topic_speaker_votes_list.size() >0){
                     winner_index = getIndexOfMaxVotes(table_topic_speaker_votes_list);
                     if(winner_index== -2)
                         view.findViewById(R.id.TieForTT).setVisibility(View.VISIBLE);

                     view.findViewById(R.id.TieForTT).findViewById(R.id.tapHere).setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Log.d("mytag" , "in onClick " );
                             ((ResultActivity) getActivity()).swipeToTTVotes();
                         }
                     });

                     best_table_topic_speaker = (winner_index >-1)?table_topic_name_list.get(winner_index):null;
                 }

                 if(role_taker_votes_list.size() >0){
                     winner_index = getIndexOfMaxVotes(role_taker_votes_list);
                     if(winner_index== -2)
                         view.findViewById(R.id.best_role_taker_Tie).setVisibility(View.VISIBLE);

                     view.findViewById(R.id.best_role_taker_Tie).findViewById(R.id.tapHere).setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Log.d("mytag" , "in onClick " );
                             ((ResultActivity) getActivity()).swipeToRoletakersVotes();
                         }
                     });
                     best_role_taker = (winner_index >-1)?role_taker_name_list.get(winner_index):null;
                 }

                 if(evaluators_votes_list.size() >0){
                     winner_index = getIndexOfMaxVotes(evaluators_votes_list);

                     if(winner_index== -2)
                         view.findViewById(R.id.best_evaluator_Tie).setVisibility(View.VISIBLE);

                     view.findViewById(R.id.best_evaluator_Tie).findViewById(R.id.tapHere).setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Log.d("mytag" , "in onClick " );
                             ((ResultActivity) getActivity()).swipeToEvaluatorsVotes();
                         }
                     });

                     best_evaulator = (winner_index >-1)?evaluators_name_list.get(winner_index):null;
                 }



             }catch (ArrayIndexOutOfBoundsException e){
                 Log.d("checkresult" ,"Array index out of bound");
                 e.printStackTrace();
             }


        best_prep_speaker_photo = getBitmap(best_prepared_speaker,(getActivity().getResources().getText(R.string.prepared_speaker)).toString());
        best_role_taker_photo = getBitmap(best_role_taker,(getActivity().getResources().getText(R.string.role_takers)).toString());
        best_table_topic_photo = getBitmap(best_table_topic_speaker,(getActivity().getResources().getText(R.string.table_topics)).toString());
        best_evaluator_photo = getBitmap(best_evaulator,(getActivity().getResources().getText(R.string.evaluators)).toString());

        LinearLayout prep_speaker_layout = (LinearLayout) view.findViewById(R.id.best_prep_speaker);
        LinearLayout role_taker_layout = (LinearLayout) view.findViewById(R.id.best_role_taker);
        LinearLayout table_topic_layout = (LinearLayout) view.findViewById(R.id.best_table_topic);
        LinearLayout evaluator_layout = (LinearLayout) view.findViewById(R.id.best_evaluator);

        ImageView share_icon = (ImageView) view.findViewById(R.id.share);

        String result_msg_to_share = ("Meeting number: " +meeting_number + "\n"
                +"Best prepared Speaker: " + best_prepared_speaker +"\n"
                + "Best Table Topic speaker: " + best_table_topic_speaker +"\n"
                + "Best Role Taker: " + best_role_taker +"\n"
                + "Best Evaluator: " + best_evaulator );

        result_msg_to_share+= (best_prepared_speaker!= null)?"Best prepared Speaker: " + best_prepared_speaker +"\n" :"";
        result_msg_to_share+= (best_role_taker!= null)?"Best Role Taker: " + best_role_taker +"\n" :"";
        result_msg_to_share+= (best_table_topic_speaker!= null)?"Best Table Topic speaker: " + best_table_topic_speaker +"\n" + best_role_taker +"\n" :"";
        result_msg_to_share+= (best_evaulator!= null)?"Best Evaluator: " + best_evaulator :"";

        final String result_msg = result_msg_to_share;
        share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(result_msg);
            }
        });

        ((TextView)prep_speaker_layout.findViewById(R.id.name_of_cadidate)).setText((best_prepared_speaker!=null)?best_prepared_speaker:"!! None!!");
        ((ImageView)prep_speaker_layout.findViewById(R.id.photo)).setImageBitmap(best_prep_speaker_photo);

        ((TextView)role_taker_layout.findViewById(R.id.name_of_cadidate)).setText((best_role_taker!=null)?best_role_taker:"!! None!!");
        ((ImageView)role_taker_layout.findViewById(R.id.photo)).setImageBitmap(best_role_taker_photo);

        ((TextView)table_topic_layout.findViewById(R.id.name_of_cadidate)).setText((best_table_topic_speaker!=null)?best_table_topic_speaker:"!! None!!");
        ((ImageView)table_topic_layout.findViewById(R.id.photo)).setImageBitmap(best_table_topic_photo);

        ((TextView)evaluator_layout.findViewById(R.id.name_of_cadidate)).setText((best_evaulator!=null)?best_evaulator:"!! None!!");
        ((ImageView)evaluator_layout.findViewById(R.id.photo)).setImageBitmap(best_evaluator_photo);


    }

    void setWinnersFragmentFromMap(){

        try{
            if(prepared_speaker_votes_list.size() >0)
                best_prepared_speaker = prepared_speaker_name_list.get(getIndexOfMaxVotes(prepared_speaker_votes_list));
            if(table_topic_speaker_votes_list.size() >0)
                best_table_topic_speaker = table_topic_name_list.get(getIndexOfMaxVotes(table_topic_speaker_votes_list));
            if(role_taker_votes_list.size() >0)
                best_role_taker = role_taker_name_list.get(getIndexOfMaxVotes(role_taker_votes_list));
            if(evaluators_votes_list.size() >0)
                best_evaulator = evaluators_name_list.get(getIndexOfMaxVotes(evaluators_votes_list));

        }catch (ArrayIndexOutOfBoundsException e){

            e.printStackTrace();
        }


        best_prep_speaker_photo = getBitmap(best_prepared_speaker,(getActivity().getResources().getText(R.string.prepared_speaker)).toString());
        best_role_taker_photo = getBitmap(best_role_taker,(getActivity().getResources().getText(R.string.role_takers)).toString());
        best_table_topic_photo = getBitmap(best_table_topic_speaker,(getActivity().getResources().getText(R.string.table_topics)).toString());
        best_evaluator_photo = getBitmap(best_evaulator,(getActivity().getResources().getText(R.string.evaluators)).toString());

        LinearLayout prep_speaker_layout = (LinearLayout) view.findViewById(R.id.best_prep_speaker);
        LinearLayout role_taker_layout = (LinearLayout) view.findViewById(R.id.best_role_taker);
        LinearLayout table_topic_layout = (LinearLayout) view.findViewById(R.id.best_table_topic);
        LinearLayout evaluator_layout = (LinearLayout) view.findViewById(R.id.best_evaluator);

        ImageView share_icon = (ImageView) view.findViewById(R.id.share);

        String result_msg_to_share = ("Meeting number: " +meeting_number + "\n"
                +"Best prepared Speaker: " + best_prepared_speaker +"\n"
                + "Best Table Topic speaker: " + best_table_topic_speaker +"\n"
                + "Best Role Taker: " + best_role_taker +"\n"
                + "Best Evaluator: " + best_evaulator );

        result_msg_to_share+= (best_prepared_speaker!= null)?"Best prepared Speaker: " + best_prepared_speaker +"\n" :"";
        result_msg_to_share+= (best_role_taker!= null)?"Best Role Taker: " + best_role_taker +"\n" :"";
        result_msg_to_share+= (best_table_topic_speaker!= null)?"Best Table Topic speaker: " + best_table_topic_speaker +"\n" + best_role_taker +"\n" :"";
        result_msg_to_share+= (best_evaulator!= null)?"Best Evaluator: " + best_evaulator :"";

        final String result_msg = result_msg_to_share;
        share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt(result_msg);
            }
        });

        ((TextView)prep_speaker_layout.findViewById(R.id.name_of_cadidate)).setText((best_prepared_speaker!=null)?best_prepared_speaker:"!! None!!");
        ((ImageView)prep_speaker_layout.findViewById(R.id.photo)).setImageBitmap(best_prep_speaker_photo);

        ((TextView)role_taker_layout.findViewById(R.id.name_of_cadidate)).setText((best_role_taker!=null)?best_role_taker:"!! None!!");
        ((ImageView)role_taker_layout.findViewById(R.id.photo)).setImageBitmap(best_role_taker_photo);

        ((TextView)table_topic_layout.findViewById(R.id.name_of_cadidate)).setText((best_table_topic_speaker!=null)?best_table_topic_speaker:"!! None!!");
        ((ImageView)table_topic_layout.findViewById(R.id.photo)).setImageBitmap(best_table_topic_photo);

        ((TextView)evaluator_layout.findViewById(R.id.name_of_cadidate)).setText((best_evaulator!=null)?best_evaulator:"!! None!!");
        ((ImageView)evaluator_layout.findViewById(R.id.photo)).setImageBitmap(best_evaluator_photo);


    }


    void setVotesFragment(int fragment_numbner){

        ListView list = (ListView) view.findViewById(R.id.listview);
        view.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        View heading = (View) view.findViewById(R.id.header) ;
        heading.setVisibility(View.GONE);


        switch (fragment_numbner){

            case 2:
                setPreparedSpeakerList(list);
                break;

            case 3:
                setRoleTakerList(list);
                break;

            case 4:
                setTableTopicList(list);
                break;

            case 5:
                setEvaluatorList(list);
                break;
        }

    }

    void setPreparedSpeakerList(ListView list){

        String voting_item = VotingResultFragemnt.voting_item_list.get(0);
        ArrayList<Object> candidate_list = cadidates_map.get(voting_item);
        ArrayList<String> name_list = new ArrayList<>();
        ArrayList<Integer> votes_list = votes_map.get(voting_item);
        for(int i = 0;i< candidate_list.size();i++){
            PreparedSpeakerDetail detail = (PreparedSpeakerDetail) candidate_list.get(i);
            name_list.add(detail.getName());
        }

        AdapterForShowingVotes adapter = new AdapterForShowingVotes(getActivity(),name_list,votes_list);
        list.setAdapter(adapter);
    }

    void setRoleTakerList(ListView list){

        String voting_item = VotingResultFragemnt.voting_item_list.get(1);
        ArrayList<Object> candidate_list = cadidates_map.get(voting_item);
        ArrayList<String> name_list = new ArrayList<>();
        ArrayList<Integer> votes_list = votes_map.get(voting_item);
        for(int i = 0;i< candidate_list.size();i++){
            RoleTakerDetail detail = (RoleTakerDetail) candidate_list.get(i);
            name_list.add(detail.getName());
        }

        AdapterForShowingVotes adapter = new AdapterForShowingVotes(getActivity(),name_list,votes_list);
        list.setAdapter(adapter);
    }

    void setTableTopicList(ListView list){

        String voting_item = VotingResultFragemnt.voting_item_list.get(2);
        ArrayList<Object> candidate_list = cadidates_map.get(voting_item);
        ArrayList<String> name_list = new ArrayList<>();
        ArrayList<Integer> votes_list = votes_map.get(voting_item);
        for(int i = 0;i< candidate_list.size();i++){
            TableTopicDetail detail = (TableTopicDetail) candidate_list.get(i);
            name_list.add(detail.getName());
        }

        AdapterForShowingVotes adapter = new AdapterForShowingVotes(getActivity(),name_list,votes_list);
        list.setAdapter(adapter);
    }

    void setEvaluatorList(ListView list){

        String voting_item = VotingResultFragemnt.voting_item_list.get(3);
        ArrayList<Object> candidate_list = cadidates_map.get(voting_item);
        ArrayList<String> name_list = new ArrayList<>();
        ArrayList<Integer> votes_list = votes_map.get(voting_item);
        for(int i = 0;i< candidate_list.size();i++){
            EvaluatorDetail detail = (EvaluatorDetail) candidate_list.get(i);
            name_list.add(detail.getName());
        }

        AdapterForShowingVotes adapter = new AdapterForShowingVotes(getActivity(),name_list,votes_list);
        list.setAdapter(adapter);
    }

    public int getIndexOfMaxVotes(ArrayList<Integer> list){
        int index_of_max = -1;
        int max_votes = 0;

        for(int i=0; i<list.size(); i++){

            if(list.get(i).intValue() > max_votes){
                index_of_max = i;
                max_votes = list.get(i).intValue();
            }

        }

        for(int i=0; i<list.size(); i++){

            if(i == index_of_max)
                continue;

            if(list.get(i).intValue() == max_votes)
                return -2;

        }
        return index_of_max;
    }


    Bitmap getBitmap(String name, String voting_item){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/voting app/" + "meeting"+meeting_number +"/" + voting_item);

        if(myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ");
        Log.d("findphoto", "myDir is  " +myDir.toString());

        String fname = name + ".jpg";
        File file = new File(myDir, fname);

        Log.d("findphoto", "file is  " +file.toString());
        Bitmap bitmap;
        if(file.exists()){
            Log.d("findphoto", "file.exists() is true ");
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            return bitmap;
        }
        Log.d("findphoto", "file.exists() is false ");
        return null;
    }



}
