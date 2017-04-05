package com.paocorp.defunessounds.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.paocorp.defunessounds.R;
import com.paocorp.defunessounds.db.DatabaseHelper;
import com.paocorp.defunessounds.db.LDFSoundHelper;
import com.paocorp.defunessounds.models.LDFSound;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPref;
    ArrayList<LDFSound> listLDFSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PrefetchData().execute();
            }
        }, 2000);

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
