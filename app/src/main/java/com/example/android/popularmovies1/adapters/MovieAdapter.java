package com.example.android.popularmovies1.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies1.R;
import com.example.android.popularmovies1.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by merouane on 11/01/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    final private MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    private final Context mContext;
    private List<Movie> mMovies;

    private final String TAG ="Adapter";
    /* the constructor */
    /* done */
    public MovieAdapter(@NonNull Context context, List<Movie> myDataset, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mMovies = myDataset;
        mClickHandler = clickHandler;
    }

    /* done */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    /* done */
    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
        Picasso.with(mContext).load(mMovies.get(position).getImageUrl())
                //.networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.mImageView);
        //Log.d(TAG, "im here link = " + mMovies.get(position).getImageUrl() );
    }

    /* done */
    @Override
    public int getItemCount() {
        if (null == mMovies){
            return 0;
        } else {
            return mMovies.size();
        }

    }

    // stores and recycles views as they are scrolled off screen
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.movie_picture);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovies.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }


    public void swapDataset(List<Movie> newData){
        mMovies = newData;
        //Log.d(TAG, "the new data has size = " + mMovies.size());
        //Log.d(TAG, "link of first image is = " + mMovies.get(0).getImageUrl());
        notifyDataSetChanged();
    }

    public List<Movie> getListMovies(){
        return mMovies;
    }

}
