package myoo.votingapp.view.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import myoo.votingapp.Model.Candidate;
import myoo.votingapp.R;
import myoo.votingapp.view.Activity.ListOfCandidates;

public class CandidateArrayAdapter extends ArrayAdapter<Candidate> {

    private final Context context;       // for saving the current context
    public Drawable d, d1;
    public int selected_position = -1;
   // ArrayList<Boolean> disable_list=null;
    String voting_item;
    private Boolean isMyMeeting;

    ArrayList<Candidate> candidates ;


    public CandidateArrayAdapter(@NonNull Context context, ArrayList<Candidate> candidateList , String voting_item) {
        super(context, R.layout.candidate , candidateList );
        this.context = context;

        candidates = candidateList ;


        this.voting_item = voting_item ;
    }

    private void loadPhoto(int position, CustomArrayAdapter.ViewHolder holder) {
        ImageView photo = holder.photo;
        String url = candidates.get(position).photo;
        if (url.length()>0) {
            Glide.with(photo.getContext()).load(url).into(photo);
        }else {
            Glide.with(photo).load(R.drawable.ic_user).into(photo);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View myView = convertView;
        final CustomArrayAdapter.ViewHolder viewHolder;

        if (myView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                myView = inflater.inflate(R.layout.candidate_voting_row, parent, false);

            viewHolder = new CustomArrayAdapter.ViewHolder();

            // asset declaration for row
            viewHolder.name = (TextView) myView.findViewById(R.id.name_of_cadidate);
            viewHolder.title = (TextView) myView.findViewById(R.id.title_of_cadidate);
            viewHolder.project = (TextView) myView.findViewById(R.id.project_of_cadidate);
            viewHolder.photo = ((ImageView) myView.findViewById(R.id.photo));
            viewHolder.RRRow=(RelativeLayout)myView.findViewById(R.id.RRRow);

            viewHolder.row = (RelativeLayout) myView.findViewById(R.id.completerow);

            viewHolder.radioButton = (RadioButton) myView.findViewById(R.id.radioButton2);
            viewHolder.deletebutton = (ImageView) myView.findViewById(R.id.deletebutton);

            myView.setTag(viewHolder);

            //if (is_it_the_voting_process) viewHolder.deletebutton.setVisibility(View.GONE);

        }

        CustomArrayAdapter.ViewHolder holder = (CustomArrayAdapter.ViewHolder) myView.getTag();



            holder.radioButton.setChecked(selected_position == position);

            holder.radioButton.setTag(position);
            if(candidates.get(position).isDisabled){
                holder.radioButton.setVisibility(View.GONE);
                holder.RRRow.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selected_position = (Integer) view.getTag();
                        notifyDataSetChanged();
                    }
                });
            }



            myView.setBackgroundColor(candidates.get(position).isDisabled? Color.parseColor("#FFDADBDC"):Color.TRANSPARENT);





        if (voting_item.equals(context.getResources().getText(R.string.prepared_speaker))) {

                holder.name.setText("Name: " + candidates.get(position).name);
            holder.project.setText("Project:  " + candidates.get(position).project);
            holder.title.setText("Title:  " + "\"" + candidates.get(position).title + "\"");
            if (candidates.get(position).photo != null)
                loadPhoto(position, holder);

        } else if (voting_item.equals(context.getResources().getText(R.string.role_takers))) {

            holder.name.setText("Name: " + candidates.get(position).name);
            holder.project.setText("Role:  " + candidates.get(position).role);
            if (candidates.get(position).photo != null)
                loadPhoto(position, holder);

        }
        else if (voting_item.equals(context.getResources().getText(R.string.tag_team))) {

            holder.name.setText("Name: " + candidates.get(position).name);
            holder.project.setText("Role:  " + candidates.get(position).role);
            if (candidates.get(position).photo != null)
                loadPhoto(position, holder);

        }
        else if (voting_item.equals(context.getResources().getText(R.string.table_topics))) {

            holder.name.setText("Name: " + candidates.get(position).name);
            if (candidates.get(position).photo != null)
                loadPhoto(position, holder);

        } else if (voting_item.equals(context.getResources().getText(R.string.evaluators))) {

            holder.name.setText("Name: " + candidates.get(position).name);
            if (candidates.get(position).photo != null)
                loadPhoto(position, holder);

        }


        return myView;
    }


}
