package nemosofts.driving.exam.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import nemosofts.driving.exam.item.ItemCat;
import nemosofts.driving.exam.item.ItemLanguage;
import nemosofts.driving.exam.item.ItemResult;

public class DBHelper extends SQLiteOpenHelper {

    private final EncryptData encryptData;
    private static final String DB_NAME = "exam.db";
    private final SQLiteDatabase db;

    private static final String TAG_ID = "id";

    private static final String TABLE_LAN = "lan";
    private static final String TAG_LAN_ID = "lid";
    private static final String TAG_LAN_NAME = "lname";

    private static final String TABLE_CAT = "cat";
    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "cname";
    private static final String TAG_CAT_IMG = "cimg";

    public static final String TABLE_RESULT = "result";
    private static final String TAG_DATE = "date";
    private static final String TAG_TIME = "time";
    private static final String TAG_RESULT = "exam_result";

    private final String[] columns_lan = new String[]{TAG_ID, TAG_LAN_ID, TAG_LAN_NAME};
    private final String[] columns_cat = new String[]{TAG_ID, TAG_CAT_ID, TAG_CAT_NAME, TAG_CAT_IMG};
    private final String[] columns_result = new String[]{TAG_ID, TAG_DATE, TAG_TIME, TAG_RESULT};

    // Creating table query
    private static final String CREATE_TABLE_LAN = "create table " + TABLE_LAN + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_LAN_ID + " TEXT," +
            TAG_LAN_NAME + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_CAT = "create table " + TABLE_CAT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_CAT_IMG + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_RESULT = "create table " + TABLE_RESULT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_DATE + " TEXT," +
            TAG_TIME + " TEXT," +
            TAG_RESULT + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 5);
        encryptData = new EncryptData(context);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_LAN);
            db.execSQL(CREATE_TABLE_CAT);
            db.execSQL(CREATE_TABLE_RESULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    public ArrayList<ItemResult> getResult(String table) {
        ArrayList<ItemResult> arrayList = new ArrayList<>();

        Cursor cursor = db.query(table, columns_result, null, null, null, null, TAG_ID + " DESC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor.getColumnIndex(TAG_ID));
                String date = cursor.getString(cursor.getColumnIndex(TAG_DATE));
                String time = cursor.getString(cursor.getColumnIndex(TAG_TIME));
                String result = cursor.getString(cursor.getColumnIndex(TAG_RESULT));
                ItemResult objItem = new ItemResult(id, date, time, result);
                arrayList.add(objItem);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<ItemLanguage> getLanguage() {
        ArrayList<ItemLanguage> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_LAN, columns_lan, null, null, null, null, TAG_ID + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor.getColumnIndex(TAG_LAN_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_LAN_NAME)).replace("%27", "'");

                ItemLanguage itemLanguage = new ItemLanguage(id, title);
                arrayList.add(itemLanguage);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<ItemCat> getCat() {
        ArrayList<ItemCat> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_CAT, columns_cat, null, null, null, null, TAG_ID + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String imageBig = encryptData.decrypt(cursor.getString(cursor.getColumnIndex(TAG_CAT_IMG)));

                String id = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String title = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME)).replace("%27", "'");

                ItemCat itemCat = new ItemCat(id, title,imageBig);
                arrayList.add(itemCat);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    public void addedLanguageList(@NonNull ItemLanguage itemLanguage) {
        String name = itemLanguage.getName().replace("'", "%27");

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_LAN_ID, itemLanguage.getId());
        contentValues.put(TAG_LAN_NAME, name);

        db.insert(TABLE_LAN, null, contentValues);
    }

    public void addedCatList(@NonNull ItemCat itemCat) {
        String imageBig = encryptData.encrypt(itemCat.getImage());

        String name = itemCat.getName().replace("'", "%27");

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_CAT_ID, itemCat.getId());
        contentValues.put(TAG_CAT_NAME, name);
        contentValues.put(TAG_CAT_IMG, imageBig);

        db.insert(TABLE_CAT, null, contentValues);
    }

    public void addedResult(String date, String time, String result) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_DATE, date);
        contentValues.put(TAG_TIME, time);
        contentValues.put(TAG_RESULT, result);

        db.insert(TABLE_RESULT, null, contentValues);
    }

    public void removeAllLanguage() {
        db.delete(TABLE_LAN, null, null);
    }

    public void removeAllCat() {
        db.delete(TABLE_CAT, null, null);
    }

    public void removeAllResult() {
        db.delete(TABLE_RESULT, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}