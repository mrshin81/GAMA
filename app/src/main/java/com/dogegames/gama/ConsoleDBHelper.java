package com.dogegames.gama;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleDBHelper extends SQLiteOpenHelper {

    private final Context context;

    private static ConsoleDBHelper instance;

    private final static String TAG="ConsoleDBHelper";

    private static int SCHEMA_VERSION=1;

    static final String DATABASE_NAME="console.db";

    static final String TABLE_NAME="console";

    SQLiteDatabase db;

    public synchronized static ConsoleDBHelper getInstance(Context context){
        if(instance==null){
            instance=new ConsoleDBHelper(context.getApplicationContext(), SCHEMA_VERSION);
        }
        return instance;
    }

    public ConsoleDBHelper(Context context, int version){
        super(context,DATABASE_NAME,null,version);
        this.context=context;
        db=getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db=sqLiteDatabase;
        createTable(TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createTable(String name){
        String createDB_query="CREATE TABLE IF NOT EXISTS "+name+" (no INTEGER PRIMARY KEY autoincrement, name TEXT, imagePath TEXT, price INT, date TEXT, memo TEXT, ordering INT, id TEXT)";
        db.execSQL(createDB_query);
    }

    public void insertRecord(String tableName, String name, String imagePath, int price, Date date, String memo, int ordering, String id){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String strDate=sdf.format(date);
        String insertRecord_query="INSERT INTO "+tableName+" (name, imagePath, price, date, memo, ordering, id) VALUES ("+"'"+name+"'"+","+"'"+imagePath+"'"+","+"'"+price+"'"+","+"'"+strDate+"'"+","+"'"+memo+"'"+","+"'"+ordering+"'"+","+"'"+id+"'"+")";
        db.execSQL(insertRecord_query);
    }

    public Cursor selectRecord(){
        String selectDB_query="SELECT * FROM "+TABLE_NAME+" ORDER BY ordering DESC";
        Cursor cursor=db.rawQuery(selectDB_query,null);
        if(cursor.getCount()!=0){
            return cursor;
        }

        return null;
    }

    public int getNo(){
        String getNo_query="SELECT * FROM "+TABLE_NAME;
        Cursor cursor=db.rawQuery(getNo_query,null);

        return cursor.getCount();
    }

    public void modifyRecord(String tableName, String name, String imagePath, int price, Date date, String memo, String id){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String strDate=sdf.format(date);
        String modifyRecord_query="UPDATE "+tableName+" SET name = "+"'"+name+"'"+", imagePath ="+"'"+imagePath+"'"+", price = "+"'"+price+"'"+", date = "+"'"+strDate+"'"+", memo = "+"'"+memo+"'"+" WHERE id = "+"'"+id+"'";      //(name, imagePath, price, date, memo) VALUES ("+"'"+name+"'"+","+"'"+imagePath+"'"+","+"'"+price+"'"+","+"'"+strDate+"'"+","+"'"+memo+"'"+")";
        db.execSQL(modifyRecord_query);
    }

    public void modifyOrdering(String tableName, int preOrdering, int movedOrdering){
        String modifyOrdering_query1="UPDATE "+tableName+" SET ordering = "+"'"+999999999+"'"+" WHERE ordering = "+"'"+movedOrdering+"'";
        String modifyOrdering_query2="UPDATE "+tableName+" SET ordering = "+"'"+movedOrdering+"'"+" WHERE ordering = "+"'"+preOrdering+"'";
        String modifyOrdering_query3="UPDATE "+tableName+" SET ordering = "+"'"+preOrdering+"'"+" WHERE ordering = "+"'"+999999999+"'";
        db.execSQL(modifyOrdering_query1);
        db.execSQL(modifyOrdering_query2);
        db.execSQL(modifyOrdering_query3);
        Log.d(TAG,"modifyOrdering "+preOrdering+"->"+movedOrdering);
        //PreferenceManager.setInt(context,ConsoleRecyclerViewAdapter.SELECTED_CONSOLE_ITEM_NUMBER,);
    }


    public void deleteRecord(String tableName, String id){
        String deleteRecord_query="DELETE FROM "+tableName+" WHERE id = "+"'"+id+"'";
        db.execSQL(deleteRecord_query);
        Log.d(TAG,"deleteRecord : "+id);
    }
}
