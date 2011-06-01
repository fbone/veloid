package com.xirgonium.android.place;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xirgonium.android.db.VeloidDBHelper;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.SQLRequestPart;
import com.xirgonium.exception.NoInternetConnection;

public class FavoritePlacesManager {

	protected SQLiteOpenHelper dbHelper;

	public FavoritePlacesManager(Context launched) {
		dbHelper = new VeloidDBHelper(launched);
	}

	public boolean saveFavoritePlaceIntoDB(FavoritePlace fav) {
		SQLiteDatabase myDB = null;
		try {

			myDB = dbHelper.getWritableDatabase();
			myDB.execSQL(SQLRequestPart.getDatabaseCreationSQLForPlaces());

			StringBuffer sqlInsert = new StringBuffer("INSERT INTO ");
			sqlInsert.append(Constant.DB_TABLE_PLACE);
			sqlInsert.append(" (");

			sqlInsert.append(Constant.DB_FIELD_S_ID);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_NAME);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_LATITUDE);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_LONGITUDE);

			sqlInsert.append(") VALUES (");

			sqlInsert.append("'");
			sqlInsert.append(fav.getId());
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(SQLRequestPart.addSlashes(fav.getName()));
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append(fav.getLatitude());
			sqlInsert.append(", ");

			sqlInsert.append(fav.getLongitude());

			sqlInsert.append(")");

			myDB.execSQL(sqlInsert.toString());

		} catch (Exception e) {
			Log.e("MGR", e.getMessage());
		} finally {
			if (myDB != null)
				myDB.close();
		}

		return true;
	}

	public Vector<FavoritePlace> getListOfFavoritePlaces() {
		Vector<FavoritePlace> fav = new Vector<FavoritePlace>();

		StringBuffer sqlSelect = new StringBuffer("SELECT ");
		sqlSelect.append(Constant.DB_FIELD_S_ID);
		sqlSelect.append(", ");

		sqlSelect.append(Constant.DB_FIELD_S_NAME);
		sqlSelect.append(", ");

		sqlSelect.append(Constant.DB_FIELD_S_LATITUDE);
		sqlSelect.append(", ");

		sqlSelect.append(Constant.DB_FIELD_S_LONGITUDE);

		sqlSelect.append(" FROM ");
		sqlSelect.append(Constant.DB_TABLE_PLACE);

		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getReadableDatabase();

			Log.d("DB", sqlSelect.toString());

			Cursor c = myDB.rawQuery(sqlSelect.toString(), null);

			/* Get the indices of the Columns we will need */
			int idColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_ID);
			int nameColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_NAME);
			int latColumn = c
					.getColumnIndexOrThrow(Constant.DB_FIELD_S_LATITUDE);
			int lngColumn = c
					.getColumnIndexOrThrow(Constant.DB_FIELD_S_LONGITUDE);

			if (c != null) {
				try {
					if (c.moveToFirst()) {
						do {
							FavoritePlace aFav = new FavoritePlace();

							Long id = c.getLong(idColumn);
							aFav.setId(id);
							Log.d(this.toString(), "FAV ID TROUVE : " + id);

							String name = c.getString(nameColumn);
							aFav.setName(name);

							float lat = c.getFloat(latColumn);
							aFav.setLatitude(lat);

							float lng = c.getFloat(lngColumn);
							aFav.setLongitude(lng);

							fav.add(aFav);
							// Log.d("MGR", "Found station " +
							// aStation.getId());

						} while (c.moveToNext());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					c.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myDB != null)
				myDB.close();
		}
		return fav;
	}
	
	public void cleanFavroitePlace() {
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append(Constant.DB_TABLE_PLACE);

		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getWritableDatabase();
			Log.d(this.getClass().getName(), "EXECUTE CLEAN REQUEST : " + sql.toString());
			myDB.execSQL(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myDB != null) {
				myDB.close();
			}
		}

	}

	public void deleteFavoritePlace(FavoritePlace fav) {
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append(Constant.DB_TABLE_PLACE);
		sql.append(" WHERE ");
		sql.append(Constant.DB_FIELD_S_ID);
		sql.append("=");
		sql.append(fav.getId());

		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getWritableDatabase();
			Log.d(this.getClass().getName(), "EXECUTE DELETE REQUEST : " + sql.toString());
			myDB.execSQL(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myDB != null) {
				myDB.close();
			}
		}

	}
}
