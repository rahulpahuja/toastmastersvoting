package myoo.votingapp.view.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import myoo.votingapp.R;

/**
 * Created by MA294214 on 5/2/2017.
 */

public class AdapterForShowingVotes extends ArrayAdapter<String> {
    private final Context context;

    private ArrayList<String> names_list ;
    private ArrayList<Integer> number_of_votes_list;

    static class ViewHolder {
        TextView name;
        TextView number_of_votes;
    }


    public AdapterForShowingVotes(Context context, ArrayList<String> names_list, ArrayList<Integer> number_of_votes_list) {

        super(context, R.layout.candidate_and_votes_gained, names_list );
        this.context = context;
        this.names_list = names_list;
        this.number_of_votes_list = number_of_votes_list;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View myView = convertView;
        final ViewHolder viewHolder;

        if(myView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                myView = inflater.inflate(R.layout.candidate_and_votes_gained, parent, false);

            viewHolder = new ViewHolder();

            // asset declaration for row
            viewHolder.name = (TextView) myView.findViewById(R.id.name);
            viewHolder.number_of_votes = (TextView) myView.findViewById(R.id.votes_tv);
            myView.setTag(viewHolder);

        }

       ViewHolder holder = (ViewHolder) myView.getTag();
            holder.name.setText(names_list.get(position));
            holder.number_of_votes.setText( ""+ number_of_votes_list.get(position));


        return myView;
    }



}

