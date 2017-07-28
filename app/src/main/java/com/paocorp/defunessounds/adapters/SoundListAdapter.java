package com.paocorp.defunessounds.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.paocorp.defunessounds.R;
import com.paocorp.defunessounds.activities.MainActivity;
import com.paocorp.defunessounds.db.LDFSoundHelper;
import com.paocorp.defunessounds.models.LDFSound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class SoundListAdapter extends BaseAdapter {

    private Context context;
    private List<LDFSound> ldfSounds;
    private LDFSoundHelper ldfSoundHelper;
    private String packageRes;
    private MediaPlayer mp;

    public SoundListAdapter(Context context, List<LDFSound> ldfSounds) {
        this.context = context;
        this.ldfSounds = ldfSounds;
        this.ldfSoundHelper = new LDFSoundHelper(context);
        packageRes = "package:" + context.getPackageName();
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
        final ViewHolder holder = new ViewHolder();
        convertView.setTag(holder);

        if (ldfSound != null && !ldfSound.getName().isEmpty()) {

            TextView soundName = (TextView) convertView.findViewById(R.id.soundName);
            soundName.setText(ldfSound.getName());
            soundName.setTag(ldfSound.getId());
            int rawID = context.getResources().getIdentifier(ldfSound.getRes(), "raw", context.getPackageName());
            if (rawID != 0) {
                soundName.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        final int ldfSoundID = (Integer) v.getTag();
                        LDFSound ldfSound1 = ldfSoundHelper.getLDFSoundById(ldfSoundID);
                        int rawID = context.getResources().getIdentifier(ldfSound1.getRes(), "raw", context.getPackageName());
                        if (mp != null) {
                            mp.release();
                            mp = null;
                        }
                        mp = MediaPlayer.create(context, rawID);
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
                holder.fav = fav;
                holder.fav.setTag(ldfSounds.get(position));
                if (ldfSound.isFavorite() == 1) {
                    holder.fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                }
                fav.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LDFSound ldfSound1 = (LDFSound) v.getTag();
                        if (ldfSound1.isFavorite() == 0) {
                            ldfSound1.setFavorite(1);
                        } else {
                            ldfSound1.setFavorite(0);
                        }
                        if (ldfSoundHelper.updateLDFSound(ldfSound1) == 1) {
                            if (ldfSound1.isFavorite() == 1) {
                                holder.fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                            } else {
                                holder.fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                            }
                        }

                    }
                });
            } else {
                convertView.setLayoutParams(new ListView.LayoutParams(-1, 1));
                convertView.setVisibility(View.GONE);
            }
            ViewHolder holderZ = (ViewHolder) convertView.getTag();
            if (holderZ != null && holderZ.fav != null) {
                if (ldfSounds.get(position).isFavorite() == 1) {
                    holderZ.fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                } else {
                    holderZ.fav.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                }
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

            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1234);
                }
            }

            Button setRingtone = (Button) v.findViewById(R.id.setRingtone);
            setRingtone.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!android.provider.Settings.System.canWrite(context)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse(packageRes));
                            context.startActivity(intent);
                        } else {
                            if (setFile(ldfSound1, RingtoneManager.TYPE_RINGTONE)) {
                                alert.dismiss();
                                Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.ringtoneApplied), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                alert.dismiss();
                                Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.errorApplied), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (setFile(ldfSound1, RingtoneManager.TYPE_RINGTONE)) {
                            alert.dismiss();
                            Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.ringtoneApplied), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            alert.dismiss();
                            Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.errorApplied), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                }
            });

            Button setNotif = (Button) v.findViewById(R.id.setNotif);
            setNotif.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!android.provider.Settings.System.canWrite(context)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse(packageRes));
                            context.startActivity(intent);
                        } else {
                            if (setFile(ldfSound1, RingtoneManager.TYPE_NOTIFICATION)) {
                                alert.dismiss();
                                Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.notifApplied), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                alert.dismiss();
                                Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.errorApplied), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (setFile(ldfSound1, RingtoneManager.TYPE_NOTIFICATION)) {
                            alert.dismiss();
                            Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.notifApplied), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            alert.dismiss();
                            Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.errorApplied), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                }
            });

            Button setAlarm = (Button) v.findViewById(R.id.setAlarm);
            setAlarm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!android.provider.Settings.System.canWrite(context)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse(packageRes));
                            context.startActivity(intent);
                        } else {
                            if (setFile(ldfSound1, RingtoneManager.TYPE_ALARM)) {
                                alert.dismiss();
                                Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.alarmApplied), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                alert.dismiss();
                                Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.errorApplied), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (setFile(ldfSound1, RingtoneManager.TYPE_ALARM)) {
                            alert.dismiss();
                            Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.alarmApplied), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            alert.dismiss();
                            Snackbar snackbar = Snackbar.make(convertView, context.getResources().getString(R.string.errorApplied), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
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

    private boolean copyFileUsingStream(String source, File dest) throws IOException {
        InputStream is;
        OutputStream os;
        try {
            is = context.getResources().openRawResource(context.getResources().getIdentifier(source, "raw", context.getPackageName()));
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static class ViewHolder {
        ImageButton fav;
    }

    private boolean setFile(LDFSound ldfSound1, int type) {
        String env = Environment.DIRECTORY_RINGTONES;
        if (type == RingtoneManager.TYPE_ALARM) {
            env = Environment.DIRECTORY_ALARMS;
        } else if (type == RingtoneManager.TYPE_NOTIFICATION) {
            env = Environment.DIRECTORY_NOTIFICATIONS;
        }

        String path = Environment.getExternalStoragePublicDirectory(env).getAbsolutePath();
        String filename = context.getPackageName() + "_" + ldfSound1.getRes();

        boolean exists = (new File(path)).exists();
        if (!exists) {
            new File(path).mkdirs();
        }
        File actualFile = new File(path, filename);

        try {
            if (copyFileUsingStream(ldfSound1.getRes(), actualFile)) {
                //We now create a new content values object to store all the information
                //about the ringtone.
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, actualFile.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, ldfSound1.getName());
                values.put(MediaStore.MediaColumns.SIZE, actualFile.length());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.Audio.AudioColumns.ARTIST, context.getString(R.string.app_name));
                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, type == RingtoneManager.TYPE_RINGTONE);
                values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, type == RingtoneManager.TYPE_NOTIFICATION);
                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, type == RingtoneManager.TYPE_ALARM);
                values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, false);

                //Work with the content resolver now
                //First get the file we may have added previously and delete it,
                //otherwise we will fill up the ringtone manager with a bunch of copies over time.
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(actualFile.getAbsolutePath());
                context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + actualFile.getAbsolutePath() + "\"", null);

                //Ok now insert it
                Uri newUri = context.getContentResolver().insert(uri, values);

                //Ok now set the ringtone from the content manager's uri, NOT the file's uri
                RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        type,
                        newUri
                );
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}