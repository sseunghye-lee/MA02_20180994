package ddwu.mobile.finalproject.ma02_20180994;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "movie_db";
    public final static String TABLE_NAME = "movie_table";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_ACTOR = "actor";
    public final static String COL_DIRECTOR = "director";
    public final static String COL_REVIEW = "review";
    public final static String COL_IMAGE = "image";
    public final static String COL_RATING = "rating";
    public final static String COL_THEATER = "theater";

    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_TITLE + " TEXT, " + COL_IMAGE + " TEXT, " + COL_ACTOR + " TEXT, " + COL_DIRECTOR + " TEXT, " + COL_REVIEW + " TEXT, "
                + COL_THEATER + " TEXT, "+ COL_RATING + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
