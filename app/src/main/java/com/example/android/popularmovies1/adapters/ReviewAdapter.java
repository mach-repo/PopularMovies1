package com.example.android.popularmovies1.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.popularmovies1.R;
import com.example.android.popularmovies1.data.Review;

import java.util.List;

/**
 * Created by merouane on 01/04/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {


    private final Context mContext;
    private List<Review> mReviews;

    private final String TAG ="Adapter";


    public ReviewAdapter(@NonNull Context context, List<Review> myDataset) {
        mContext = context;
        mReviews = myDataset;
    }


    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, parent, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.mAuthorTextView.setText(review.getAuthor());
        holder.mReviewTextView.setText(review.getReview());
    }


    @Override
    public int getItemCount() {
        if (null == mReviews){
            return 0;
        } else {
            return mReviews.size();
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView mAuthorTextView;
        TextView mReviewTextView;


        ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.review_author_textview);
            mReviewTextView = (TextView) itemView.findViewById(R.id.review_content_textview);
        }
    }


    public void swapDataset(List<Review> newData){
        mReviews = newData;
        notifyDataSetChanged();
    }
}

