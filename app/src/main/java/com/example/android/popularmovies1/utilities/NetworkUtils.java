package com.example.android.popularmovies1.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies1.data.Movie;
import com.example.android.popularmovies1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by merouane on 12/01/2018.
 */

public final class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    public static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static final String POPULAR_MOVIES_PATH = "popular";

    public static final String TOP_RATED_PATH = "top_rated";

    public static final String TRAILERS_PATH = "videos";

    public static final String REVIEWS_PATH = "reviews";

    public static final String API_KEY_PARAM = "api_key";

    public static final String API_KEY = "b246298de22db32f3645ad9ad3cef8f1";


    /* redas from the shared prefrence and gets the suitable URL for the query */
    private static URL getUrl(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortValue = prefs.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular));

        if(sortValue.equals(context.getString(R.string.pref_sort_toprated))){
            return buildUrl(TOP_RATED_PATH);
        }else{
            return buildUrl(POPULAR_MOVIES_PATH);
        }
    }

    /* helper method to create URL from the path */
    private static URL buildUrl(String path) {

        Uri movieQueryUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(path)
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

    /* gets the raw JSON from the given url server */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
            //Log.d(TAG, "we have the JSON data now getResponseFromHttpUrl function is fine");
        }
    }

    /* returns list of movies from the RAW JSON response we got */
    private static List<Movie> getListmovies(String rawJsonResponse) throws JSONException {

        List<Movie> listMovies = new LinkedList<Movie>();

        JSONObject rawJsonObject = new JSONObject(rawJsonResponse);

        if (rawJsonObject.has("results")) {

            JSONArray moviesArray = rawJsonObject.getJSONArray("results");

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject aSingleMovie = moviesArray.getJSONObject(i);

                long id = aSingleMovie.getLong("id");
                String posterUrl = aSingleMovie.getString("poster_path");
                String title = aSingleMovie.getString("original_title");
                String overview = aSingleMovie.getString("overview");
                String userRating = aSingleMovie.getString("vote_average");
                String releaseDate = aSingleMovie.getString("release_date");

                Movie newMovie = new Movie(id, posterUrl, title, overview, userRating, releaseDate);

                listMovies.add(newMovie);
            }

            Log.d(TAG, "getListmovies function is fine");
        } else {
            // an error has occured (link is invalid or server is down
            Log.e(TAG, "rawJson has wrong response");
            return null;
        }
        return listMovies;
    }


    public static List<Movie> getMovies(Context context){
        List<Movie> movies = new LinkedList<Movie>();

        // build the URL first
        URL queryUrl = getUrl(context);
        //Log.e(TAG, "the query url is " + queryUrl.toString());

        // get the JSON response
        String rawJsonResponse = "";
        try{
            rawJsonResponse = getResponseFromHttpUrl(queryUrl);
        }catch (IOException e){
            Log.e(TAG, "json data id = " + rawJsonResponse);
            Log.e(TAG, "can't get the raw json yet");
        }

        // get the list of movies from the JSON response
        try{
            movies = getListmovies(rawJsonResponse);
        } catch(JSONException e){
            Log.e(TAG, "can't get the data from JSON");
        }
        Log.e("merou","got the movies");
        return movies;
    }
}
