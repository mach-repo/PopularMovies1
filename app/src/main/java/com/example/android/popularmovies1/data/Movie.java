package com.example.android.popularmovies1.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by merouane on 11/01/2018.
 */

public class Movie implements Parcelable {

    private long id;
    private String imageUrl = "http://image.tmdb.org/t/p/w185";
    private String title;
    private String overview;
    private String userRating;
    private String releaseDate;

    public Movie(long id, String image, String title, String overview, String userRating, String releaseDate){
        this.id = id;
        this.imageUrl = this.imageUrl + image;
        this.title = title;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate.substring(0, 4);
    }

    public Movie(long id, String image, String title, String overview, String userRating, String releaseDate, boolean fromCursor){
        this.id = id;
        this.imageUrl = image;
        this.title = title;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate.substring(0, 4);
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.imageUrl);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.userRating);
        dest.writeString(this.releaseDate);
    }

    protected Movie(Parcel in) {
        this.id = in.readLong();
        this.imageUrl = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
