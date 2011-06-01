package com.xirgonium.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.SQLRequestPart;

public class VeloidDBHelper extends SQLiteOpenHelper {

  public VeloidDBHelper(Context context) {
    super(context, Constant.DB_DATABASE_NAME, null, Constant.DB_VERSION);
  }

  public void onCreate(SQLiteDatabase db) {
    // Log.d("DATABASE", "Create");
    db.execSQL(SQLRequestPart.getDatabaseCreationSQLForStation());
    //db.execSQL(SQLRequestPart.getDatabaseCreationSQLForPlaces());
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w("MGR", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
    if(newVersion == 2){
    	String updateSQL = "ALTER TABLE "+ Constant.DB_TABLE_STATIONS+ " ADD COLUMN color DECIMAL;";
    	Log.w("DBHELPER", "UPDATE TABLE " + updateSQL);
    	 db.execSQL(updateSQL);
    }
  }

}
