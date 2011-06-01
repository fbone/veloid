package com.xirgonium.android.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xirgonium.android.veloid.R;

public class NextBikeGermanyStationManager extends ConstructorNextBikeStationManager {

  final String COUNTRY_NAME = "Germany";
  final String NETWORK_NAME = "Next Bike [DE]";

  public NextBikeGermanyStationManager() {
    setOwnSpecificAction(true);
  }

  public NextBikeGermanyStationManager(Context launched) {
    super(launched);
    setOwnSpecificAction(true);
  }

  @Override
  protected String getCountry() {
    return COUNTRY_NAME;
  }

  @Override
  public String[] getCities() {
    return new String[] { "Leipzig", "Dresden", "Erlangen", "Nürnberg", "Wiesbaden", "Frankfurt", "Halle", "Friedrichshafen", "Offenbach am Main", "Magdeburg", "Koblenz", "Düsseldorf" };
  }

  /**
   * For the special action
   */
  @Override
  public void specialAction(Context ctx) {
    try {
      Uri uri = Uri.parse(ctx.getString(R.string.specific_action_nextbike_url_de));
      ctx.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  @Override
  public String getSpecialActionText(Context ctx) {
    return ctx.getString(R.string.specific_action_nextbike);
  }

}
