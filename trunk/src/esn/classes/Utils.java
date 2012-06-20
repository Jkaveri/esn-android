package esn.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
			
			if(!json.isNull(fieldName)){
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
		while(matches.find()){
			String timeStampStr = matches.group();
			
			if (timeStampStr != null && timeStampStr.length() > 0) {
				long timeStamp = Long.parseLong(timeStampStr);
				Date date = new Date(timeStamp);
				return date;
			}
		}
		
		return null;
	}
	public static String DateToStringByLocale(Date date){
		return SimpleDateFormat.getDateInstance().format(date);
	}
}
