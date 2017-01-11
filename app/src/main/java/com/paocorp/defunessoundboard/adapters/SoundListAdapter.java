package com.paocorp.defunessoundboard.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.app.AlertDialog;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.paocorp.defunessoundboard.R;
import com.paocorp.defunessoundboard.db.LDFSoundHelper;
import com.paocorp.defunessoundboard.models.LDFSound;

import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class SoundListAdapter extends BaseAdapter {

    private Context context;
    private List<LDFSound> ldfSounds;
    private LDFSoundHelper ldfSoundHelper;

    public SoundListAdapter(Context context, List<LDFSound> ldfSounds) {
        this.context = context;
        this.ldfSounds = ldfSounds;
        this.ldfSoundHelper = new LDFSoundHelper(context);
    }

    @Override
    public int getCount() {
        return ldfSounds.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LDFSound ldfSound = ldfSounds.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.sound_item, null);

        if (ldfSound != null && !ldfSound.getName().isEmpty()) {

            TextView soundName = (TextView) convertView.findViewById(R.id.soundName);
            soundName.setText(ldfSound.getName());
            soundName.setTag(ldfSound.getId());
            int rawID = context.getResources().getIdentifier(ldfSound.getRes(), "raw", context.getPackageName());
            if (rawID != 0) {
                final MediaPlayer mp = MediaPlayer.create(context, rawID);
                soundName.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if (mp.isPlaying()) {
                            mp.stop();
                        } else {
                            mp.start();
                        }
                    }
                });

                soundName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final int ldfSoundID = (Integer) v.getTag();
                        View parent = (View) v.getParent();
                        createRingtoneDialog(parent, ldfSoundID);
                        return true;
                    }
                });

                ImageButton fav = (ImageButton) convertView.findViewById(R.id.soundFavorite);
                fav.setTag(ldfSound.getId());
                if (ldfSound.isFavorite() == 1) {
                    fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                }
                fav.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final int ldfSoundID = (Integer) v.getTag();
                        final View parent = (View) v.getParent();
                        LDFSound ldfSound1 = ldfSoundHelper.getLDFSoundById(ldfSoundID);
                        if (ldfSound1.isFavorite() == 0) {
                            ldfSound1.setFavorite(1);
                        } else {
                            ldfSound1.setFavorite(0);
                        }
                        if (ldfSoundHelper.updateLDFSound(ldfSound1) == 1) {
                            ImageButton fav = (ImageButton) parent.findViewById(R.id.soundFavorite);
                            if (ldfSound1.isFavorite() == 1) {
                                fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                            } else {
                                fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                            }
                        }
                    }
                });
            } else {
                convertView.setLayoutParams(new ListView.LayoutParams(-1,1));
                convertView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void createRingtoneDialog(final View convertView, final int id) {
        final LDFSound ldfSound1 = ldfSoundHelper.getLDFSoundById(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.dialog_ringtone, null);
        builder.setCancelable(true);

        if (ldfSound1 != null) {
            builder.setView(v).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            TextView dialogTitle = (TextView) v.findViewById(R.id.dialogTitle);
            dialogTitle.setText(context.getResources().getString(R.string.dialog_title, ldfSound1.getName()));

            final AlertDialog alert = builder.create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            Button setRingtone = (Button) v.findViewById(R.id.setRingtone);
            setRingtone.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!android.provider.Settings.System.canWrite(context)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:com.paocorp.louisdefunes"));
                            context.startActivity(intent);
                        } else {
                            Uri path = Uri.parse("android.resource://com.paocorp.louisdefunes/raw/" + ldfSound1.getRes());
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, path);
                            RingtoneManager.getRingtone(context, path).play();
                        }
                    } else {
                        Uri path = Uri.parse("android.resource://com.paocorp.louisdefunes/raw/" + ldfSound1.getRes());
                        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, path);
                        RingtoneManager.getRingtone(context, path).play();
                    }
                    alert.dismiss();
                    Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.ringtoneApplied), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            Button setNotif = (Button) v.findViewById(R.id.setNotif);
            setNotif.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!android.provider.Settings.System.canWrite(context)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:com.paocorp.louisdefunes"));
                            context.startActivity(intent);
                        } else {
                            Uri path = Uri.parse("android.resource://com.paocorp.louisdefunes/raw/" + ldfSound1.getRes());
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, path);
                            RingtoneManager.getRingtone(context, path).play();
                        }
                    } else {
                        Uri path = Uri.parse("android.resource://com.paocorp.louisdefunes/raw/" + ldfSound1.getRes());
                        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, path);
                        RingtoneManager.getRingtone(context, path).play();
                    }
                    alert.dismiss();
                    Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.notifApplied), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            alert.show();
            alert.getWindow().setAttributes(lp);
            if (isNetworkAvailable()) {
                AdView adView = (AdView) v.findViewById(R.id.banner_bottom);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}