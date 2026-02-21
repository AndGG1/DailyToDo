package Database.DatabaseUsages.TasksDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String TABLE_NAME = "TASKS_";

    public static final String _ID = "_id";
    public static final String SUBJECT = "subject";
    public static final String DESC = "description";
    public static final String CHECKED = "checked";
    public static final String POSITION = "position";
    public static final String REPEAT = "repeat";

    static final String DB_NAME = "DailyToDo_App.DB";
    static final int DB_VERSION = 6;

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SUBJECT + " TEXT NOT NULL, "
                    + DESC + " TEXT NOT NULL, "
                    + CHECKED + " TEXT NOT NULL,"
                    + POSITION + " INTEGER NOT NULL,"
                    + REPEAT + " INTEGER NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE.replace(TABLE_NAME, TABLE_NAME + "1"));
        db.execSQL(CREATE_TABLE.replace(TABLE_NAME, TABLE_NAME + "2"));
        db.execSQL(CREATE_TABLE.replace(TABLE_NAME, TABLE_NAME + "3"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + (TABLE_NAME + "1"));
        db.execSQL("DROP TABLE IF EXISTS " + (TABLE_NAME + "2"));
        db.execSQL("DROP TABLE IF EXISTS " + (TABLE_NAME + "3"));

        onCreate(db);
    }

    public void switchDatabases(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO " + (TABLE_NAME + "3") +
                    " SELECT * FROM " + (TABLE_NAME + "1") +
                    " WHERE repeat = 1");

            db.execSQL("INSERT INTO " + (TABLE_NAME + "3") +
                    " SELECT * FROM " + (TABLE_NAME + "2") +
                    " WHERE repeat = 1");

            // 1. Clear TASKS_1
            db.execSQL("DELETE FROM " + (TABLE_NAME + "1"));

            // 2. Copy TASKS_2 -> TASKS_1
            db.execSQL("INSERT INTO " + (TABLE_NAME + "1") +
                        " SELECT * FROM " + (TABLE_NAME + "2"));

            // 3. Clear TASKS_2
            db.execSQL("DELETE FROM " + (TABLE_NAME + "2"));

            // 4. Copy TASKS_3 -> TASKS_2
            db.execSQL("INSERT INTO " + (TABLE_NAME + "2") +
                        " SELECT * FROM " + (TABLE_NAME + "3"));

            // 5. Clear TASKS_3 (optional: keep empty for reuse)
            db.execSQL("DELETE FROM " + (TABLE_NAME + "3")
                    + " WHERE repeat = 0");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
