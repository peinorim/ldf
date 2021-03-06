package com.paocorp.defunessounds.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.paocorp.defunessounds.R;
import com.paocorp.defunessounds.adapters.SoundListAdapter;
import com.paocorp.defunessounds.db.LDFSoundHelper;
import com.paocorp.defunessounds.models.LDFSound;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected InterstitialAd mInterstitialAd;
    protected ArrayList<LDFSound> listLDFSound;
    protected SoundListAdapter adapter;
    ListView soundListView;
    PackageInfo pInfo;
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView);

        listLDFSound = (ArrayList<LDFSound>) getIntent().getSerializableExtra("listLDFSound");

        if (listLDFSound == null || listLDFSound.size() == 0) {
            listLDFSound = new ArrayList<LDFSound>();
            LDFSoundHelper capsuleTypeHelper = new LDFSoundHelper(getApplicationContext());
            listLDFSound = capsuleTypeHelper.getAllLDFSounds();
        }

        soundListView = (ListView) findViewById(R.id.sound_list);
        adapter = new SoundListAdapter(this, listLDFSound);
        soundListView.setAdapter(adapter);

        boolean isDinerInstalled = appInstalledOrNot(getResources().getString(R.string.dinerPackage));
        boolean isPereInstalled = appInstalledOrNot(getResources().getString(R.string.perePackage));
        boolean isBronzesInstalled = appInstalledOrNot(getResources().getString(R.string.bronzesPackage));
        boolean isKaamInstalled = appInstalledOrNot(getResources().getString(R.string.kaamPackage));

        if (isDinerInstalled && isPereInstalled && isBronzesInstalled && isKaamInstalled) {
            MenuItem interest = navigationView.getMenu().findItem(R.id.interest);
            if (interest != null) {
                interest.setVisible(false);
            }
        } else {
            if (isDinerInstalled) {
                MenuItem dinerItem = navigationView.getMenu().findItem(R.id.nav_diner);
                if (dinerItem != null) {
                    dinerItem.setVisible(false);
                }
            }

            if (isPereInstalled) {
                MenuItem pereItem = navigationView.getMenu().findItem(R.id.nav_pere);
                if (pereItem != null) {
                    pereItem.setVisible(false);
                }
            }

            if (isBronzesInstalled) {
                MenuItem bronzesItem = navigationView.getMenu().findItem(R.id.nav_bronzes);
                if (bronzesItem != null) {
                    bronzesItem.setVisible(false);
                }
            }

            if (isKaamInstalled) {
                MenuItem kaamItem = navigationView.getMenu().findItem(R.id.nav_kaam);
                if (kaamItem != null) {
                    kaamItem.setVisible(false);
                }
            }
        }

        if (isNetworkAvailable()) {
            launchInterstitial();
        }

        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView txv = (TextView) findViewById(R.id.appVersion);
            String APPINFO = txv.getText() + " v" + pInfo.versionName;
            txv.setText(APPINFO);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.store_url)));
        } else if (id == R.id.nav_share) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                String fbText = getResources().getString(R.string.fb_ContentDesc);
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(getResources().getString(R.string.store_url)))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentDescription(fbText)
                        .setImageUrl(Uri.parse(getResources().getString(R.string.app_icon_url)))
                        .build();

                shareDialog.show(linkContent);
            }
        } else if (id == R.id.nav_diner) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.diner_url)));
        } else if (id == R.id.nav_pere) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.pere_url)));
        } else if (id == R.id.nav_bronzes) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.bronzes_url)));
        } else if (id == R.id.nav_kaam) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.kaam_url)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
        return true;
    }

    protected void launchInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(this.getResources().getString(R.string.interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
