package com.xirgonium.android.manager;

import android.content.Context;

public class BicincittaParmaStationManager extends ConstructorBincinettaV2 {

    public BicincittaParmaStationManager() {}  
    
  public BicincittaParmaStationManager(Context launched) {
    super(launched);
    // TODO Auto-generated constructor stub
  }

  private String url     = "http://www.bicincitta.com/citta_v3.asp?id=2&pag=2";
  private String country = "Italia";

  @Override
  protected String getCountry() {
    return country;
  }

  @Override
  protected String getUrlOfInformationPage() {
    return url;
  }

  @Override
  public String[] getCities() {
    return new String[] { "Parma" };
  }

}
