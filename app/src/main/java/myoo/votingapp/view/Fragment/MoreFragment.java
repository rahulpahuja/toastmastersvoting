package myoo.votingapp.view.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import myoo.votingapp.R;


/**
 * A login screen that offers login via email/password.
 */
public class MoreFragment extends Fragment {




    public static MoreFragment newInstance() {
        MoreFragment feedsFragment = new MoreFragment();
        return feedsFragment;
    }


    View rootView;
    TextView txt_header;
    RelativeLayout RRShare,RRRate;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more, container, false);

        init();
        setUpUI();

        RRShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "https://play.google.com/store/apps/details?id=myoo.votingapp";

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Voting App (Open it in Google Play Store to Download the Application)");

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        RRRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=myoo.votingapp")));
            }
        });

        return rootView;
    }

    public void init()
    {
        txt_header=(TextView)rootView.findViewById(R.id.txt_header);
        RRShare=(RelativeLayout)rootView.findViewById(R.id.RRShare);
        RRRate=(RelativeLayout)rootView.findViewById(R.id.RRRate);
    }

    public void setUpUI()
    {
        txt_header.setText("More");
    }



}

