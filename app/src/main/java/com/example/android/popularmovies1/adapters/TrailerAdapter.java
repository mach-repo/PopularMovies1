package com.example.android.popularmovies1.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.popularmovies1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by merouane on 31/03/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    final private TrailerAdapter.TrailerAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailer);
    }

    private final Context mContext;
    private List<String> mTrailers;

    private final String TAG ="Adapter";


    public TrailerAdapter(@NonNull Context context, List<String> myDataset, TrailerAdapter.TrailerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mTrailers = myDataset;
        mClickHandler = clickHandler;
    }


    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder holder, int position) {

        String imagePath = "http://img.youtube.com/vi/" + mTrailers.get(position) + "/mqdefault.jpg";
        Picasso.with(mContext)
                .load(imagePath)
                .into(holder.mTrailerImage);
    }


    @Override
    public int getItemCount() {
        if (null == mTrailers){
            return 0;
        } else {
            return mTrailers.size();
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mTrailerImage;

        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerImage = (ImageView) itemView.findViewById(R.id.watch_trailer_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String trailer = mTrailers.get(adapterPosition);
            mClickHandler.onClick(trailer);
        }
    }


    public void swapDataset(List<String> newData){
        mTrailers = newData;
        notifyDataSetChanged();
    }
}
