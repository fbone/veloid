package com.xirgonium.android.veloid.test;

import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xirgonium.android.manager.BicincittaParmaStationManager;
import com.xirgonium.android.manager.BicincittaSaviglianoStationManager;
import com.xirgonium.android.manager.BicingBarcelonaStationManager;
import com.xirgonium.android.manager.BiclooNantesStationManager;
import com.xirgonium.android.manager.BikeMiMilanoStationManager;
import com.xirgonium.android.manager.BipPerpignanStationManager;
import com.xirgonium.android.manager.BixiMontrealStationManager;
import com.xirgonium.android.manager.BiziZaragozaStationManager;
import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.manager.CyclicRouen;
import com.xirgonium.android.manager.DublinBikeStationManager;
import com.xirgonium.android.manager.EcoBiciMexico;
import com.xirgonium.android.manager.LausanneRoule;
import com.xirgonium.android.manager.LeVeloMarseilleStationManager;
import com.xirgonium.android.manager.LibelloValence;
import com.xirgonium.android.manager.MetroVeloGrenoble;
import com.xirgonium.android.manager.NextBikeAustriaStationManager;
import com.xirgonium.android.manager.NextBikeGermanyStationManager;
import com.xirgonium.android.manager.NextBikeNewZealandStationManager;
import com.xirgonium.android.manager.OyBikeCardiffStationManager;
import com.xirgonium.android.manager.OyBikeFarnboroughStationManager;
import com.xirgonium.android.manager.OyBikeLondonStationManager;
import com.xirgonium.android.manager.OyBikeReadingStationManager;
import com.xirgonium.android.manager.RomaNBikeStationManager;
import com.xirgonium.android.manager.SambaRioDeJaneiroStationManager;
import com.xirgonium.android.manager.SeviciSevillaStationManager;
import com.xirgonium.android.manager.SmartBikeDCWashingtonStationManager;
import com.xirgonium.android.manager.VCubBordeaux;
import com.xirgonium.android.manager.Velam;
import com.xirgonium.android.manager.VelcomPlaineCommune;
import com.xirgonium.android.manager.VelibStationManager;
import com.xirgonium.android.manager.Velo2CergyStationManager;
import com.xirgonium.android.manager.VeloBleuNice2;
import com.xirgonium.android.manager.VeloPlusOrleansStationManager;
import com.xirgonium.android.manager.VeloStanNancyStationManager;
import com.xirgonium.android.manager.VeloStarRennes;
import com.xirgonium.android.manager.VeloToulouseStationManager;
import com.xirgonium.android.manager.VeloVertStEtienne;
import com.xirgonium.android.manager.VeloceaVannes;
import com.xirgonium.android.manager.VelociteBesanconStationManager;
import com.xirgonium.android.manager.VelociteMulhouse;
import com.xirgonium.android.manager.VelodiDijonStationManager;
import com.xirgonium.android.manager.VelomaggMontpellier;
import com.xirgonium.android.manager.VelopopAvignon;
import com.xirgonium.android.manager.VelovLyonStationManager;
import com.xirgonium.android.manager.VeolCaenStationManager;
import com.xirgonium.android.manager.VilloBruxxelStationManager;
import com.xirgonium.android.manager.YelloLaRochelle;
import com.xirgonium.android.object.Station;
import com.xirgonium.android.veloid.R;
import com.xirgonium.exception.NoInternetConnection;

public class NetworkTest extends Activity {

