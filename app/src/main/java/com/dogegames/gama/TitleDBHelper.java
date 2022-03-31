package com.dogegames.gama;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TitleDBHelper extends SQLiteOpenHelper {
    private static final String TAG="TitleDBHelper";

    private final Context context;
    private static TitleDBHelper instance;
    private static int SCHEMA_VERSION=1;
    static final String DATABASE_NAME="title.db";
    static final String TABLE_NAME="title";

    SQLiteDatabase db;

    public synchronized static TitleDBHelper getInstance(Context context){
        if(instance==null){
            instance=new TitleDBHelper(context.getApplicationContext(),SCHEMA_VERSION);
        }
        return instance;
    }

    public TitleDBHelper(Context context, int version){
        super(context,DATABASE_NAME,null,version);
        this.context=context;
        this.db=getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db=sqLiteDatabase;
        createTable(TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTable(String name){
        String createDB_query="CREATE TABLE IF NOT EXISTS "+name+" (no INTEGER PRIMARY KEY autoincrement, name TEXT, platform TEXT, maker TEXT, launchdate TEXT, imagepath TEXT, genre TEXT, memo TEXT, price INT, rating INT, ordering INT, id INT)";
        db.execSQL(createDB_query);
    }

    public void insertRecord(String tableName, String name, String platform, String maker, Date launchDate, String imagePath, String genre, String memo, int price, int rating, int ordering, String id){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String strDate=sdf.format(launchDate);
        String insertRecord_query="INSERT INTO "+tableName+" (name, platform, maker, launchdate, imagepath, genre, memo, price, rating, ordering, id) VALUES ("+"'"+name+"'"+","+"'"+platform+"'"+","+"'"+maker+"'"+","+"'"+strDate+"'"+","+"'"+imagePath+"'"+","+"'"+genre+"'"+","+"'"+memo+"'"+","+"'"+price+"'"+","+"'"+rating+"'"+","+"'"+ordering+"'"+","+"'"+id+"'"+")";
        db.execSQL(insertRecord_query);
    }

    public Cursor selectRecord(){
        String selectDB_query="SELECT * FROM "+TABLE_NAME+" ORDER BY ordering DESC";
        Cursor cursor=db.rawQuery(selectDB_query,null);

        return cursor;
    }

    public int getNo(){
        String getNo_query="SELECT * FROM "+TABLE_NAME;
        Cursor cursor=db.rawQuery(getNo_query,null);

        return cursor.getCount();
    }

    public void modifyRecord(String tableName, String name, String platform, String maker, Date launchDate, String imagePath, String genre, String memo, int price, int rating, String id){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String strDate=sdf.format(launchDate);
        String modifyRecord_query="UPDATE "+tableName+" SET name = "+"'"+name+"'"+", platform ="+"'"+platform+"'"+", maker = "+"'"+maker+"'"+", launchdate = "+"'"+strDate+"'"+", imagepath = "+"'"+imagePath+"'"+", genre = "+"'"+genre+"'"+", memo = "+"'"+memo+"'"+", price = "+"'"+price+"'"+", rating = "+"'"+rating+"'"+" WHERE id = "+"'"+id+"'";
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
