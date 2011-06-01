package com.xirgonium.android.manager;

import android.content.Context;

public class VelibStationManager extends ConstructorJCDecaultVelibLikeStationManager {

  // public final static String WS_VELIB_ALL_STATIONS_URL = "http://xirgonium.free.fr/testxml/carto.xml";
  private final static String WS_ALL_STATIONS_URL   = "http://www.velib.paris.fr/service/carto";

  // ---- Add the station ID at the end of the URL to get data from Velib'
  private final static String WS_STATION_DETAIL_URL = "http://www.velib.paris.fr/service/stationdetails/";

  public VelibStationManager() {
  }

  public VelibStationManager(Context launched) {
    super(launched);
  }

  public String[] getCities() {
    return new String[] { "Paris", "Arcueil", "Aubervillier", "Bagnolet", "Boulogne-Billancourt", "Charenton-le-Pont", "Clichy-sur-Seine", "Fontenay-sous-Bois", "Gentilly", "Issy-les-Moulineaux",
        "Ivry-sur-Seine", "Joinville-le-Pont", "Le Kremli-Bicêtre", "Le Pré-Saint-Gervais", "Les Lilas", "Levallois-Perret", "Malakoff", "Montreuil", "Montrouge", "Neuilly-sur-Seine",
        "Nogent-sur-Marne", "Pantin", "Puteaux", "Saint-Cloud", "Saint-Denis", "Saint-Mandé", "Saint-Maurice", "Saint-Ouen", "Suresnes", "Vanves", "Vincennes" };
  }

  @Override
  protected String getAllStationURL() {
    return WS_ALL_STATIONS_URL;
  }

  @Override
  protected String getOneStationInfoURL() {
    // TODO Auto-generated method stub
    return WS_STATION_DETAIL_URL;
  }
  
  @Override
  boolean useOpenTag() {
    return true;
  }
}
