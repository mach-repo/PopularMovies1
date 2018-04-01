package com.example.android.popularmovies1.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by merouane on 17/01/2018.
 */

public class Review implements Parcelable {

    private String author;
    private String review;

    public Review(String author, String review) {
        this.author = author;
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public String getReview() {
        return review;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.review);
    }

    protected Review(Parcel in) {
        this.author = in.readString();
        this.review = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
