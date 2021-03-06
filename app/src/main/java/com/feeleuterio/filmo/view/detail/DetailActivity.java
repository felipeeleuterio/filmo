package com.feeleuterio.filmo.view.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.feeleuterio.filmo.R;
import com.feeleuterio.filmo.api.model.Genre;
import com.feeleuterio.filmo.api.model.Images;
import com.feeleuterio.filmo.api.model.Movie;
import com.feeleuterio.filmo.view.App;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";

    @Inject
    DetailPresenter detailPresenter;

    @BindView(R.id.movieDetailPoster)
    ImageView imageViewPoster;
    @BindView(R.id.movieDetailToolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    View contentView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.overviewHeader)
    View overviewHeader;
    @BindView(R.id.overviewTextView)
    TextView overviewTextView;
    @BindView(R.id.genresTextView)
    TextView genresTextView;
    @BindView(R.id.releaseTextView)
    TextView releaseTextView;
    @BindView(R.id.textView)
    View errorView;
    @BindView(R.id.progressBar)
    View loadingView;

    private int movieId = -1;
    private Images images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        DaggerDetailComponent.builder()
                .appComponent(App.getAppComponent(getApplication()))
                .detailModule(new DetailModule(this))
                .build()
                .inject(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            movieId = extras.getInt(MOVIE_ID);
            String movieTitle = extras.getString(MOVIE_TITLE);

            setTitle(movieTitle);
        }
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailPresenter.start(movieId);
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        showContent(false);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showContent(Movie movie) {
        toolbar.setTitle(getTitle());
        String fullImagePosterUrl = getFullImagePosterUrl(movie);
        if (!fullImagePosterUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .error(R.color.colorPrimaryDark);
            Glide.with(this)
                    .load(fullImagePosterUrl)
                    .apply(options)
                    .transition(withCrossFade())
                    .into(imageViewPoster);
        }
        String fullImageUrl = getFullImageUrl(movie);
        if (!fullImageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .error(R.drawable.movies_white_48dp);
            Glide.with(this)
                    .load(fullImageUrl)
                    .apply(options)
                    .transition(withCrossFade())
                    .into(imageView);
        }
        overviewTextView.setText(getOverview(movie.overview));
        genresTextView.setText(getGenres(movie));
        releaseTextView.setText(getReleaseDate(movie.releaseDate));

        loadingView.setVisibility(View.GONE);
        showContent(true);
        errorView.setVisibility(View.GONE);
    }

    private String getReleaseDate(String release) {
        return TextUtils.isEmpty(release) ? "-" : release;
    }

    private String getOverview(String overview) {
        return TextUtils.isEmpty(overview) ? "-" : overview;
    }

    @NonNull
    private String getFullImagePosterUrl(Movie movie) {
        String imagePath;

        if (movie.backdropPath != null && !movie.backdropPath.isEmpty()) {
            imagePath = movie.backdropPath;
        } else {
            imagePath = movie.posterPath;
        }

        if (images != null && images.baseUrl != null && !images.baseUrl.isEmpty()) {
            if (images.posterSizes != null) {
                if (images.posterSizes.size() > 5) {
                    return images.baseUrl + images.posterSizes.get(5) + imagePath;
                } else {
                    return images.baseUrl + "w500" + imagePath;
                }
            }
        }
        return "";
    }

    @NonNull
    private String getFullImageUrl(Movie movie) {
        String imagePath;

        if (movie.posterPath != null && !movie.posterPath.isEmpty()) {
            imagePath = movie.posterPath;
        } else {
            imagePath = movie.backdropPath;
        }

        if (images != null && images.baseUrl != null && !images.baseUrl.isEmpty()) {
            if (images.posterSizes != null) {
                if (images.posterSizes.size() > 4) {
                    return images.baseUrl + images.posterSizes.get(5) + imagePath;
                } else {
                    return images.baseUrl + "w500" + imagePath;
                }
            }
        }
        return "";
    }

    private String getGenres(Movie movie) {
        String genres = "";
        for (int i = 0; i < movie.genres.size(); i++) {
            Genre genre = movie.genres.get(i);
            genres += genre.name + ", ";
        }

        genres = removeTrailingComma(genres);

        return genres.isEmpty() ? "-" : genres;
    }

    @NonNull
    private String removeTrailingComma(String text) {
        text = text.trim();
        if (text.endsWith(",")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    @Override
    public void showError() {
        loadingView.setVisibility(View.GONE);
        showContent(false);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationSet(Images images) {
        this.images = images;
    }

    private void showContent(boolean show) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;

        contentView.setVisibility(visibility);
        overviewHeader.setVisibility(visibility);
        overviewTextView.setVisibility(visibility);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
