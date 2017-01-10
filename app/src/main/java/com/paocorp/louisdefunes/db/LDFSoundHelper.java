package com.paocorp.louisdefunes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.paocorp.louisdefunes.models.LDFSound;

import java.util.ArrayList;

public class LDFSoundHelper extends DatabaseHelper {

    public LDFSoundHelper(Context context) {
        super(context);
    }

    public ArrayList<LDFSound> getAllLDFSounds() {

        ArrayList<LDFSound> allLDFSounds = new ArrayList<LDFSound>();

        String selectQuery = "SELECT * FROM " + TABLE_SOUNDS + " ORDER BY " + COLUMN_SOUND_FAVORITE + "  DESC, " + COLUMN_SOUND_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                LDFSound ldfSound = new LDFSound();
                ldfSound.setId(c.getInt(c.getColumnIndex(COLUMN_SOUND_ID)));
                ldfSound.setName(c.getString(c.getColumnIndex(COLUMN_SOUND_NAME)));
                ldfSound.setRes(c.getString(c.getColumnIndex(COLUMN_SOUND_RES)));
                ldfSound.setFavorite(c.getInt(c.getColumnIndex(COLUMN_SOUND_FAVORITE)));

                allLDFSounds.add(ldfSound);
            } while (c.moveToNext());
        }
        c.close();
        return allLDFSounds;
    }

    public LDFSound getLDFSoundById(int id) {
        String selectQuery = "SELECT * FROM " + TABLE_SOUNDS + " WHERE " + COLUMN_SOUND_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            LDFSound ldfSound = new LDFSound();
            ldfSound.setId(c.getInt(c.getColumnIndex(COLUMN_SOUND_ID)));
            ldfSound.setName(c.getString(c.getColumnIndex(COLUMN_SOUND_NAME)));
            ldfSound.setRes(c.getString(c.getColumnIndex(COLUMN_SOUND_RES)));
            ldfSound.setFavorite(c.getInt(c.getColumnIndex(COLUMN_SOUND_FAVORITE)));
            return ldfSound;
        }
        return null;
    }

    public int updateLDFSound(LDFSound ldfSound) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SOUND_FAVORITE, ldfSound.isFavorite());

        try {
            return db.update(TABLE_SOUNDS, values, COLUMN_SOUND_ID + " = ?",
                    new String[]{String.valueOf(ldfSound.getId())});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
