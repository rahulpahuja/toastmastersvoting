package myoo.votingapp.view.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
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

import myoo.votingapp.R;
import myoo.votingapp.view.Activity.ListOfCandidates;

/**
 * Created by MA294214 on 3/26/2017.
 */

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private final Context context;       // for saving the current context
    public Drawable d, d1;
    public int selected_position = -1;
    ArrayList<Boolean> disable_list=null;
    String voting_item;
    private Boolean isMyMeeting;

    boolean is_it_the_voting_process = false;

    RadioButtonManager radioButtonManager;
    private ArrayList<String> names_list, project_list, title_list, role_list;
    private ArrayList<String> photo_list;

    public CustomArrayAdapter(Context context,
                              ArrayList<String> names_list,
                              ArrayList<String> title_list,
                              ArrayList<String> project_list,
                              ArrayList<String> photo_list,
                              String voting_item,
                              ArrayList<Boolean> disable_list,
                              Boolean isMyMeeting) {

        super(context, R.layout.candidate, names_list);

        // initialise class variables with passed parameters
        this.context = context;
        d= VectorDrawableCompat.create(context.getResources(),R.drawable.ic_visibility_black_24dp,context.getTheme());
        d1= VectorDrawableCompat.create(context.getResources(),R.drawable.ic_visibility_off_black_24dp,context.getTheme());
        this.names_list = names_list;
        this.role_list = title_list;
        this.title_list = title_list;
        this.project_list = project_list;
        this.photo_list = photo_list;
        this.disable_list = disable_list;
        this.voting_item = voting_item;
        this.isMyMeeting = isMyMeeting;

        Log.d("checkadapter", "custom adapter intialised with voting_item " + voting_item);
        radioButtonManager = new RadioButtonManager();
    }

    public CustomArrayAdapter(Context context, ArrayList<String> names_list,
                              ArrayList<String> title_list,
                              ArrayList<String> project_list,
                              ArrayList<String> photo_list,
                              String voting_item,  Boolean isMyMeeting) {

        super(context, R.layout.candidate, names_list);
this.isMyMeeting = isMyMeeting;
        // initialise class variables with passed parameters
        this.context = context;
        d= VectorDrawableCompat.create(context.getResources(),R.drawable.ic_visibility_black_24dp,context.getTheme());
        d1= VectorDrawableCompat.create(context.getResources(),R.drawable.ic_visibility_off_black_24dp,context.getTheme());
        this.names_list = names_list;
        this.role_list = title_list;
        this.title_list = title_list;
        this.project_list = project_list;
        this.photo_list = photo_list;

        this.voting_item = voting_item;
        radioButtonManager = new RadioButtonManager();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View myView = convertView;
        final ViewHolder viewHolder;

        if (myView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (is_it_the_voting_process)
                myView = inflater.inflate(R.layout.candidate_voting_row, parent, false);
            else
                myView = inflater.inflate(R.layout.candidate, parent, false);

            viewHolder = new ViewHolder();

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

        ViewHolder holder = (ViewHolder) myView.getTag();

        if (is_it_the_voting_process) {

            holder.radioButton.setChecked(selected_position == position);

            holder.radioButton.setTag(position);
            if(disable_list.get(position)){
                holder.radioButton.setVisibility(View.GONE);
                holder.RRRow.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(disable_list.get(position)){
                            ((RadioButton)peopeleview).setChecked(false);
                            Toast.makeText(context, "Can not vot a disqualify getUsersListRef", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        selected_position = (Integer) view.getTag();
                        notifyDataSetChanged();
                    }
                });
            }


        } else {
            holder.deletebutton.setOnClickListener(view -> {
                ((ListOfCandidates) context).deleteTheCandidate(position);
                notifyDataSetChanged();
            });

        }

        if(disable_list!=null)
        {
            myView.setBackgroundColor(disable_list.get(position)? Color.parseColor("#FFDADBDC"):Color.TRANSPARENT);
        }




        if (voting_item.equals(context.getResources().getText(R.string.prepared_speaker))) {

            holder.name.setText("Name: " + names_list.get(position));
            holder.project.setText("Project:  " + project_list.get(position));
            holder.title.setText("Title:  " + "\"" + title_list.get(position) + "\"");
            if (photo_list.get(position) != null)
                loadPhoto(position, holder);

        } else if (voting_item.equals(context.getResources().getText(R.string.role_takers))) {

            holder.name.setText("Name: " + names_list.get(position));
            holder.project.setText("Role:  " + role_list.get(position));
            if (photo_list.get(position) != null)
                loadPhoto(position, holder);

        }
        else if (voting_item.equals(context.getResources().getText(R.string.tag_team))) {

            holder.name.setText("Name: " + names_list.get(position));
            holder.project.setText("Role:  " + role_list.get(position));
            if (photo_list.get(position) != null)
                loadPhoto(position, holder);

        }
        else if (voting_item.equals(context.getResources().getText(R.string.table_topics))) {

            holder.name.setText("Name: " + names_list.get(position));
            if (photo_list.get(position) != null)
                loadPhoto(position, holder);

        } else if (voting_item.equals(context.getResources().getText(R.string.evaluators))) {

            holder.name.setText("Name: " + names_list.get(position));
            if (photo_list.get(position) != null)
                loadPhoto(position, holder);

        }

        if (!is_it_the_voting_process)
            holder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  if (isMyMeeting) ((ListOfCandidates) context).editTheCandidate(position);
                    //  dialogueForSharingThePhrase(holder.word_tv.getText().toString() ,holder.meaning_tv.getText().toString() ,language );
                }
            });


        return myView;
    }

    private void loadPhoto(int position, ViewHolder holder) {
        ImageView photo = holder.photo;
        String url = photo_list.get(position);
        if (url.length()>0) {
            Glide.with(photo.getContext()).load(url).into(photo);
        }else {
            Glide.with(photo).load(R.drawable.ic_user).into(photo);
        }
        }

    static class ViewHolder {

        // On screen asset declarations
        TextView name;
        TextView project;
        TextView title;
        ImageView photo;

        RelativeLayout row, RRRow;

        RadioButton radioButton;
        ImageView deletebutton;
    }

    class RadioButtonManager {
        int currently_selected_position;
        ArrayList<RadioButton> radio_button_list;

        public RadioButtonManager() {
            currently_selected_position = -1;
            radio_button_list = new ArrayList<>();
        }

        void radiobuttonchecked(int position, boolean checked) {

            if (checked) {
                Log.d("checkradio", "radiobutton  at position " + position + "was " + checked);
                for (int i = 0; i < radio_button_list.size(); i++) {
                    radio_button_list.get(i).setChecked(false);
                }

                radio_button_list.get(position).setChecked(true);
                currently_selected_position = position;

            }
        }

        void resetTheGroup() {
            radio_button_list.clear();
            currently_selected_position = -1;

        }
    }



}




