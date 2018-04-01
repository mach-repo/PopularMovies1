package com.example.android.popularmovies1.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies1.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.android.popularmovies1.utilities.NetworkUtils.API_KEY;
import static com.example.android.popularmovies1.utilities.NetworkUtils.API_KEY_PARAM;
import static com.example.android.popularmovies1.utilities.NetworkUtils.MOVIES_BASE_URL;
import static com.example.android.popularmovies1.utilities.NetworkUtils.REVIEWS_PATH;

/**
 * Created by merouane on 17/01/2018.
 */

public class ReviewUtils {

    private static final String TAG = "ReviewUtils";
    /* CREATING THE REVIEWS URL */
    /* done */
    private static URL buildReviewsUrl(long movieId) {

        Uri movieQueryUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath("" + movieId)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        try {
            URL queryUrl = new URL(movieQueryUri.toString());
            return queryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "malformatted URL, buildReviewsUrl function");
            return null;
        } finally {
            Log.d(TAG, "buildReviewsUrl function is fine");
            Log.d(TAG, "reviews url is = " + movieQueryUri.toString());
        }
    }


    /* returns list of trailers from the RAW JSON response we got */
    /* done */
    private static List<Review> getReviewsFromJSON(String rawJsonResponse) throws JSONException {

        List<Review> listReviews = new LinkedList<Review>();

        JSONObject rawJsonObject = new JSONObject(rawJsonResponse);

        if (rawJsonObject.has("results")) {
            // now we have the right data
            JSONArray reviewsArray = rawJsonObject.getJSONArray("results");
            for(int i = 0; i < reviewsArray.length(); i++){

                JSONObject aSingleReview = reviewsArray.getJSONObject(i);

                String author = aSingleReview.getString("author");
                String reviewContent = aSingleReview.getString("content");

                Review review = new Review(author, reviewContent);

                listReviews.add(review);
            }
            Log.d(TAG, "getReviewsFromJSON function is fine");
        } else {
            // an error has occured (link is invalid or server is down
            Log.e(TAG, "rawJson has wrong response");
            return null;
        }
        return listReviews;
    }

    /* makes the network call, parse the data then pass it back */
    public static List<Review> getReviews(long movieId){

        List<Review> reviews = new LinkedList<Review>();

        try{
            URL queryUrl = buildReviewsUrl(movieId);

            String rawJsonResponse = NetworkUtils.getResponseFromHttpUrl(queryUrl);

            reviews = getReviewsFromJSON(rawJsonResponse);

        } catch(Exception e){
            //Log.e(TAG, "error getReviews function");

        } finally {
            Log.d(TAG, "getReviews function is fine");
        }
        Log.d("merou", "got reviews");
        return reviews;
    }
}
