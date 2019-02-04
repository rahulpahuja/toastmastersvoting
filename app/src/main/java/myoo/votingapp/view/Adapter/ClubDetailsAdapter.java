package myoo.votingapp.view.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import myoo.votingapp.view.Fragment.ListOfClubMembersFragment;
import myoo.votingapp.R;

/**
 * Created by MA294214 on 6/22/2017.
 */

public class ClubDetailsAdapter extends ArrayAdapter<String> {
    private final Context context;       // for saving the current context

    private ArrayList<String> names_list;

    final int MEMBER_TYPE = 1, GUEST_TYPE = 2;

    static class ViewHolder {
        // On screen asset declarations
        TextView name;
        ImageView photo;
        View delete;
    }

    ListOfClubMembersFragment fragment;

    public ClubDetailsAdapter(Context context, ArrayList<String> names_list, ListOfClubMembersFragment fragment) {

        super(context, R.layout.row_person_with_photo, names_list);
        this.fragment = fragment;
        this.context = context;
        this.names_list = names_list;


    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View myView = convertView;
        final ViewHolder viewHolder;

        if (myView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            myView = inflater.inflate(R.layout.row_person_with_photo, parent, false);

            viewHolder = new ViewHolder();

            // asset declaration for row
            viewHolder.photo = (ImageView) myView.findViewById(R.id.photo_of_person);
            viewHolder.name = (TextView) myView.findViewById(R.id.name_of_person);
            viewHolder.delete = myView.findViewById(R.id.deletebutton);
            myView.setTag(viewHolder);

        }


        ViewHolder holder = (ViewHolder) myView.getTag();
        holder.name.setText(names_list.get(position));
        Bitmap b = getCandidatePhoto(names_list.get(position));


        if (b != null)
            holder.photo.setImageBitmap(b);
        holder.name.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (fragment).editTheCandidate(position);
            }
        });
        holder.delete.setVisibility(View.GONE);


        holder.delete.setVisibility(View.VISIBLE);

        if (fragment.getMPage() == 1) {

        }

        switch (fragment.getMPage()) {

            case MEMBER_TYPE:
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTheMember(position);
                    }
                });
                break;
            case GUEST_TYPE:
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTheGuest(position);
                    }
                });
                break;
        }
        return myView;
    }

    Bitmap getCandidatePhoto(String name) {
        String root = Environment.getExternalStorageDirectory().toString();

        String file_path = "";
        switch (fragment.getMPage()) {

            case MEMBER_TYPE:
                file_path = root + "/voting app /members";
                break;
            case GUEST_TYPE:
                file_path = root + "/voting app /guests";
                break;
        }

        File myDir = new File(file_path);

        if (myDir.exists())
            Log.d("findphoto", "myDir.exists() is true ");
        Log.d("findphoto", "myDir is  " + myDir.toString());

        String fname = name + ".jpg";
        File file = new File(myDir, fname);

        Log.d("findphoto", "file is  " + file.toString());
        Bitmap bitmap;
        if (file.exists()) {
            Log.d("findphoto", "file.exists() is true ");
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            return bitmap;
        }
        Log.d("findphoto", "file.exists() is false ");
        return null;
    }

    public void deleteTheMember(final int position) {
        AlertDialog.Builder set_server_dialogue = new AlertDialog.Builder(context);
        set_server_dialogue.setTitle(" Do you want to delete member's entries?");

        set_server_dialogue.setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                names_list.remove(position);
                ClubDetailsAdapter.this.notifyDataSetChanged();
                fragment.getMydb().setMembers(names_list);

                dialog.dismiss();

            }
        });

        set_server_dialogue.setNegativeButton("No ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }
        });

        AlertDialog myalertobject = set_server_dialogue.create();
        //Show the dialog
        myalertobject.show();


    }

    public void deleteTheGuest(final int position) {
        AlertDialog.Builder set_server_dialogue = new AlertDialog.Builder(context);
        set_server_dialogue.setTitle(" Do you want to delete guest's entries?");

        set_server_dialogue.setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                names_list.remove(position);
                ClubDetailsAdapter.this.notifyDataSetChanged();
                fragment.getMydb().setguests(names_list);

                dialog.dismiss();

            }
        });

        set_server_dialogue.setNegativeButton("No ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }
        });

        AlertDialog myalertobject = set_server_dialogue.create();
        //Show the dialog
        myalertobject.show();


    }
}

