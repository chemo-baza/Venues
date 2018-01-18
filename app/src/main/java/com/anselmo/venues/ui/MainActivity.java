package com.anselmo.venues.ui;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import com.anselmo.venues.R;
import com.anselmo.venues.api.VenueService;
import com.anselmo.venues.models.VenueReponse;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {
    private static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_LOCATION_PERM = 124;

    //This I don't like. Dependency injection using dagger 2 would be the best
    private Retrofit mRetrofit;
    private VenueService mApi;

    /**
     * It had would good idea to use view injection library -ButterKnife
     * But that make the final apk more heavy and I had would have that make changes in gradle file,
     * proguard, etc... Too much time ;(
     */
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolBar = getToolbar();
        mToolBar.setTitle(getString(R.string.app_name));

        /**
         * --WARNING--
         * Never do that below. It's the worst thing that you can do. Why?
         *
         * Simple: It's not maintainable and testable. In a future if I need to change anything I will need to change all classes
         * that needs make http requests. Also its much spend of memory and resources.
         *
         * Other best choice?
         *
         * Yes! In the personal I like to use the following frameworks to try to obtain a clean architecture:
         * -Retrofit
         * -Dagger 2
         * -RxJava/RxAndroid
         *
         * If you get join all those frameworks yours apps would be great
         *
         * And MVP pattern to separate business logic from views(Activities).
         *
         * Unfortunately I don't have enough time to do the best practices
         */
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://movesync-qa.dcsg.com/dsglabs/mobile/api/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        mApi = mRetrofit.create(VenueService.class);
        //-----------------------------------------

        Call<VenueReponse> response = mApi.getVenues();
        response.enqueue(new Callback<VenueReponse>() {
            @Override
            public void onResponse(Call<VenueReponse> call, Response<VenueReponse> response) {
                if( response != null ) {
                    Toast.makeText(MainActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VenueReponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getCause().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
