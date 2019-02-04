package myoo.votingapp.view.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


import myoo.votingapp.R;
import myoo.votingapp.Model.Meeting;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MyViewHolder> {

    private Activity mContext;
    private ArrayList<Meeting> meeting_list = new ArrayList<>();

    private final OnItemClickListener listener;

    public void bindNewList(@Nullable List<Meeting> it) {
        meeting_list.clear();
        meeting_list.addAll(it);
    }

    @NotNull
    public List<Meeting> getOriginalList() {
        return meeting_list;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public TextView title;
        public View rowView = null;
        public MyViewHolder(final View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            title= (TextView) view.findViewById(R.id.title);
            this.rowView = view;


        }
    }
    public MeetingAdapter(Activity mContext, OnItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_meeting, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Meeting meeting = meeting_list.get(position);

        holder.title.setText(meeting.getMeetingNo());

        // add here logic to fetch meeting created by whome and change UI accordingly
        // album.getCreatedby();
        // only show the meetings created by self




        holder.rowView.setTag(holder);

        holder.rowView.setOnClickListener(view -> {
                    MyViewHolder interHolder = (MyViewHolder) view.getTag();
            listener.onCardClicked(meeting_list.get(interHolder.getAdapterPosition()),position);
        });
    }

    @Override
    public int getItemCount() {
        return meeting_list.size();
    }
    public interface OnItemClickListener {
        void onCardClicked(Meeting cardDetails, int position);
    }
}
