package com.hsdroid.moviebuff;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hsdroid.moviebuff.Model.MovieResponse;

public class MovieDetails extends AppCompatActivity {

    MovieResponse response;
    ImageView movie_backdrop, movie_like;
    TextView tx_movie_title, tx_movie_adult, tx_movie_release, tx_movie_language, tx_vote_count, tx_movie_vote, tx_movie_overview;
    View adult_view;
    int like = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        response = (MovieResponse) getIntent().getSerializableExtra("movie");

        String movie_title = response.getTitle().substring(1, response.getTitle().length() - 1);

        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.Toolbar_TitleText);
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.Toolbar_Collapsed_TitleText);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CardView cardView = findViewById(R.id.pinned_details);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolBarLayout.setTitle(movie_title);
                    cardView.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    toolBarLayout.setTitle(" ");
                    cardView.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        init();
        pinnedDataInit();
    }

    private void pinnedDataInit() {
        ImageView pinned_poster = findViewById(R.id.pinned_poster);

        TextView pinned_title = findViewById(R.id.tx_movie_title_2);
        TextView pinned_adult = findViewById(R.id.tx_movie_adult_2);
        TextView pinned_release = findViewById(R.id.tx_movie_release_2);
        TextView pinned_language = findViewById(R.id.tx_movie_language_2);
        View pinned_adult_view = findViewById(R.id.adult_line_2);
        TextView pinned_vote_count = findViewById(R.id.tx_movie_vote_count_2);
        TextView pinned_vote = findViewById(R.id.tx_movie_vote_2);
        setPinnedData(pinned_poster, pinned_title, pinned_adult, pinned_release, pinned_language, pinned_adult_view, pinned_vote_count, pinned_vote);
    }

    private void setPinnedData(ImageView pinned_poster, TextView pinned_title, TextView pinned_adult, TextView pinned_release, TextView pinned_language, View pinned_adult_view, TextView pinned_vote_count, TextView pinned_vote) {
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/" + response.getPoster_path())
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(pinned_poster);

        pinned_title.setText(response.getTitle().substring(1, response.getTitle().length() - 1));
        pinned_language.setText(response.getOriginal_language().substring(1, response.getOriginal_language().length() - 1));
        pinned_release.setText(response.getRelease_date().substring(1, response.getRelease_date().length() - 1));
        pinned_vote_count.setText("Votes : " + response.getVote_count() + "");
        pinned_vote.setText(response.getVote_average() + "");

        if (!response.getAdult()) {
            pinned_adult.setVisibility(View.GONE);
            pinned_adult_view.setVisibility(View.GONE);
        }
    }

    private void init() {
        movie_like = findViewById(R.id.like);
        movie_backdrop = findViewById(R.id.movie_backdrop);
        tx_movie_adult = findViewById(R.id.tx_movie_adult);
        tx_movie_title = findViewById(R.id.tx_movie_title);
        tx_movie_release = findViewById(R.id.tx_movie_release);
        tx_movie_language = findViewById(R.id.tx_movie_language);
        tx_vote_count = findViewById(R.id.tx_movie_vote_count);
        tx_movie_vote = findViewById(R.id.tx_movie_vote);
        adult_view = findViewById(R.id.adult_line);
        tx_movie_overview = findViewById(R.id.tx_movie_overview);
        setData();
    }

    private void setData() {
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/" + response.getBackdrop_path())
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(movie_backdrop);

        tx_movie_title.setText(response.getTitle().substring(1, response.getTitle().length() - 1));
        tx_movie_language.setText(response.getOriginal_language().substring(1, response.getOriginal_language().length() - 1));
        tx_movie_release.setText(response.getRelease_date().substring(1, response.getRelease_date().length() - 1));
        tx_vote_count.setText("Votes : " + response.getVote_count() + "");
        tx_movie_vote.setText(response.getVote_average() + "");
        tx_movie_overview.setText(response.getOverview().substring(1, response.getOverview().length() - 1));

        if (!response.getAdult()) {
            tx_movie_adult.setVisibility(View.GONE);
            adult_view.setVisibility(View.GONE);
        }
    }

    public void imageLiked(View view) {
        if (like == 0) {
            movie_like.setImageResource(R.drawable.ic_baseline_favorite_24_selected);
            like = 1;
        } else {
            movie_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            like = 0;
        }
    }
}