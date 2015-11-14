package idv.hsu.metrowifirecorder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbSchema.class.getSimpleName();
    private static final boolean D = false;

    private SQLiteDatabase db;
    private Context mContext;
    private String DB_PATH = "";

    private static final int VERSION = 1;

    public DbHelper(Context context) {
        super(context, DbSchema.DB_NAME, null, VERSION);
        mContext = context;
        DB_PATH = context.getDatabasePath(DbSchema.DB_NAME).toString();
    }

    public void create() throws IOException {
        boolean check = checkDatabase();

        SQLiteDatabase db_read = null;

        db_read = this.getWritableDatabase();
        db_read.close();
        SQLiteDatabase db_create = null;
        try {
            if (!check) {
                copyDatabase();

                db_create = this.getWritableDatabase();
                db_create.execSQL("CREATE TABLE log(" +
                        "_id INTEGER, " +
                        "STATION TEXT NOT NULL, " +
                        "BSSID TEXT NOT NULL, " +
                        "SSID TEXT, " +
                        "CAPABILITIES TEXT, " +
                        "FREQUENCY TEXT, " +
                        "LEVEL TEXT, " +
                        "PRIMARY KEY(STATION, BSSID));");
            }
        } catch (IOException ioe) {
            throw new Error("Error copying database." + ioe.getLocalizedMessage());
        } finally {
            if (db_create != null) {
                db_create.close();
            }
        }
    }

    private boolean checkDatabase() {
        SQLiteDatabase db_check = null;
        try {
            db_check = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException sqle) {
        }
        if (db_check != null) {
            db_check.close();
        }
        return (db_check != null) ? true : false;
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = mContext.getAssets().open(DbSchema.DB_NAME);
        OutputStream outputStream = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void open() throws SQLiteException {
        db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public long insertTracking(ContentValues values) {
        long result = db.insert(DbSchema.TABLE_TRACKING, null, values);

        return result;
    }

    public Cursor queryTracking(String station) {
        Cursor cursor = db.query(DbSchema.TABLE_TRACKING,
                new String[]{
                        DbSchema._ID,
                        DbSchema.BSSID,
                        DbSchema.SSID,
                        DbSchema.CAPABILITIES,
                        DbSchema.FREQUENCY,
                        DbSchema.LEVEL,
                        DbSchema.STATION},
                DbSchema.STATION + "=?", new String[] {station}, null, null, null);
//        Cursor cursor = db.rawQuery("SELECT  * FROM " + DbSchema.TABLE_TRACKING, null);
        if (cursor != null) {
            cursor.moveToFirst();
//            db.close();
            return cursor;
        }
//        db.close();
        return null;
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
