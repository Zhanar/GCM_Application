package com.zhanar.gcm_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;

import java.util.ArrayList;

/**
 * Created by Жанар on 26-May-16.
 */
public class DataController {

    public interface ObjectCallback {
        void callback(Object o);
    }

    private static DataController instance;
    private Context context;
    private AQuery aq;
    private DatabaseHelper dbHelper;

    public static DataController getInstance(Context context) {
        if (instance == null) {
            instance = new DataController(context);
        }
        return instance;
    }

    private DataController(Context context) {
        AQUtility.setDebug(true);
        this.context = context.getApplicationContext();
        this.aq = new AQuery(context);
        this.dbHelper = new DatabaseHelper(this.context);
    }

    public ArrayList<MessageInfo> getMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<MessageInfo> lst = new ArrayList<>();
        Cursor c = db.rawQuery("select * from messages", null);
        try {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                MessageInfo item = null;
                try {
                    item = new MessageInfo();
                    item.setId(c.getInt(0));
                    item.setText(c.getString(1));
                    item.setCreatedAt(c.getString(2));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (item != null) {
                    lst.add(item);
                }
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            dbHelper.close();
        }

        return lst;
    }

    synchronized public void saveMessage(MessageInfo messageInfo) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        values.put("text", messageInfo.getText());
        db.insert("messages", null, values);
        dbHelper.close();
    }
}
