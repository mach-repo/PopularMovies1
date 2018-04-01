package com.example.android.popularmovies1;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies1.adapters.ReviewAdapter;
import com.example.android.popularmovies1.adapters.TrailerAdapter;
import com.example.android.popularmovies1.data.Movie;
import com.example.android.popularmovies1.data.Review;
import com.example.android.popularmovies1.utilities.ReviewUtils;
import com.example.android.popularmovies1.utilities.TrailersUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import static android.view.View.GONE;
import static com.example.android.popularmovies1.MainActivity.DETAIL_ACTIVITY_RESULT;

import com.example.android.popularmovies1.data.MovieContract.MovieEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapter.TrailerAdapterOnClickHandler{

    public static final String TAG = "DetailActivity";
    public static final String MOVIE = "movie";

    @BindView(R.id.movie_image)
    ImageView mMoviePoster;

    @BindView(R.id.movie_title)
    TextView mMovieTitle;

    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDate;

    @BindView(R.id.movie_rating)
    TextView mMovieRating;

    @BindView(R.id.movie_overview)
    TextView mMovieOverview;

    @BindView(R.id.favorites_button)
    Button mFavoritesButton;

    @BindView(R.id.trailers_linearlayout)
    LinearLayout mTrailersLinearLayout;

    @BindView(R.id.reviews_linearlayout)
    LinearLayout mReviewsLinearLayout;

    private static final int ID_TRAILERS_LOADER = 5001;
    private static final int ID_REVIEWS_LOADER = 5002;

    private Uri mCurrentMovieUri;
    private Movie mCurrentMovie;

    private boolean mDataHasChanged = false;

    View.OnClickListener mAddMovieListener = new View.OnClickListener(){
        public void  onClick  (View  v){
            addMovieToDb();
            mDataHasChanged = true;
        }
    };
    View.OnClickListener mDeleteMovieListener = new View.OnClickListener(){
        public void  onClick  (View  v){
            deleteMovieFromDb();
            mDataHasChanged = true;
        }
    };

    private LoaderManager.LoaderCallbacks<List<String>> trailersLoaderListener
            = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int loaderId, Bundle args) {

            switch (loaderId) {
                case ID_TRAILERS_LOADER:
                    return  new TrailersTaskLoader(DetailActivity.this, null, mCurrentMovie.getId());
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            // passing the data to the trailers' recyclerview adapter
            mTrailerAdapter.swapDataset(data);

            // make the linear layout visible now
            if(data != null && data.size() != 0){
                mTrailersLinearLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            mTrailerAdapter.swapDataset(null);
        }};

    private LoaderManager.LoaderCallbacks<List<Review>> reviewsLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Review>>() {
        @Override
        public Loader<List<Review>> onCreateLoader(int loaderId, Bundle args) {

            switch (loaderId) {
                case ID_REVIEWS_LOADER:
                    return new ReviewsTaskLoader(DetailActivity.this, null, mCurrentMovie.getId());
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            // passing the data to the trailers' recyclerview adapter
            mReviewAdapter.swapDataset(data);

            // make the linear layout visible now
            if(data != null && data.size() != 0){
                mReviewsLinearLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
            mReviewAdapter.swapDataset(null);
        }};

    // trailers' recyclerview and adapter
    @BindView(R.id.trailers_recyclerView)
    RecyclerView mTrailersRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    // reviews' recyclerview and adapter
    @BindView(R.id.reviews_recyclerView)
    RecyclerView mReviewsRecyclerView;
    private ReviewAdapter mReviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // trailers recyclerview and its adapter
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        // trailer adapter
        mTrailerAdapter = new TrailerAdapter(this, null, this);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);


        // reviews recyclerview and its adapter
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        // reviews adapter
        mReviewAdapter = new ReviewAdapter(this, null);
        mReviewsRecyclerView.setAdapter(mReviewAdapter);


        if(getIntent() != null){

            Bundle data = getIntent().getExtras();
            mCurrentMovie = data.getParcelable(DetailActivity.MOVIE);

            assignMovieDataToViews();

            boolean isMovieInDb = isMovieInDb(mCurrentMovie.getId());

            if(isMovieInDb){
                mCurrentMovieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, mCurrentMovie.getId());
                changeFavoriteButtonBehavior(isMovieInDb);
            }else{
                changeFavoriteButtonBehavior(isMovieInDb);
            }


            getSupportLoaderManager().initLoader(ID_TRAILERS_LOADER, null, trailersLoaderListener);
            getSupportLoaderManager().initLoader(ID_REVIEWS_LOADER, null, reviewsLoaderListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // telling MainActivity if favorites has changed
            Intent returnIntent = new Intent();
            returnIntent.putExtra(DETAIL_ACTIVITY_RESULT, mDataHasChanged);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void assignMovieDataToViews(){
        Picasso.with(this).load(mCurrentMovie.getImageUrl()).into(mMoviePoster);
        setTitle(mCurrentMovie.getTitle());
        mMovieTitle.setText(mCurrentMovie.getTitle());
        mMovieReleaseDate.setText(mCurrentMovie.getReleaseDate());
        mMovieRating.setText(mCurrentMovie.getUserRating());
        mMovieOverview.setText(mCurrentMovie.getOverview());
    }

    private void addMovieToDb(){

        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, mCurrentMovie.getId());
        values.put(MovieEntry.COLUMN_IMAGE_URL, mCurrentMovie.getImageUrl());
        values.put(MovieEntry.COLUMN_TITLE, mCurrentMovie.getTitle());
        values.put(MovieEntry.COLUMN_OVERVIEW, mCurrentMovie.getOverview());
        values.put(MovieEntry.COLUMN_USER_RATING, mCurrentMovie.getUserRating());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mCurrentMovie.getReleaseDate());

        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        if (newUri == null) {
            Log.e(TAG, "error coudlnt add the movie");
        } else {
            Log.d(TAG, "movie added successfully");

            mCurrentMovieUri = newUri;
            changeFavoriteButtonBehavior(true);
        }
    }

    private void deleteMovieFromDb(){
        Log.d(TAG, "about to get deleted " + mCurrentMovieUri.toString());
        int rowsDeleted = getContentResolver().delete(mCurrentMovieUri, null, null);

        if (rowsDeleted == 0) {
            Log.e(TAG, "error no movie was deleted");
        } else {
            Log.d(TAG, "movie deleted successfully");
            mCurrentMovieUri = null;
            changeFavoriteButtonBehavior(false);
        }
    }

    private boolean isMovieInDb(long movieId){

        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        if(cursor != null){
            while (cursor.moveToNext()){
                long currentMovieId = cursor.getLong(cursor.getColumnIndex(MovieEntry._ID));
                if (currentMovieId == movieId) {
                    return true;
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    private void changeFavoriteButtonBehavior(boolean movieIsInDb){
        if(movieIsInDb){
            mFavoritesButton.setText(getString(R.string.delete_from_favorites));
            mFavoritesButton.setOnClickListener(mDeleteMovieListener);
        }else{
            mFavoritesButton.setText(getString(R.string.add_to_favorites));
            mFavoritesButton.setOnClickListener(mAddMovieListener);
        }
    }

    // onclick of the trailer
    @Override
    public void onClick(String trailer) {

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailer));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    // this class is an asynctask that gets the reviews
    private static class ReviewsTaskLoader extends AsyncTaskLoader<List<Review>> {

        List<Review> mReviewsResults;
        long movieId;

        public ReviewsTaskLoader(Context context, List<Review> reviews, long movieId) {
            super(context);
            this.mReviewsResults = reviews;
            this.movieId = movieId;
        }

        @Override
        public List<Review> loadInBackground() {
            mReviewsResults = ReviewUtils.getReviews(movieId);
            return mReviewsResults;
        }
        @Override
        protected void onStartLoading() {
            if (mReviewsResults != null) {
                Log.d(TAG, "reviews cached with success");
                deliverResult(mReviewsResults);
            } else {
                forceLoad();
            }
        }
        @Override
        public void deliverResult(List<Review> results) {
            mReviewsResults = results;
            super.deliverResult(results);
        }
    }

    // this class is an asynctask that gets the trailers
    private static class TrailersTaskLoader extends AsyncTaskLoader<List<String>> {

        // this variable for caching purpose
        List<String> mResults;
        long movieId;

        public TrailersTaskLoader(Context context, List<String> trailers, long movieId) {
            super(context);
            this.mResults = trailers;
            this.movieId = movieId;
        }

        @Override
        public List<String> loadInBackground() {
            mResults = TrailersUtils.getTrailers(movieId);
            return mResults;
        }
        @Override
        protected void onStartLoading() {
            if (mResults != null) {
                Log.d(TAG, "trailers cached with success");
                deliverResult(mResults);
            } else {
                forceLoad();
            }
        }
        @Override
        public void deliverResult(List<String> results) {
            mResults = results;
            super.deliverResult(results);
        }
    }
}
