package com.xirgonium.android.manager;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xirgonium.android.config.ConfigurationContext;
import com.xirgonium.android.db.VeloidDBHelper;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.util.Constant;
import com.xirgonium.android.util.SQLRequestPart;
import com.xirgonium.exception.NoInternetConnection;

/**
 * 
 * When you add a new system :
 * <ul>
 * <li>Create a new class</li>
 * <li>change the file res/raw/network_manager.xml</li>
 * </ul>
 * 
 * That's it !
 * 
 * @author gervais
 * 
 */

public abstract class CommonStationManager {

	protected SQLiteOpenHelper	dbHelper;

	protected String			networkID			= null;
	protected String			commonName			= null;
	protected boolean			ownSpecificAction	= false;
	

	public CommonStationManager(Context launched) {
		dbHelper = new VeloidDBHelper(launched);
	}

	public CommonStationManager() {

	}

	public void assignDbHelper(Context launched) {
		this.dbHelper = new VeloidDBHelper(launched);
	}

	public void setNetworkId(String id) {
		this.networkID = id;
	}

	public String getNetworkId() {
		return networkID;
	}

	public void setCommonName(String name) {
		this.commonName = name;
	}

	public String getCommonName() {
		return commonName;
	}

	/**
	 * 
	 * Get on the Velib APIs the informations related to all Stations.
	 * 
	 * @param aStation
	 * @return The station given as parameter but completed with Velib information
	 * 
	 */
	public abstract void updateStationListDynamicaly() throws NoInternetConnection;

	/**
	 * 
	 * Get on the Velib APIs the informations related to a Station.
	 * 
	 * @param aStation
	 * @return The station given as parameter but completed with Velib information
	 * 
	 */
	public abstract Station fillDynamicInformationForAStation(Station aStation) throws NoInternetConnection;

	/**
	 * Clear the list of the station in the database. Use the clearListOfStationFromDatabaseImpl(String network) method when overriden.
	 * 
	 */
	public abstract void clearListOfStationFromDatabase();

	/**
	 * Restore all the favorite of a network. Use the restoreFavoriteFromDataBaseAndWebImpl(String network) method when overriden.
	 */
	public abstract Vector<Station> restoreFavoriteFromDataBaseAndWeb() throws NoInternetConnection;

	/**
	 * Restore all the favorite of a network, but no connection internet done. Use the restoreFavoriteFromDataBaseImpl(String network) method when overriden.
	 */
	public abstract Vector<Station> restoreFavoriteFromDataBase();

	/**
	 * Restore the minimum information from base and web. When override, please use public Vector<Station> restoreAllStationWithminimumInfoFromDataBaseImpl(String network)
	 * 
	 * @return
	 */
	public abstract Vector<Station> restoreAllStationWithminimumInfoFromDataBase();

	/**
	 * Mix information from db and web. use the fillInformationFromDBAndWeb when override;
	 * 
	 * @param stations
	 * @return
	 */
	public abstract Vector<Station> fillInformationFromDBAndWeb(Vector<Station> stations) throws NoInternetConnection;

	/**
	 * information from db. use the fillInformationFromDBImpl when override;
	 * 
	 * @param stations
	 * @return
	 */
	public abstract Vector<Station> fillInformationFromDB(Vector<Station> stations);

	/**
	 * Mainly used to facilitate the search by address of a customer
	 * 
	 * @return
	 */
	public abstract String[] getCities();

	public boolean canExecuteASpecificAction() {
		return ownSpecificAction;
	}

	public void setOwnSpecificAction(boolean ownSpecificAction) {
		this.ownSpecificAction = ownSpecificAction;
	}
	
	public boolean isSupportedbyMolib(){
		return false;
	}

	/**
	 * Used to trigger a special action on the main menu. Override for each network that needs it.
	 */
	public void specialAction(Context ctx) {
	}

	public String getSpecialActionText(Context ctx) {
		return null;
	}

