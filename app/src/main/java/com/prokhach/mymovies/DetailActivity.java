package com.prokhach.mymovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prokhach.mymovies.adapter.ReviewAdapter;
import com.prokhach.mymovies.adapter.TrailerAdapter;
import com.prokhach.mymovies.data.FavouriteMovie;
import com.prokhach.mymovies.data.MainViewModel;
import com.prokhach.mymovies.data.Movie;
import com.prokhach.mymovies.data.Review;
import com.prokhach.mymovies.data.Trailer;
import com.prokhach.mymovies.utils.JSONUtils;
import com.prokhach.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewAddToFavourites;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private FavouriteMovie favouriteMovie;
    private Movie movie;
    private int id;
    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent favouriteIntent = new Intent(this, FavouriteActivity.class);
                startActivity(favouriteIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewAddToFavourites = findViewById(R.id.imageViewAddToFavourites);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();

        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        trailerAdapter = new TrailerAdapter();
        reviewAdapter = new ReviewAdapter();

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });

        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);

        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideo(movie.getId());
        JSONObject jsonObjectReview = NetworkUtils.getJSONForReviews(movie.getId());
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewFromJSON(jsonObjectReview);
        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);
        scrollViewInfo.smoothScrollTo(0, 0);

        imageViewAddToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteMovie == null) {
                    viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                    Toast.makeText(DetailActivity.this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.deleteFavouriteMovie(favouriteMovie);
                    Toast.makeText(DetailActivity.this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                }
                setFavourite();
            }
        });
    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourites.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourites.setImageResource(R.drawable.favourite_remove);
        }
    }
}