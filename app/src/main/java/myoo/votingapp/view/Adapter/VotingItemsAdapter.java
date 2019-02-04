package myoo.votingapp.view.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import myoo.votingapp.R;


public class VotingItemsAdapter extends RecyclerView.Adapter<VotingItemsAdapter.MyViewHolder> {

    private Activity mContext;
    private ArrayList<String> imagelist,voting_item_count_list;
    private final OnItemClickListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title,txt_user_count;
        public View rowView = null;
        public MyViewHolder(final View view) {
            super(view);

            txt_user_count = (TextView) view.findViewById(R.id.txt_user_count);
            title= (TextView) view.findViewById(R.id.title);
            this.rowView = view;


        }
    }
    public VotingItemsAdapter(Activity mContext,
                              ArrayList<String> imagelist,
                              ArrayList<String> voting_item_count_list,
                              OnItemClickListener listener) {
        this.mContext = mContext;
        this.imagelist = imagelist;
        this.voting_item_count_list=voting_item_count_list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_voting_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String album = imagelist.get(position);

        holder.title.setText(album);
        holder.txt_user_count.setText(position<voting_item_count_list.size() ? voting_item_count_list.get(position):""+0);
        String singleItem=imagelist.get(position);
        holder.rowView.setTag(singleItem);
        holder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onCardClicked((String) view.getTag(),position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }
    public interface OnItemClickListener {
        void onCardClicked(String cardDetails, int position);
    }
}
