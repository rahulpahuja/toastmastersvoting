package myoo.votingapp.view.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import myoo.votingapp.Model.UniqueId;
import myoo.votingapp.R;

public class CustomAdapterForVoting extends ArrayAdapter<UniqueId> {

    private final Context context;
    public CustomAdapterForVoting(Context context,
                              ArrayList<UniqueId> names_list
                              ) {

        super(context, R.layout.candidate, names_list);

        // initialise class variables with passed parameters
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View myView = convertView;


        return myView;
    }
}
