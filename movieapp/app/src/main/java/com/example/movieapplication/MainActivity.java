package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int SORT_BY_NONE = 0;
    private static final int SORT_BY_NAME = 1;
    private static final int SORT_BY_RELEASE_DATE = 2;
    private static final int SORT_BY_RATING = 3;
    private static final String TAG = "MainActivity";

    private LoadingDialog loadingDialog;

    private List<Movie> movies = new ArrayList<>();

    private TextView tvDown1, tvDown2, tvFilterMessage;
    private Button btnReset, btnName, btnReleaseDate, btnRating;
    private Toolbar toolbar;

    IRequestHandler service;

    FragmentManager frManager;
    Fragment listFrag;


    private FragmentRefreshListener fragmentRefreshListener;
    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder remoteService) {
            service = IRequestHandler.Stub.asInterface(remoteService);
            Log.d(TAG, "onServiceConnected() connected");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
        }
    };

    public void initService() {
        Intent i = new Intent();
        i.setClassName("com.example.movieapplication", com.example.movieapplication.RequestHandler.class.getName());
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
    }

    private void releaseService() {
        unbindService(connection);
        connection = null;
        Log.d(TAG, "releaseService() unbound.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search a movie:");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Movie> filteredMovies = new ArrayList<>();
                for (Movie movie : movies) {
                    if (movie.getMovieName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredMovies.add(movie);
                    }
                }
                if (filteredMovies.size() == 0) {
                    frManager.beginTransaction()
                            .hide(listFrag)
                            .commit();
                    tvFilterMessage.setVisibility(View.VISIBLE);
                } else {
                    frManager.beginTransaction()
                            .show(listFrag)
                            .commit();
                }
                getFragmentRefreshListener().onRefresh(SORT_BY_NONE, filteredMovies);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();

        NotificationChannel channel = new NotificationChannel("notification_channel",
                "notification_channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed Successfully";
                    if (!task.isSuccessful()) {
                        msg = "Subscription failed";
                    }
                    Log.d(TAG, msg);
                });

        loadingDialog = new LoadingDialog(this);
        //loadingDialog.startLoadingAnimation();

        btnReset = findViewById(R.id.btnReset);
        btnName = findViewById(R.id.btnName);
        btnReleaseDate = findViewById(R.id.btnReleaseDate);
        btnRating = findViewById(R.id.btnRating);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tvDown1 = findViewById(R.id.tvDown1);
        tvDown2 = findViewById(R.id.tvDown2);
        tvFilterMessage = findViewById(R.id.tvFilterMessage);
        tvFilterMessage.setVisibility(View.GONE);
        tvDown1.setVisibility(View.GONE);
        tvDown2.setVisibility(View.GONE);

        frManager = getSupportFragmentManager();
        listFrag = frManager.findFragmentById(R.id.listFrag);

        btnReset.setOnClickListener(v -> {
            getFragmentRefreshListener().onRefresh(SORT_BY_NONE, movies);
        });
        btnName.setOnClickListener(v -> {
            getFragmentRefreshListener().onRefresh(SORT_BY_NAME, movies);
        });
        btnReleaseDate.setOnClickListener(v ->
                getFragmentRefreshListener().onRefresh(SORT_BY_RELEASE_DATE, movies));
        btnRating.setOnClickListener(v -> {
            getFragmentRefreshListener().onRefresh(SORT_BY_RATING, movies);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        loadingDialog.startLoadingAnimation();
        super.onResume();
        Handler handler = new Handler(Looper.getMainLooper());
        Handler handler1 = new Handler(Looper.getMainLooper());
        handler1.postDelayed(() -> {
            try {
                if(service != null){
                    tvDown1.setVisibility(View.GONE);
                    tvDown2.setVisibility(View.GONE);
                    service.getAllMovies(movies);
                }
            } catch (RemoteException e) {
                Log.d(TAG, e.toString());
                tvDown1.setVisibility(View.VISIBLE);
                tvDown2.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismissDialog();
            handler.postDelayed(() -> {
                if(movies.size() == 0){
                    tvDown1.setVisibility(View.VISIBLE);
                    tvDown2.setVisibility(View.VISIBLE);
                } else {
                    getFragmentRefreshListener().onRefresh(SORT_BY_NONE, movies);
                }
                Log.d(TAG, movies.toString());
            }, 400);
        }, 500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    public interface FragmentRefreshListener {
        void onRefresh(int sort, List<Movie> movies);
    }
}