package com.example.android.popularmovies1.utilities;

import android.net.Uri;
import android.util.Log;

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
import static com.example.android.popularmovies1.utilities.NetworkUtils.TRAILERS_PATH;

/**
 * Created by merouane on 17/01/2018.
 */

public class TrailersUtils {

    /* CREATING THE TRAILER URL */
    private static URL buildTrailersUrl(long movieId) {

        Uri movieQueryUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath("" + movieId)
                .appendPath(TRAILERS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        try {
            URL queryUrl = new URL(movieQueryUri.toString());
            return queryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "malformatted URL, builUrl function");
            return null;
        } finally {
            Log.d(TAG, "buildurl function is fine");
        }
    }


    /* returns list of trailers from the RAW JSON response we got */
    private static List<String> getTrailersFromJSON(String rawJsonResponse) throws JSONException {

        List<String> listTrailers = new LinkedList<String>();

        JSONObject rawJsonObject = new JSONObject(rawJsonResponse);

        if (rawJsonObject.has("results")) {

            JSONArray videosArray = rawJsonObject.getJSONArray("results");

            for(int i = 0; i < videosArray.length(); i++){
                JSONObject aSingleVideo = videosArray.getJSONObject(i);

                // get the type first is it a normal video or a trailer ?
                String type = aSingleVideo.getString("type");

                if(type.equals("Trailer")){
                    // now we're sure that this video is a trailer
                    String videoKey = aSingleVideo.getString("key");
                    listTrailers.add(videoKey);
                }
            }

            Log.d(TAG, "getTrailersFromJSON function is fine");
        } else {
            // an error has occured (link is invalid or server is down
            Log.e(TAG, "rawJson has wrong response");
            return null;
        }
        return listTrailers;
    }

    /* makes the network call, parse the data then pass it back */
    public static List<String> getTrailers(long movieId){

        List<String> trailers = new LinkedList<String>();

        try{
            URL queryUrl = buildTrailersUrl(movieId);

            String rawJsonResponse = NetworkUtils.getResponseFromHttpUrl(queryUrl);

            trailers = getTrailersFromJSON(rawJsonResponse);

        } catch(Exception e){
            Log.e(TAG, "error getTrailers function");

        } finally {
            Log.d(TAG, "getTrailers function is fine");
        }

        return trailers;
    }
}