	public void saveStationIntoDB(Station aStation) {
		SQLiteDatabase myDB = null;
		try {

			myDB = dbHelper.getWritableDatabase();
			myDB.execSQL(SQLRequestPart.getDatabaseCreationSQLForStation());

			StringBuffer sqlInsert = new StringBuffer("INSERT INTO ");
			sqlInsert.append(Constant.DB_TABLE_STATIONS);
			sqlInsert.append(" (");

			sqlInsert.append(Constant.DB_FIELD_S_ID);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_NAME);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_ADDRESS);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_FULL_ADRESS);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_LATITUDE);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_LONGITUDE);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_OPEN);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_COMMENT);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_NETWORK);
			sqlInsert.append(", ");

			sqlInsert.append(Constant.DB_FIELD_S_COLOR);
			sqlInsert.append(", ");
			
			sqlInsert.append(Constant.DB_FIELD_S_SIGNET);
			sqlInsert.append(") VALUES (");

			sqlInsert.append("'");
			sqlInsert.append(aStation.getId());
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(SQLRequestPart.addSlashes(aStation.getName()));
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(SQLRequestPart.addSlashes(aStation.getAddress()));
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(SQLRequestPart.addSlashes(aStation.getFullAddress()));
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append(aStation.getLatitude());
			sqlInsert.append(", ");

			sqlInsert.append(aStation.getLongitude());
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(aStation.getOpen());
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(SQLRequestPart.addSlashes(aStation.getComment()));
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append("'");
			sqlInsert.append(SQLRequestPart.addSlashes(aStation.getNetwork()));
			sqlInsert.append("'");
			sqlInsert.append(", ");
			
			sqlInsert.append("'");
			sqlInsert.append(aStation.getFavoriteColor());
			sqlInsert.append("'");
			sqlInsert.append(", ");

			sqlInsert.append(aStation.getFavorite());
			sqlInsert.append(")");

			// Log.d("MGR", "Save station SQL : " + sqlInsert.toString());
			myDB.execSQL(sqlInsert.toString());

			/*
			 * ContentValues values = new ContentValues(); values.put(Constant.DB_FIELD_S_ID, aStation.getId()); values.put(Constant.DB_FIELD_S_COMMENT, aStation.getComment());
			 * values.put(Constant.DB_FIELD_S_FULL_ADRESS, aStation.getFullAddress()); values.put(Constant.DB_FIELD_S_ADDRESS, aStation.getAddress()); values.put(Constant.DB_FIELD_S_LATITUDE,
			 * aStation.getLatitude()); values.put(Constant.DB_FIELD_S_LONGITUDE, aStation.getLongitude()); values.put(Constant.DB_FIELD_S_NAME, aStation.getName());
			 * values.put(Constant.DB_FIELD_S_OPEN, aStation.getOpen()); //values.put(Constant.DB_FIELD_S_SIGNET, aStation.());
			 */

			// myDB.insert(Constant.DB_TABLE_STATIONS, Constant.DB_FIELD_S_ID,
			// values);
		} catch (Exception e) {
			Log.e("MGR", e.getMessage());
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}

	public void setStationAsSignet(Station aStation) {
		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getWritableDatabase();

			StringBuffer update = new StringBuffer("UPDATE ");
			update.append(Constant.DB_TABLE_STATIONS);
			update.append(" SET ");
			update.append(Constant.DB_FIELD_S_SIGNET);
			update.append("=1 ");
			update.append(" WHERE ");
			update.append(Constant.DB_FIELD_S_ID);
			update.append("='");
			update.append(aStation.getId());
			update.append("' AND ");
			update.append(Constant.DB_FIELD_S_NETWORK);
			update.append("='");
			update.append(aStation.getNetwork());
			update.append("'");

			Log.d("MGR", "Execute SQL " + update.toString());
			myDB.execSQL(update.toString());

		} catch (Exception e) {
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}

	public void updateStation(Station aStation) {
		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getWritableDatabase();

			StringBuffer update = new StringBuffer("UPDATE ");
			update.append(Constant.DB_TABLE_STATIONS);
			update.append(" SET ");
			update.append(Constant.DB_FIELD_S_COLOR);
			update.append("=");
			update.append(aStation.getFavoriteColor());
			update.append(", ");
			update.append(Constant.DB_FIELD_S_COMMENT);
			update.append("='");
			update.append(aStation.getComment());
			update.append("' WHERE ");
			update.append(Constant.DB_FIELD_S_ID);
			update.append("='");
			update.append(aStation.getId());
			update.append("' AND ");
			update.append(Constant.DB_FIELD_S_NETWORK);
			update.append("='");
			update.append(aStation.getNetwork());
			update.append("'");

			 Log.d("MGR", "Execute SQL " + update.toString());
			myDB.execSQL(update.toString());

		} catch (Exception e) {
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}

	public void removeStationAsSignet(Station aStation) {
		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getWritableDatabase();

			StringBuffer update = new StringBuffer("UPDATE ");
			update.append(Constant.DB_TABLE_STATIONS);
			update.append(" SET ");
			update.append(Constant.DB_FIELD_S_SIGNET);
			update.append("=0 ");
			update.append(" WHERE ");
			update.append(Constant.DB_FIELD_S_ID);
			update.append("='");
			update.append(aStation.getId());
			update.append("' AND ");
			update.append(Constant.DB_FIELD_S_NETWORK);
			update.append("='");
			update.append(aStation.getNetwork());
			update.append("'");

			// Log.d("MGR", "Execute SQL " + update.toString());
			myDB.execSQL(update.toString());

		} catch (Exception e) {
			Log.e("MGR", e.getMessage());
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}

	void clearListOfStationFromDatabaseImpl(String network) {
		SQLiteDatabase myDB = null;
		try {
			Log.i("MGR", "Delete stations from db");
			myDB = dbHelper.getWritableDatabase();
			myDB.execSQL("DELETE FROM " + Constant.DB_TABLE_STATIONS + " WHERE " + Constant.DB_FIELD_S_NETWORK + "='" + network + "'");

		} catch (Exception e) {
		} finally {
			if (myDB != null)
				myDB.close();
		}
	}

	/**
	 * Return the favorite station stored by the user
	 * 
	 * @return
	 */
	Vector<Station> restoreFavoriteFromDataBaseAndWebImpl(String network) throws NoInternetConnection {

		StringBuffer sqlSelect = new StringBuffer(SQLRequestPart.getSelectFieldsForStations());
		sqlSelect.append(" FROM ");
		sqlSelect.append(Constant.DB_TABLE_STATIONS);
		sqlSelect.append(" WHERE ");
		sqlSelect.append(Constant.DB_FIELD_S_NETWORK);
		sqlSelect.append(" = '");
		sqlSelect.append(network);
		sqlSelect.append("' AND ");
		sqlSelect.append(Constant.DB_FIELD_S_SIGNET);
		sqlSelect.append(" = 1");

		Vector<Station> found = getStationsFoundFromSQLRequest(sqlSelect.toString(), true);

		return found;

	}

	/**
	 * Return the favorite station stored by the user
	 * 
	 * @return
	 */
	Vector<Station> restoreFavoriteFromDataBaseImpl(String network) {

		StringBuffer sqlSelect = new StringBuffer(SQLRequestPart.getSelectFieldsForStations());
		sqlSelect.append(" FROM ");
		sqlSelect.append(Constant.DB_TABLE_STATIONS);
		sqlSelect.append(" WHERE ");
		sqlSelect.append(Constant.DB_FIELD_S_NETWORK);
		sqlSelect.append(" = '");
		sqlSelect.append(network);
		sqlSelect.append("' AND ");
		sqlSelect.append(Constant.DB_FIELD_S_SIGNET);
		sqlSelect.append(" = 1");

		Vector<Station> found = null;
		try {
			found = getStationsFoundFromSQLRequest(sqlSelect.toString(), false);
		} catch (NoInternetConnection e) {
			// can not happen
		}

		return found;

	}

	/**
	 * Return the all the stations stored by the user
	 * 
	 * @return
	 */
	// public Vector<Station> restoreAllStationFromDataBase() {
	// Log.d("MGR.DB", "Start the restoration stations from database");
	// StringBuffer sqlSelect = new
	// StringBuffer(SQLRequestPart.getSelectFieldsForStations());
	// sqlSelect.append(" FROM ");
	// sqlSelect.append(Constant.DB_TABLE_STATIONS);
	// sqlSelect.append(";");
	// Log.d("MGR.DB", "Finish the restoration stations from database");
	// return getStationsFoundFromSQLRequest(sqlSelect.toString(), false);
	//
	// }
	/**
	 * Return the all the stations stored by the user
	 * 
	 * @return
	 */
	public Vector<Station> restoreAllStationWithminimumInfoFromDataBaseImpl(String network) {
		// Log.d("MGR.DB", "Start the minimum restoration stations from database");
		StringBuffer sqlSelect = new StringBuffer(SQLRequestPart.getSelectFieldsForStations());
		sqlSelect.append(" FROM ");
		sqlSelect.append(Constant.DB_TABLE_STATIONS);
		sqlSelect.append(" WHERE ");
		sqlSelect.append(Constant.DB_FIELD_S_NETWORK);
		sqlSelect.append("='");
		sqlSelect.append(network);
		sqlSelect.append("'");
		// Log.d("MGR.DB", "Finish the minimum restoration stations from database");
		return getStationsMinimumInfoFoundFromSQLRequest(sqlSelect.toString());

	}

	public Vector<Station> fillInformationFromDBAndWebImpl(String network, Vector<Station> stations) throws NoInternetConnection {

		StringBuffer sqlSelect = new StringBuffer(SQLRequestPart.getSelectFieldsForStations());
		sqlSelect.append(" FROM ");
		sqlSelect.append(Constant.DB_TABLE_STATIONS);
		sqlSelect.append(" WHERE ");
		sqlSelect.append(Constant.DB_FIELD_S_NETWORK);
		sqlSelect.append("='");
		sqlSelect.append(network);
		;
		sqlSelect.append("' AND ");

		int max = stations.size();
		for (int i = 0; i < stations.size(); i++) {
			Station s = stations.get(i);
			sqlSelect.append(Constant.DB_FIELD_S_ID);
			sqlSelect.append(" = '");
			sqlSelect.append(s.getId());
			sqlSelect.append((i != (max - 1)) ? "' OR " : "';");
		}

		return getStationsFoundFromSQLRequest(sqlSelect.toString(), true);
	}

	public Vector<Station> fillInformationFromDBImpl(String network, Vector<Station> stations) {

		// stations is already from web

		StringBuffer sqlSelect = new StringBuffer(SQLRequestPart.getSelectFieldsForStations());
		sqlSelect.append(" FROM ");
		sqlSelect.append(Constant.DB_TABLE_STATIONS);
		sqlSelect.append(" WHERE ");

		sqlSelect.append(Constant.DB_FIELD_S_NETWORK);
		sqlSelect.append("='");
		sqlSelect.append(network);
		;
		sqlSelect.append("' AND ");

		int max = stations.size();
		for (int i = 0; i < stations.size(); i++) {
			Station s = stations.get(i);
			sqlSelect.append(Constant.DB_FIELD_S_ID);
			sqlSelect.append(" = '");
			sqlSelect.append(s.getId());
			sqlSelect.append((i != (max - 1)) ? "' OR " : "';");
		}

		try {
			Vector<Station> stationFilled = getStationsFoundFromSQLRequest(sqlSelect.toString(), false);

			for (Station station : stationFilled) {
				for (Station initial : stations) {
					if (initial.getId().equals(station.getId())) {
						// same station, can fill information
						initial.setAddress(station.getAddress());
						initial.setComment(station.getComment());
						initial.setFavorite(station.getFavorite());
						initial.setFavoriteColor(station.getFavoriteColor());
					}
				}
			}
		} catch (NoInternetConnection e) {
			// can not happen
		}

		return stations;
	}

	public Vector<Station> fillInformationFromWeb(Vector<Station> stations) throws NoInternetConnection {
		Vector<Station> updated = new Vector<Station>();
		for (Station station : stations) {
			updated.add(fillDynamicInformationForAStation(station));

		}
		return updated;
	}

	public Vector<Station> getStationsFoundFromSQLRequest(String sql, boolean updateWeb) throws NoInternetConnection {
		Vector<Station> stations = new Vector<Station>();

		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getReadableDatabase();

			Log.d("DB", sql);

			Cursor c = myDB.rawQuery(sql, null);

			/* Get the indices of the Columns we will need */
			int idColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_ID);
			int nameColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_NAME);
			int lblColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_COMMENT);
			int addrColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_ADDRESS);
			int fullAddrColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_FULL_ADRESS);
			int latColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_LATITUDE);
			int lngColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_LONGITUDE);
			int openColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_OPEN);
			int signetColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_SIGNET);
			int networkColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_NETWORK);
			int colorColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_COLOR);

			if (c != null) {
				try {
					if (c.moveToFirst()) {
						do {
							Station aStation = new Station();

							String id = c.getString(idColumn);
							aStation.setId(id);

							String label = c.getString(lblColumn);
							aStation.setComment(label);

							String name = c.getString(nameColumn);
							aStation.setName(name);

							String network = c.getString(networkColumn);
							aStation.setNetwork(network);

							String address = c.getString(addrColumn).replace(" -", "").trim();
							aStation.setAddress(address);

							String fullAddress = c.getString(fullAddrColumn);
							aStation.setFullAddress(fullAddress);

							float lat = c.getFloat(latColumn);
							aStation.setLatitude(lat);

							float lng = c.getFloat(lngColumn);
							aStation.setLongitude(lng);

							String open = c.getString(openColumn);
							aStation.setOpen(open);

							int signet = c.getInt(signetColumn);
							aStation.setFavorite(signet);
							
							int color = c.getInt(colorColumn);
							aStation.setFavoriteColor(color);

							if (updateWeb) {
								aStation = fillDynamicInformationForAStation(aStation);
							}
							stations.add(aStation);
							// Log.d("MGR", "Found station " + aStation.getId());

						} while (c.moveToNext());
					}
				} catch (NoInternetConnection nie) {
					throw new NoInternetConnection(nie.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					c.close();
				}
			}

		} catch (NoInternetConnection nie) {
			throw new NoInternetConnection(nie.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myDB != null)
				myDB.close();
		}

		return stations;
	}

	public Vector<Station> getStationsMinimumInfoFoundFromSQLRequest(String sql) {
		Vector<Station> stations = new Vector<Station>();

		SQLiteDatabase myDB = null;
		try {
			myDB = dbHelper.getWritableDatabase();
			// Log.d("MGR", "Start executing SQL");
			// myDB.execSQL(SQLRequestPart.getDatabaseCreationSQL());
			// Log.d("MGR", "End executing SQL");

			// Log.d("MGR", "Start cursor SQL");
			Cursor c = myDB.rawQuery(sql, null);

			/* Get the indices of the Columns we will need */
			int idColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_ID);
			int latColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_LATITUDE);
			int lngColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_LONGITUDE);
			int nameColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_NAME);
			int networkColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_NETWORK);
			int colorColumn = c.getColumnIndexOrThrow(Constant.DB_FIELD_S_COLOR);

			if (c != null) {
				try {
					if (c.moveToFirst()) {
						do {
							Station aStation = new Station();

							String id = c.getString(idColumn);
							aStation.setId(id);

							float lat = c.getFloat(latColumn);
							aStation.setLatitude(lat);

							float lng = c.getFloat(lngColumn);
							aStation.setLongitude(lng);

							String name = c.getString(nameColumn);
							aStation.setName(name);
							
							String network = c.getString(networkColumn);
							aStation.setNetwork(network);
							
							int color = c.getInt(colorColumn);
							aStation.setFavoriteColor(color);

							stations.add(aStation);

						} while (c.moveToNext());
					}
				} catch (Exception e) {

					e.printStackTrace();
				} finally {
					c.close();
				}
			}
			// Log.d("MGR", "end cursor SQL");
		}

		catch (Exception e) {
		} finally {
			if (myDB != null)
				myDB.close();
		}

		return stations;
	}

	public Vector<Station> foundStationFromParameters(String pattern) {

		StringBuffer sql = new StringBuffer(SQLRequestPart.getSelectFieldsForStations());
		sql.append(" FROM ");
		sql.append(Constant.DB_TABLE_STATIONS);
		sql.append(" WHERE ");
		sql.append(Constant.DB_FIELD_S_NETWORK);
		sql.append("='");
		sql.append(ConfigurationContext.getNetwork());
		;
		sql.append("' AND (");
		// Here are the selection
		sql.append(SQLRequestPart.getAttributeLike(Constant.DB_FIELD_S_ID, pattern, SQLRequestPart.END_TYPE_OR));
		sql.append(SQLRequestPart.getAttributeLike(Constant.DB_FIELD_S_FULL_ADRESS, pattern, SQLRequestPart.END_TYPE_OR));
		sql.append(SQLRequestPart.getAttributeLike(Constant.DB_FIELD_S_NAME, pattern, SQLRequestPart.END_TYPE_PARENTHESIS_POINT_COMMA));

		Vector<Station> stations = null;
		try {
			stations = getStationsFoundFromSQLRequest(sql.toString(), false);
		} catch (NoInternetConnection e) {
			// can not happen
		}

		return stations;

	}

	public boolean isThereAtLeastOneStationInDBForNetwork() {
		return (getNbStationsInDB() > 0);
	}

	public int getNbStationsInDB() {
		StringBuffer sql = new StringBuffer("SELECT COUNT(*)");
		sql.append(" FROM ");
		sql.append(Constant.DB_TABLE_STATIONS);
		sql.append(" WHERE ");
		sql.append(Constant.DB_FIELD_S_NETWORK);
		sql.append("='");
		sql.append(getNetworkId());
		sql.append("'");
		// Log.d("SQL", sql.toString());

		SQLiteDatabase myDB = null;

		try {
			int number = 0;
			myDB = dbHelper.getReadableDatabase();
			// Log.d("MGR", "Start executing SQL");
			myDB.execSQL(SQLRequestPart.getDatabaseCreationSQLForStation());
			// Log.d("MGR", "End executing SQL");

			// Log.d("MGR", "Start cursor SQL");
			Cursor c = myDB.rawQuery(sql.toString(), null);

			if (c != null) {
				try {
					if (c.moveToFirst()) {
						number = c.getInt(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					c.close();
				}
			}

			// Log.d("MGR", number + " stations for the network " + getNetworkId());

			return number;
		} catch (SQLException e) {
			// Log.e("SQL", "ERROR HAPPENED " + e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	// public abstract int getMenuSelectionIndex();

}
