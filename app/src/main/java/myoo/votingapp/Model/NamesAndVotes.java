package myoo.votingapp.Model;

import java.io.Serializable;
import java.util.ArrayList;



public class NamesAndVotes implements Serializable {
    public String name;
    public ArrayList<String> prepared_speaker_name_list;

    public ArrayList<String> role_taker_name_list ;

    public ArrayList<String> table_topic_name_list ;

    public ArrayList<String> evaluators_name_list;

    public ArrayList<Integer> prepared_speaker_votes_list;

    public ArrayList<Integer> role_taker_votes_list ;

    public ArrayList<Integer> table_topic_speaker_votes_list ;

    public ArrayList<Integer> evaluators_votes_list;


    public NamesAndVotes(ArrayList<String> prepared_speaker_name_list, ArrayList<String> role_taker_name_list, ArrayList<String> table_topic_name_list, ArrayList<String> evaluators_name_list, ArrayList<Integer> prepared_speaker_votes_list, ArrayList<Integer> role_taker_votes_list, ArrayList<Integer> table_topic_speaker_votes_list, ArrayList<Integer> evaluators_votes_list) {
        this.prepared_speaker_name_list = prepared_speaker_name_list;
        this.role_taker_name_list = role_taker_name_list;
        this.table_topic_name_list = table_topic_name_list;
        this.evaluators_name_list = evaluators_name_list;
        this.prepared_speaker_votes_list = prepared_speaker_votes_list;
        this.role_taker_votes_list = role_taker_votes_list;
        this.table_topic_speaker_votes_list = table_topic_speaker_votes_list;
        this.evaluators_votes_list = evaluators_votes_list;
    }
}