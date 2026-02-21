package Database.DatabaseUsages.TasksDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManager(Context c) {
        context = c;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String name, String desc, String checked, int val, int pos) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SUBJECT, name);
        contentValues.put(DatabaseHelper.DESC, desc);
        contentValues.put(DatabaseHelper.CHECKED, checked);
        contentValues.put(DatabaseHelper.POSITION, pos);
        contentValues.put(DatabaseHelper.REPEAT, 0);

        return database.insert(DatabaseHelper.TABLE_NAME + val, null, contentValues);
    }

    public Cursor fetch(int val) {
        String[] columns = new String[] {DatabaseHelper._ID,
        DatabaseHelper.SUBJECT, DatabaseHelper.DESC, DatabaseHelper.CHECKED,
                DatabaseHelper.POSITION, DatabaseHelper.REPEAT};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME + val,
                columns,
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String name, String desc, String checked, int val, int pos, int repeat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SUBJECT, name);
        contentValues.put(DatabaseHelper.DESC, desc);
        contentValues.put(DatabaseHelper.CHECKED, checked);
        contentValues.put(DatabaseHelper.POSITION, pos);
        contentValues.put(DatabaseHelper.REPEAT, repeat);

        return database.update(
                DatabaseHelper.TABLE_NAME + val,
                contentValues,
                DatabaseHelper._ID + " = " + _id,
                null);
    }

    public void delete(long _id, int val) {
        database.delete(
                DatabaseHelper.TABLE_NAME + val,
                DatabaseHelper._ID + " = " + _id,
                null);
    }

    public int update(long id, int day, ContentValues values) {
        return database.update(
                DatabaseHelper.TABLE_NAME + day,
                values,
                DatabaseHelper._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public void changeDatabases() {
        dbHelper.switchDatabases(database);
    }
}
