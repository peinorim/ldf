package com.paocorp.louisdefunes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.paocorp.louisdefunes.R;
import com.paocorp.louisdefunes.db.DatabaseHelper;
import com.paocorp.louisdefunes.db.LDFSoundHelper;
import com.paocorp.louisdefunes.models.LDFSound;

import java.util.ArrayList;
import java.util.Random;

public class SplashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPref;
    ArrayList<LDFSound> listLDFSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashImg = (ImageView) findViewById(R.id.splashImg);
        Random rand = new Random();
        int res = rand.nextInt(10 - 1 + 1) + 1;
        splashImg.setImageResource(getResources().getIdentifier("img_" + res, "drawable", getPackageName()));

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PrefetchData().execute();
            }
        }, 3000);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            long data_imported = sharedPref.getInt(getString(R.string.data_imported), 0);

            if (data_imported == 0) {
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.startImportation();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.data_imported), 1);
                editor.apply();
            }

            listLDFSound = new ArrayList<LDFSound>();

            LDFSoundHelper capsuleTypeHelper = new LDFSoundHelper(getApplicationContext());
            listLDFSound = capsuleTypeHelper.getAllLDFSounds();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and launch main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("listLDFSound", listLDFSound);

            startActivity(intent);
            finish();
        }

    }
}
