package com.example.android.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies1.adapters.MovieAdapter;
import com.example.android.popularmovies1.data.Movie;
import com.example.android.popularmovies1.data.Review;
import com.example.android.popularmovies1.utilities.NetworkUtils;
import com.example.android.popularmovies1.data.MovieContract.MovieEntry;
import com.example.android.popularmovies1.utilities.TrailersUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler{

    private static final String TAG = "MainActivity";

    private final int NUMBER_OF_COLUMNS = 2;

    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.error_message_textview)
    TextView mNoInternetTextview;

    private int mPosition = RecyclerView.NO_POSITION;

    private static final int ADD_DELETE_MOVIE_TO_FAVORITES_REQUEST = 10;
    private static final int SETTINGS_CODE = 11;
    public static final String SETTING_ACTIVITY_RESULT = "settings-results";
    public static final String DETAIL_ACTIVITY_RESULT = "detail-results";

    private boolean mDisplayingFavorites = false;

    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final int ID_MOVIES_LOADER = 1992;
    private static final int ID_FAVORITE_MOVIES_LOADER = 1992;



    private LoaderManager.LoaderCallbacks<List<Movie>> moviesLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Movie>>() {
        @Override
        public Loader<List<Movie>> onCreateLoader(int loaderId, Bundle args) {

            switch (loaderId) {
                case ID_MOVIES_LOADER:
                    showProgressBar();
                    return new MoviesLoader(MainActivity.this, null);
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
            mAdapter.swapDataset(data);
            if (mPosition == RecyclerView.NO_POSITION) {
                mPosition = 0;
            }
            mRecyclerView.smoothScrollToPosition(mPosition);
            hideProgressBar();
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {
            mAdapter.swapDataset(null);
        }};

    private LoaderManager.LoaderCallbacks<Cursor> favoritesMoviesLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

            showProgressBar();

            String[] projection = {
                    MovieEntry._ID,
                    MovieEntry.COLUMN_IMAGE_URL,
                    MovieEntry.COLUMN_TITLE,
                    MovieEntry.COLUMN_OVERVIEW,
                    MovieEntry.COLUMN_USER_RATING,
                    MovieEntry.COLUMN_RELEASE_DATE};

            return new CursorLoader(MainActivity.this,
                    MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            ArrayList<Movie> movies = new ArrayList<>();
            movies.addAll(createMoviesFromCursor(data));

            mAdapter.swapDataset(movies);
            mPosition = 0;
            mRecyclerView.smoothScrollToPosition(mPosition);
            hideProgressBar();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapDataset(null);
        }};

    private ArrayList<Movie> mMoviesList;
    private static final String MOVIES_LIST = "movies-list";
    private static final String LAYOUT_MANAGER_STATE = "layout-state";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMoviesList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter(this, null, this);
        mRecyclerView.setAdapter(mAdapter);




        if(savedInstanceState != null){
            mMoviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST);
            mAdapter.swapDataset(mMoviesList);

            Parcelable state = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            mLayoutManager.onRestoreInstanceState(state);
        }else{
            if(isOnline()){
                executeCorrespondingLoader();
            }else{
                showErrorNoInternet();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mMoviesList = new ArrayList<Movie>(mAdapter.getListMovies());
        outState.putParcelableArrayList(MOVIES_LIST, mMoviesList);
        outState.putParcelable(LAYOUT_MANAGER_STATE, mLayoutManager.onSaveInstanceState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showErrorNoInternet(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoInternetTextview.setVisibility(View.VISIBLE);
    }
    private void showProgressBar(){
        mRecyclerView.setVisibility(View.GONE);
        mNoInternetTextview.setVisibility(View.GONE);
        mProgressBar.setVisibility(VISIBLE);
    }
    private void hideProgressBar(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoInternetTextview.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private List<Movie> createMoviesFromCursor(Cursor data){
        List<Movie> moviesList = new LinkedList<Movie>();

        if(data != null){
            while (data.moveToNext()) {

                long movieId = data.getLong(data.getColumnIndex(MovieEntry._ID));
                String imageUrl = data.getString(data.getColumnIndex(MovieEntry.COLUMN_IMAGE_URL));
                String title = data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE));
                String overview = data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                String userRating = data.getString(data.getColumnIndex(MovieEntry.COLUMN_USER_RATING));
                String releaseDate = data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));

                Movie newMovie = new Movie(movieId, imageUrl, title, overview, userRating, releaseDate, true);

                //Log.d(TAG, "the url of the movie is = " + imageUrl);
                moviesList.add(newMovie);
            }
        }

        if(data != null){
            data.close();
        }
        //Log.d(TAG, "number of movies in favorites is " + moviesList.size());
        return moviesList;
    }

    private void executeCorrespondingLoader(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortValue = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));

        if(sortValue.equals(getString(R.string.pref_sort_favorites))){
            getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, favoritesMoviesLoaderListener);
            mDisplayingFavorites = true;
        }else{
            getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, moviesLoaderListener);
            mDisplayingFavorites = false;
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE, movie);
        startActivityForResult(intent, ADD_DELETE_MOVIE_TO_FAVORITES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_DELETE_MOVIE_TO_FAVORITES_REQUEST) {
            if (resultCode == RESULT_OK) {

                boolean hasFavoritesChanged = data.getBooleanExtra(DETAIL_ACTIVITY_RESULT, false);

                if(hasFavoritesChanged && mDisplayingFavorites){
                    getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, favoritesMoviesLoaderListener).forceLoad();
                }
            }
        } else if (requestCode == SETTINGS_CODE) {
            if (resultCode == RESULT_OK) {

                boolean hasPrefChanged = data.getBooleanExtra(SETTING_ACTIVITY_RESULT, false);

                if(hasPrefChanged){
                    executeCorrespondingLoader();
                }
            }
        }
    }

    // this class is an asynctask that gets the movies
    private static class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

        List<Movie> mMoviesResults;
        Context mContext;

        public MoviesLoader(Context context, List<Movie> movies){
            super(context);
            mContext = context;
            mMoviesResults = movies;
        }

        @Override
        public List<Movie> loadInBackground() {
            mMoviesResults = NetworkUtils.getMovies(mContext);
            return mMoviesResults;
        }
        @Override
        protected void onStartLoading() {
            if (mMoviesResults != null) {
                Log.d(TAG, "movies cached with success");
                deliverResult(mMoviesResults);
            } else {
                forceLoad();
            }
        }
        @Override
        public void deliverResult(List<Movie> results) {
            mMoviesResults = results;
            super.deliverResult(results);
        }
    }
}
