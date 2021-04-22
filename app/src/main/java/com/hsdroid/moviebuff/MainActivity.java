package com.hsdroid.moviebuff;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hsdroid.moviebuff.Adapter.MovieAdapter;
import com.hsdroid.moviebuff.Api.InterfaceAPI;
import com.hsdroid.moviebuff.Api.MyRetrofit;
import com.hsdroid.moviebuff.Database.DBSqlite;
import com.hsdroid.moviebuff.Model.MovieResponse;
import com.hsdroid.moviebuff.Utils.PrefUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;

    Retrofit retrofit;
    InterfaceAPI interfaceAPI;

    DBSqlite dbSqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xff191a32));

        init();
        createImageFolder();
        getPermissions();

    }

    private void createImageFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Movie Buff");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        } else {
            folder.delete();
            success = folder.mkdirs();
        }

        if (success) {
            //Toast.makeText(this, "Created !!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    private void init() {

        dbSqlite = new DBSqlite(this);

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmerAnimation();

        recyclerView = findViewById(R.id.recycler_movielist);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        retrofit = MyRetrofit.getRetrofit();
        interfaceAPI = retrofit.create(InterfaceAPI.class);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            getMovieData(interfaceAPI);
            // Toast.makeText(this, "Internet !!", Toast.LENGTH_SHORT).show();
        } else {
            LinearLayout layout_tryagain = findViewById(R.id.layout_try_again);
            layout_tryagain.setVisibility(View.GONE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connect to Network !!", Toast.LENGTH_SHORT).show();
                    layout_tryagain.setVisibility(View.VISIBLE);
                }
            }, 3000);

        }

    }

    private void getMovieData(InterfaceAPI interfaceAPI) {
        LinearLayout layout_tryagain = findViewById(R.id.layout_try_again);
        layout_tryagain.setVisibility(View.GONE);

        Call<JsonObject> call = interfaceAPI.getMovie();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                if (jsonObject != null) {
                    Log.d("myMovie Success : ", jsonObject.toString());
                    gotoSuccess(jsonObject);

                } else {
                    Toast.makeText(MainActivity.this, "Slow Internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("myMovie Exc : ", t.toString());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Network Problem !!", Toast.LENGTH_SHORT).show();
                        layout_tryagain.setVisibility(View.VISIBLE);
                    }
                }, 5000);
            }
        });
    }

    private void gotoSuccess(JsonObject jsonObject) {
        ArrayList<MovieResponse> ar_movie = new ArrayList<>();
        boolean adult;
        String backdrop_path;
        int[] genres;
        int id;
        String original_language;
        String original_title;
        String overview;
        float popularity;
        String poster_path;
        String release_date;
        String title;
        String video;
        float vote_average;
        long vote_count;

        JsonElement jsonElement = jsonObject.get("results");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Log.d("myMovie Success : ", jsonArray.toString());
        JsonObject object;
        for (JsonElement element : jsonArray) {
            object = element.getAsJsonObject();

            adult = Boolean.parseBoolean(object.get("adult").toString());
            String temp = "\\" + object.get("backdrop_path").toString();
            String path = temp.substring(3, temp.length() - 1);
            backdrop_path = new StringBuilder().append(path).toString();

            String ids = object.get("genre_ids").toString();

            String sub_string = ids.substring(1, ids.length() - 1);

            String[] split = sub_string.split(",");
            genres = new int[split.length];

            for (int i = 0; i < split.length; i++) {
                genres[i] = Integer.parseInt(split[i]);
            }

            id = Integer.parseInt(object.get("id").toString());

            original_language = object.get("original_language").toString();
            original_title = object.get("original_title").toString();
            overview = object.get("overview").toString();
            popularity = Float.parseFloat(object.get("popularity").toString());

            temp = "\\" + object.get("poster_path").toString();
            path = temp.substring(3, temp.length() - 1);
            poster_path = new StringBuilder().append(path).toString();

            //poster_path = object.get("poster_path").toString();
            release_date = object.get("release_date").toString();
            title = object.get("title").toString();
            video = object.get("video").toString();
            vote_average = Float.parseFloat(object.get("vote_average").toString() + "f");
            vote_count = Long.parseLong(object.get("vote_count").toString());

            MovieResponse response = new MovieResponse(adult, backdrop_path, genres, id, original_language, original_title, overview, popularity, poster_path, release_date, title, video, vote_average, vote_count);
            ar_movie.add(response);

            //Log.d("myMovie Success", adult + " " + backdrop_path+" "+genres+" "+vote_average+" "+path);
        }
        setMovieData(ar_movie);
        dbSqlite.insertDBData(ar_movie);
        ArrayList<MovieResponse> data = dbSqlite.getDBData();
        PrefUtilities.with(this).setIsFirstTimeLogin("false");
    }

    private void setMovieData(ArrayList<MovieResponse> ar_movie) {
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        MovieAdapter adapter = new MovieAdapter(ar_movie, this);
        recyclerView.setAdapter(adapter);


        for (MovieResponse movie : ar_movie) {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500/" + movie.getBackdrop_path())
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            SaveImage(bitmap, movie.getBackdrop_path());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500/" + movie.getPoster_path())
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            SaveImage(bitmap, movie.getPoster_path());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

    }


    public void tryAgain(View view) {
        getMovieData(interfaceAPI);
    }

    private void SaveImage(Bitmap finalBitmap, String fname) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Movie Buff");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offline(View view) {

        String response = PrefUtilities.with(this).getIsFirstTimeLogin();

        if (response.equals("false")) {
            ArrayList<MovieResponse> ar_movie = dbSqlite.getDBData();

            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            MovieAdapter adapter = new MovieAdapter(ar_movie, this);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}