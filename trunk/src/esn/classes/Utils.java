package esn.classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

public class Utils {
	private static final String EMAIL_PATTERN = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+){1,3}$";
	private static final String JSON_DATE_TYPE_PATTERN = "^\\/Date\\((\\d+)\\)\\/$";

	public static Bitmap getBitmapFromURL(String src) throws IOException {

		Log.e("src", src);

		URL url = new URL(src);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setDoInput(true);

		connection.connect();

		InputStream input = connection.getInputStream();

		Bitmap myBitmap = BitmapFactory.decodeStream(input);

		Log.d("Bitmap", "returned");

		return myBitmap;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			while (true) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static boolean IsEmail(String src) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(src);

		return matcher.matches();
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void JsonToObject(JSONObject json, Object obj)
			throws IllegalArgumentException, IllegalAccessException,
			JSONException {
		Class<?> type = obj.getClass();
		Field[] fields = type.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();

			if (!json.isNull(fieldName)) {
				Object value = json.get(fieldName);
				if (value.getClass() == String.class
						&& value.toString().matches(JSON_DATE_TYPE_PATTERN)) {
					value = GetDateFromJSONString(value.toString());
				}
				field.set(obj, value);
			}
		}

	}

	public static Date GetDateFromJSONString(String jsonDate) {
		Pattern dateTypePattern = Pattern.compile("\\d+");
		Matcher matches = dateTypePattern.matcher(jsonDate);
		while (matches.find()) {
			String timeStampStr = matches.group();

			if (timeStampStr != null && timeStampStr.length() > 0) {
				long timeStamp = Long.parseLong(timeStampStr);
				Date date = new Date(timeStamp);
				return date;
			}
		}

		return null;
	}

	public static String DateToStringByLocale(Date date, int i) {
		String rs = null;

		switch (i) {
		case 1:
			rs = SimpleDateFormat.getDateInstance().format(date);
			break;

		case 2:
			SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
			StringBuilder nowYYYYMMDD = new StringBuilder(
					dateformat.format(date));
			rs = nowYYYYMMDD.toString();
			break;

		default:
			break;
		}

		return rs;
	}

	private static double toRad(double num) {
		return num * Math.PI / 180;
	}

	public static double distanceOfTwoPoint(GeoPoint point1, GeoPoint point2) {
		double lat1 = point1.getLatitudeE6() / 1E6;
		double lat2 = point2.getLatitudeE6() / 1E6;
		double lon1 = point1.getLongitudeE6() / 1E6;
		double lon2 = point2.getLongitudeE6() / 1E6;
		final int r = 6371;// km -- ban kinh trai dat

		double dLat = toRad(Math.abs(lat1 - lat1));
		double dLon = toRad(Math.abs(lon2 - lon1));
		lat1 = toRad(lat1);
		lat2 = toRad(lat2);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return r * c;
	}

	public static boolean isNetworkAvailable(Activity act) {
		ConnectivityManager connectivityManager = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public static String bitmapToBase64(Bitmap img) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] byteArr = bos.toByteArray();
		return android.util.Base64.encodeToString(byteArr, Base64.DEFAULT);
	}

}