	LinearLayout	container	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unittest);
		container = (LinearLayout) findViewById(R.id.unitTestLister);

		// testBixi();
		//testVeloBleuNice();
		// testVeol();
		// testVelodi();
		//testBicing();
		// testVelov();
		//		testOybike();

		// testBikeMi();

		// testVeloPlus();

		// testParma();

		// testSamba();
		// testSavigliano();
		// testRoman();

		// testSmartbike();
		// testBiziZaragoza();
		// testNBAustria();
		// testNBGermany();
		// testNBNz();

		// JCDECAULT
		/*
		 * testVelostan(); testBicloo();
		 */
		testSevici();

		/*
		 * testVeloToulouse(); testVelo2(); testBip(); testVelib(); testlevelo(); testVelocite(); testVeloMagg(); testVillo(); testVelopop(); testLausanneRoule(); testCyclicRouen();
		 */
		//testVeloStarRennes();
		/*
		 * testVelamAmiens(); testVelociteMulhouse();
		 * 
		 * testVelcomPlaineCommune();
		 */
		//testEcoBiciMexico();
		//testVCub();

		//		testOyBikeCardiff();
		//		testOyBikeFarnoborought();
		//		testOyBikeReading();
		testDublinBike();

		//testYelloLaRochelle();
		//testVeloceaVannes();
		testLibelloValence();
		testVelovertStEtienne();
		testMetroVeloGrenoble();

	}

	public void testLibelloValence() {
		LibelloValence manager = new LibelloValence(this);
		manager.setNetworkId("libelloval");
		forThisNetworkManager(manager);
	}

	public void testVelovertStEtienne() {
		VeloVertStEtienne manager = new VeloVertStEtienne(this);
		manager.setNetworkId("velovertstet");
		forThisNetworkManager(manager);
	}

	public void testMetroVeloGrenoble() {
		MetroVeloGrenoble manager = new MetroVeloGrenoble(this);
		manager.setNetworkId("metrovelogren");
		forThisNetworkManager(manager);
	}

	public void testVeloceaVannes() {
		VeloceaVannes manager = new VeloceaVannes(this);
		manager.setNetworkId("veloceavannes");
		forThisNetworkManager(manager);
	}

	public void testYelloLaRochelle() {
		YelloLaRochelle manager = new YelloLaRochelle(this);
		manager.setNetworkId("yellolarochelle");
		forThisNetworkManager(manager);
	}

	public void testOyBikeReading() {
		OyBikeReadingStationManager manager = new OyBikeReadingStationManager(this);
		manager.setNetworkId("oybikereading");
		forThisNetworkManager(manager);
	}

	public void testOyBikeFarnoborought() {
		OyBikeFarnboroughStationManager manager = new OyBikeFarnboroughStationManager(this);
		manager.setNetworkId("oybikefarno");
		forThisNetworkManager(manager);
	}

	public void testOyBikeCardiff() {
		OyBikeCardiffStationManager manager = new OyBikeCardiffStationManager(this);
		manager.setNetworkId("oybikecardiff");
		forThisNetworkManager(manager);
	}

	public void testEcoBiciMexico() {
		EcoBiciMexico manager = new EcoBiciMexico(this);
		manager.setNetworkId("ecobicimexico");
		forThisNetworkManager(manager);
	}

	public void testVCub() {
		VCubBordeaux manager = new VCubBordeaux(this);
		manager.setNetworkId("vcube");
		forThisNetworkManager(manager);
	}

	public void testDublinBike() {
		DublinBikeStationManager manager = new DublinBikeStationManager(this);
		manager.setNetworkId("dublinbike");
		forThisNetworkManager(manager);
	}

	public void testVelcomPlaineCommune() {
		VelcomPlaineCommune manager = new VelcomPlaineCommune(this);
		manager.setNetworkId("velcomplainecommune");
		forThisNetworkManager(manager);
	}

	public void testVelociteMulhouse() {
		VelociteMulhouse manager = new VelociteMulhouse(this);
		manager.setNetworkId("velocite_mul_test");
		forThisNetworkManager(manager);
	}

	public void testVelamAmiens() {
		Velam manager = new Velam(this);
		manager.setNetworkId("velam_test");
		forThisNetworkManager(manager);
	}

	public void testVeloStarRennes() {
		VeloStarRennes manager = new VeloStarRennes(this);
		manager.setNetworkId("velostar");
		forThisNetworkManager(manager);
	}

	public void testVeloBleuNice() {
		VeloBleuNice2 manager = new VeloBleuNice2(this);
		manager.setNetworkId("velobleunice");
		forThisNetworkManager(manager);
	}

	public void testCyclicRouen() {
		CyclicRouen manager = new CyclicRouen(this);
		manager.setNetworkId("cyclic");
		forThisNetworkManager(manager);
	}

	public void testLausanneRoule() {
		LausanneRoule manager = new LausanneRoule(this);
		manager.setNetworkId("lausanneRoule");
		forThisNetworkManager(manager);
	}

	public void testVillo() {
		VilloBruxxelStationManager manager = new VilloBruxxelStationManager(this);
		manager.setNetworkId("villo");
		forThisNetworkManager(manager);
	}

	public void testVeloMagg() {
		VelomaggMontpellier manager = new VelomaggMontpellier(this);
		manager.setNetworkId("velomagg");
		forThisNetworkManager(manager);
	}

	public void testBixi() {
		BixiMontrealStationManager manager = new BixiMontrealStationManager(this);
		manager.setNetworkId("bixi");
		forThisNetworkManager(manager);
	}

	public void testVelo2() {
		Velo2CergyStationManager manager = new Velo2CergyStationManager(this);
		manager.setNetworkId("velo2cergy");
		forThisNetworkManager(manager);
	}

	public void testVelocite() {
		VelociteBesanconStationManager manager = new VelociteBesanconStationManager(this);
		manager.setNetworkId("velocite");
		forThisNetworkManager(manager);
	}

	public void testVeol() {
		VeolCaenStationManager manager = new VeolCaenStationManager(this);
		manager.setNetworkId("veol");
		forThisNetworkManager(manager);
	}

	public void testVelodi() {
		VelodiDijonStationManager manager = new VelodiDijonStationManager(this);
		manager.setNetworkId("velodi");
		forThisNetworkManager(manager);
	}

	// <manager location="Barcelona" name="Bicing" id="bicingbarcelona" class="com.xirgonium.android.manager.BicingBarcelonaStationManager" />
	public void testBicing() {
		BicingBarcelonaStationManager manager = new BicingBarcelonaStationManager(this);
		manager.setNetworkId("bicing");
		forThisNetworkManager(manager);
	}

	// <manager location="Lyon" name="VŽlov" id="velovlyon" class="com.xirgonium.android.manager.VelovLyonStationManager" />
	public void testVelov() {
		VelovLyonStationManager manager = new VelovLyonStationManager(this);
		manager.setNetworkId("velov");
		forThisNetworkManager(manager);
	}

	// <manager location="London" name="Oybike" id="oybikelondon" class="com.xirgonium.android.manager.OyBikeLondonStationManager" />
	public void testOybike() {
		OyBikeLondonStationManager manager = new OyBikeLondonStationManager(this);
		manager.setNetworkId("oybike");
		forThisNetworkManager(manager);
	}

	// <manager location="Marseille" name="LevŽlo" id="levelomarseille" class="com.xirgonium.android.manager.LeVeloMarseilleStationManager" />
	public void testlevelo() {
		LeVeloMarseilleStationManager manager = new LeVeloMarseilleStationManager(this);
		manager.setNetworkId("levelo");
		forThisNetworkManager(manager);
	}

	// <manager location="Milano" name="BikeMi" id="bikemimilano" class="com.xirgonium.android.manager.BikeMiMilanoStationManager" />
	public void testBikeMi() {
		BikeMiMilanoStationManager manager = new BikeMiMilanoStationManager(this);
		manager.setNetworkId("bikemi");
		forThisNetworkManager(manager);
	}

	// <manager location="Nancy" name="VŽlostan" id="velostannancy" class="com.xirgonium.android.manager.VeloStanNancyStationManager" />
	public void testVelostan() {
		VeloStanNancyStationManager manager = new VeloStanNancyStationManager(this);
		manager.setNetworkId("velostan");
		forThisNetworkManager(manager);
	}

	// <manager location="Nantes" name="Bicloo" id="bicloonantes" class="com.xirgonium.android.manager.BiclooNantesStationManager" />
	public void testBicloo() {
		BiclooNantesStationManager manager = new BiclooNantesStationManager(this);
		manager.setNetworkId("bicloo");
		forThisNetworkManager(manager);
	}

	// <manager location="OrlŽans" name="VŽlo+" id="veloplusorleans" class="com.xirgonium.android.manager.VeloPlusOrleansStationManager" />
	public void testVeloPlus() {
		VeloPlusOrleansStationManager manager = new VeloPlusOrleansStationManager(this);
		manager.setNetworkId("veloplus");
		forThisNetworkManager(manager);
	}

	// <manager location="Paris" name="VŽlib" id="velib" class="com.xirgonium.android.manager.VelibStationManager" />
	public void testVelib() {
		VelibStationManager manager = new VelibStationManager(this);
		manager.setNetworkId("velib");
		forThisNetworkManager(manager);
	}

	// <manager location="Parma" name="Bicincittˆ - Parma" id="bicincittaparma" class="com.xirgonium.android.manager.BicincittaParmaStationManager" />
	public void testParma() {
		BicincittaParmaStationManager manager = new BicincittaParmaStationManager(this);
		manager.setNetworkId("parma");
		forThisNetworkManager(manager);
	}

	// <manager location="Perpignan" name="Bip!" id="bipperpignan" class="com.xirgonium.android.manager.BipPerpignanStationManager" />
	public void testBip() {
		BipPerpignanStationManager manager = new BipPerpignanStationManager(this);
		manager.setNetworkId("bip");
		forThisNetworkManager(manager);
	}

	// <manager location="Rio de Janeiro" name="Samba" id="sambario" class="com.xirgonium.android.manager.SambaRioDeJaneiroStationManager" />
	public void testSamba() {
		SambaRioDeJaneiroStationManager manager = new SambaRioDeJaneiroStationManager(this);
		manager.setNetworkId("samba");
		forThisNetworkManager(manager);
	}

	// <manager location="Savigliano" name="Bicincittˆ - Savigliano" id="bicincittasavigliano" class="com.xirgonium.android.manager.BicincittaSaviglianoStationManager" />
	public void testSavigliano() {
		BicincittaSaviglianoStationManager manager = new BicincittaSaviglianoStationManager(this);
		manager.setNetworkId("savigliano");
		forThisNetworkManager(manager);
	}

	// <manager location="Roma" name="Roma'n'bike" id="romanbikeroma" class="com.xirgonium.android.manager.RomaNBikeStationManager" />
	public void testRoman() {
		RomaNBikeStationManager manager = new RomaNBikeStationManager(this);
		manager.setNetworkId("roman");
		forThisNetworkManager(manager);
	}

	// <manager location="Sevilla" name="Sevici" id="sevicisevilla" class="com.xirgonium.android.manager.SeviciSevillaStationManager" />
	public void testSevici() {
		SeviciSevillaStationManager manager = new SeviciSevillaStationManager(this);
		manager.setNetworkId("sevici");
		forThisNetworkManager(manager);
	}

	// <manager location="Toulouse" name="VŽl™Toulouse" id="velotoulouse" class="com.xirgonium.android.manager.VeloToulouseStationManager" />
	public void testVeloToulouse() {
		VeloToulouseStationManager manager = new VeloToulouseStationManager(this);
		manager.setNetworkId("velot");
		forThisNetworkManager(manager);
	}

	// <manager location="Washington DC" name="SmartbikeDC" id="smartbikedcwashington" class="com.xirgonium.android.manager.SmartBikeDCWashingtonStationManager" />
	public void testSmartbike() {
		SmartBikeDCWashingtonStationManager manager = new SmartBikeDCWashingtonStationManager(this);
		manager.setNetworkId("smartbike");
		forThisNetworkManager(manager);
	}

	// <manager location="Zaragoza" name="Bizi" id="bizizaragoza" class="com.xirgonium.android.manager.BiziZaragozaStationManager" />
	public void testBiziZaragoza() {
		BiziZaragozaStationManager manager = new BiziZaragozaStationManager(this);
		manager.setNetworkId("bizi");
		forThisNetworkManager(manager);
	}

	//    
	// <manager location="Austria" name="NextBike" id="nextbikeaustria" class="com.xirgonium.android.manager.NextBikeAustriaStationManager" />
	public void testNBAustria() {
		NextBikeAustriaStationManager manager = new NextBikeAustriaStationManager(this);
		manager.setNetworkId("nbaus");
		forThisNetworkManager(manager);
	}

	// <manager location="Germany" name="NextBike" id="nextbikegermany" class="com.xirgonium.android.manager.NextBikeGermanyStationManager" />
	public void testNBGermany() {
		NextBikeGermanyStationManager manager = new NextBikeGermanyStationManager(this);
		manager.setNetworkId("nbGer");
		forThisNetworkManager(manager);
	}

	// <manager location="New Zealand" name="NextBike" id="nextbikenewzealand" class="com.xirgonium.android.manager.NextBikeNewZealandStationManager" />
	public void testNBNz() {
		NextBikeNewZealandStationManager manager = new NextBikeNewZealandStationManager(this);
		manager.setNetworkId("nbnz");
		forThisNetworkManager(manager);
	}

	public void testVelopop() {
		VelopopAvignon manager = new VelopopAvignon(this);
		manager.setNetworkId("velopop");
		forThisNetworkManager(manager);
	}

	private void forThisNetworkManager(CommonStationManager manager) {
		try {

			logMessage("***********  " + manager.getNetworkId() + " ************");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Date before = new Date();
			manager.updateStationListDynamicaly();
			Date after = new Date();
			long sec = (after.getTime() - before.getTime()) / 1000;
			logMessage("DURATION THE STATION GATHERING : " + sec + " sec");

			Vector<Station> stations = manager.restoreAllStationWithminimumInfoFromDataBase();
			int total = stations.size();
			assertTrue("NO STATIONS RESTORED FROM NETWORK " + manager.getNetworkId(), total > 0);
			logMessage("NUMBER FOUND STATIONS  " + total);

			int error = 0;
			for (Station station : stations) {

				station = manager.fillDynamicInformationForAStation(station);

				int bikes = station.getAvailableBikes();
				int slots = station.getFreeSlot();

				assertTrue("NO INFO FOR THE STATION " + station.getId() + " b:" + bikes + " s:" + slots, !(bikes <= 0 & slots <= 0));
				if ((bikes <= 0 & slots <= 0))
					error++;

			}

			if (error > 0) {
				logMessage("ERROR : " + error + "/" + total);
			} else {
				logMessage("NO ERROR");
			}

			manager.clearListOfStationFromDatabase();

			assertTrue("BADLY DELETED ", manager.getNbStationsInDB() == 0);

			logMessage("*********** END ************");

		} catch (NoInternetConnection e) {
			assertTrue("NO INTERNET CONNEXION", false);
		}
	}

	private void logMessage(String msg) {
		Log.d("TEST", msg);
		TextView tv = new TextView(this);
		tv.setText(msg);
		container.addView(tv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

	}

	private void assertTrue(String msg, boolean checked) {
		if (!checked) {
			TextView tv = new TextView(this);
			Log.e("TEST", msg);
			tv.setText(msg);
			tv.setTextColor(Color.RED);
			container.addView(tv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}
}
