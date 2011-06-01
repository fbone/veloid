package com.xirgonium.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;

import com.xirgonium.android.veloid.R;

public class FormatUtility {
	public static String generateLastUpdateField(Date lu, Activity act) {

		if (lu == null) {
			return act.getString(R.string.main_last_update_not_done);
		}

		String pattern = act.getString(R.string.main_hour_date_format);

		SimpleDateFormat format = new SimpleDateFormat(pattern);

		StringBuffer buf = new StringBuffer(act.getString(R.string.main_hour_date_format_prefix));
		buf.append(" ");
		buf.append(format.format(lu));

		return buf.toString();
	}

	public static String getTwoDigitsFormatedNumber(int number) {
		DecimalFormat format = new DecimalFormat("00", new DecimalFormatSymbols(Locale.FRENCH));
		try {
			return format.format(number);
		} catch (NumberFormatException nfe) {
			return "";
		}
	}

	/**
	 * convert a duration in seconds in minute + seconds 135 sec = [2, 15]
	 * 
	 * @param duration
	 * @return a array of int where [0] = min and [1] = sec
	 */
	public static int[] convertSecondDurationInMinSec(int duration) {
		int[] toret = { 0, 0 };

		BigDecimal min = new BigDecimal(duration / 60);
		BigDecimal sec = new BigDecimal(duration % 60);

		toret[0] = min.setScale(0, BigDecimal.ROUND_DOWN).intValue();
		toret[1] = sec.setScale(0, BigDecimal.ROUND_DOWN).intValue();

		return toret;
	}

	public static String slurp(InputStream in, String charset) throws IOException {
		StringBuffer out = new StringBuffer();

		InputStreamReader reader = new InputStreamReader(in, charset);

		BufferedReader inReader = new BufferedReader(reader);
		String line;
		while ((line = inReader.readLine()) != null) {
			out.append(line);
		}

		return out.toString();
	}
	
//	public static StringBuffer slurp2(InputStream in, String charset) throws IOException {
//		StringBuffer out = new StringBuffer();
//
//		InputStreamReader reader = new InputStreamReader(in, charset);
//
//		BufferedReader inReader = new BufferedReader(reader);
//		String line;
//		while ((line = inReader.readLine()) != null) {
//			out.append(line);
//		}
//
//		return out;
//	}
	

	public static int intFromString(String s) {

		byte[] b = s.getBytes();
		int value = 0;
		for (int i = 0; i < s.length() - 1; i++) {
			int shift = (s.length() - 2 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}

		return Math.abs(value);
	}

	public static String formatAdressForList(Address addr) {
		StringBuffer buf = new StringBuffer(addr.getFeatureName());
		if (addr.getLocality() != null) {
			buf.append(",");
			buf.append(addr.getLocality());
		} else if (addr.getAdminArea() != null) {
			buf.append(",");
			buf.append(addr.getAdminArea());
		}

		return buf.toString();
	}

	public static String removeFileExtension(String fileName) {
		int beginningExtension = fileName.lastIndexOf(".");
		return fileName.substring(0, beginningExtension);
	}

	public static int getBackgroundColorForFavorite(int colorIndex, Context ctx) {
		switch (colorIndex) {
			case 1:
				return ctx.getResources().getColor(R.color.favorite_alternative_one);
			case 2:
				return ctx.getResources().getColor(R.color.favorite_alternative_two);
			case 3:
				return ctx.getResources().getColor(R.color.favorite_alternative_three);
			default:
				return ctx.getResources().getColor(R.color.green_one);
		}
	}
	
	public static int getTextColorForFavorite(int colorIndex, Context ctx) {
		switch (colorIndex) {
			case 1:
				return ctx.getResources().getColor(R.color.favorite_text_color_alternative1);
			case 2:
				return ctx.getResources().getColor(R.color.favorite_text_color_alternative2);
			case 3:
				return ctx.getResources().getColor(R.color.favorite_text_color_alternative3);
			default:
				return ctx.getResources().getColor(R.color.favorite_text_color);
		}
	}

}
