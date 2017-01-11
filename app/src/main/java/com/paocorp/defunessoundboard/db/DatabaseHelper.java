package com.paocorp.defunessoundboard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    protected Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbldf";

    // Logcat tag
    protected static final String LOG = "DatabaseHelper";

    // TABLES NAMES
    static final String TABLE_SOUNDS = "sounds";

    // CAPSULES COLUMNS NAMES
    static final String COLUMN_SOUND_ID = "sound_id";
    static final String COLUMN_SOUND_NAME = "sound_name";
    static final String COLUMN_SOUND_RES = "sound_res";
    static final String COLUMN_SOUND_FAVORITE = "sound_favorite";

    // CAPSULES TABLE CREATE STATEMENT
    private static final String TABLE_CREATE_SOUNDS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SOUNDS + " (" +
                    COLUMN_SOUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_SOUND_NAME + " TEXT, " +
                    COLUMN_SOUND_RES + " TEXT, " +
                    COLUMN_SOUND_FAVORITE + " INTEGER DEFAULT 0)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /******************** TEST ENV ONLY ***********************/
        //db.execSQL("DROP TABLE IF EXISTS capsules");
        //db.execSQL("DROP TABLE IF EXISTS capsule_type");
        /******************** TEST ENV ONLY ***********************/
        db.execSQL(TABLE_CREATE_SOUNDS);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void startImportation() {
        SQLiteDatabase db = this.getWritableDatabase();
        /******************** TEST ENV ONLY ***********************/
        //db.execSQL("DROP TABLE IF EXISTS capsules");
        //db.execSQL("DROP TABLE IF EXISTS capsule_type");
        db.execSQL(TABLE_CREATE_SOUNDS);
        /******************** TEST ENV ONLY ***********************/
        db.execSQL(importSounds());
        closeDB();
    }


    private String importSounds() {
        return "INSERT INTO " + TABLE_SOUNDS + " (" + COLUMN_SOUND_ID + ", " + COLUMN_SOUND_NAME + ", " + COLUMN_SOUND_RES + ", " + COLUMN_SOUND_FAVORITE + ") VALUES \n" +
                "(1,'Zip splash','audio_1','0')," +
                "(2,'Hin hin hin hin','audio_2','0')," +
                "(3,'He he','audio_3','0')," +
                "(4,'Oh oh oh ho!','audio_4','0')," +
                "(5,'Oh oh ooh!','audio_5','0')," +
                "(6,'Hahahahaha!','audio_6','0')," +
                "(7,'He he hehee','audio_7','0')," +
                "(8,'Beuahhhhh','audio_8','0')," +
                "(9,'Vous vous foutez moi?','audio_9','0')," +
                "(10,'Vous vous foutez moi? 2','audio_10','0')," +
                "(11,'Il me regarde comme ça','audio_11','0')," +
                "(12,'Là!','audio_12','0')," +
                "(13,'Kikikikiki','audio_13','0')," +
                "(14,'COuicouicouicouicoui','audio_14','0')," +
                "(15,'Kikikikiki2','audio_15','0')," +
                "(16,'Couii','audio_16','0')," +
                "(17,'Proui ','audio_17','0')," +
                "(18,'Croui ','audio_18','0')," +
                "(19,'Coui Coui','audio_19','0')," +
                "(20,'Coui coui 2','audio_20','0')," +
                "(21,'Pivert','audio_21','0')," +
                "(22,'PLac','audio_22','0')," +
                "(23,'Pouac','audio_23','0')," +
                "(24,'Praaa tactactac','audio_24','0')," +
                "(25,'Praaa tactactac 2','audio_25','0')," +
                "(26,'prui flac','audio_26','0')," +
                "(27,'prui flac 2 ','audio_27','0')," +
                "(28,'Gngbngngnn','audio_28','0')," +
                "(29,'Gngngngng 2','audio_29','0')," +
                "(30,'Gngngngng 3','audio_30','0')," +
                "(31,'Zzzzzz paf','audio_31','0')," +
                "(32,'Saligaud','audio_32','0')," +
                "(33,'Je vous chasse','audio_33','0')," +
                "(34,'Paf paf','audio_34','0')," +
                "(35,'Aaaaaah','audio_35','0')," +
                "(36,'Aaah','audio_36','0')," +
                "(37,'AAAAAAAAaaaaaaah','audio_37','0')," +
                "(38,'Je suis zinzin','audio_38','0')," +
                "(39,'Je suis zinzin 2','audio_39','0')," +
                "(40,'Qu''il est bon ce blaze','audio_40','0')," +
                "(41,'Priiii plam','audio_41','0')," +
                "(42,'Est ce que j''ai une tête de ptit coco?','audio_42','0')," +
                "(43,'Voila là voila là','audio_43','0')," +
                "(44,'Gnigningni','audio_44','0')," +
                "(45,'C''était pas mauvais c''était très mauvais!','audio_45','0')," +
                "(46,'Bon sang enfin!','audio_46','0')," +
                "(47,'Tatatittatata...','audio_47','0')," +
                "(48,'Tais toi tais toi','audio_48','0')," +
                "(49,'Mmmmmmmmmmm!','audio_49','0')," +
                "(50,'Mmmm que c''est bon!','audio_50','0')," +
                "(51,'Oooooooooo','audio_51','0')," +
                "(52,'Aie!','audio_52','0')," +
                "(53,'Alors moi y m''épate y m''épate y m''épate!','audio_53','0')," +
                "(54,'Eeeeh!','audio_54','0')," +
                "(55,'Ein salopard ein gross salopard!','audio_55','0')," +
                "(56,'Hiiinin bing','audio_56','0')," +
                "(57,'Hiiinin plof','audio_57','0')," +
                "(58,'Ma biche c''est un malentendu','audio_58','0')," +
                "(59,'Tu mens !','audio_59','0')," +
                "(60,'Ma biche je suis trop seul !','audio_60','0')," +
                "(61,'Ma biche','audio_61','0')," +
                "(62,'Ma biche je peux tout vous expliquer !','audio_62','0')," +
                "(63,'Oh mon eau minérale !','audio_63','0')";


    }
}
