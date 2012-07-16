package esn.classes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.Util;
import com.google.android.maps.GeoPoint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class Utils {
	private static final String EMAIL_PATTERN = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+){1,3}$";
	private static final String JSON_DATE_TYPE_PATTERN = "^\\/Date\\((\\d+)\\)\\/$";
	private static int MAX_IMAGE_DIMENSION = 720;

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
			ex.printStackTrace();
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
					Object dateValue = GetDateFromJSONString(value.toString());
					field.set(obj, dateValue);
				} else {
					field.set(obj, value);
				}
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

	@SuppressLint("NewApi")
	public static String bitmapToBase64(Bitmap img) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			img.compress(CompressFormat.PNG, 100, bos);
			byte[] byteArr = bos.toByteArray();
			img.recycle();
			bos.close();
			bos = null;
			return Base64.encodeToString(byteArr, Base64.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

	}

	@SuppressLint("NewApi")
	public static String bitmapToBase64(Context context, Uri uri) {
		try {

			byte[] byteArr = Utils.scaleImage(context, uri);
			return Base64.encodeToString(byteArr, Base64.DEFAULT);
		} catch (IOException e) {

			e.printStackTrace();
			return "";
		}

	}

	public static Bitmap base64ToBitmap(String base64Img) {
		byte[] byteArr = Base64.decode(base64Img, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
	}

	public static byte[] scaleImage(Context context, Uri photoUri)
			throws IOException {
		InputStream is = context.getContentResolver().openInputStream(photoUri);

		BitmapFactory.Options bmo = new BitmapFactory.Options();
		bmo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, bmo);
		is.close();
		is = context.getContentResolver().openInputStream(photoUri);
		String type = bmo.outMimeType;
		Bitmap uploadImage;

		if (bmo.outHeight > MAX_IMAGE_DIMENSION
				|| bmo.outWidth > MAX_IMAGE_DIMENSION) {
			float widthRatio = ((float) bmo.outHeight)
					/ ((float) MAX_IMAGE_DIMENSION);
			float heightRatio = ((float) bmo.outWidth)
					/ ((float) MAX_IMAGE_DIMENSION);
			float maxRatio = Math.max(widthRatio, heightRatio);
			bmo.inSampleSize = (int) maxRatio;
			bmo.inJustDecodeBounds = false;
			uploadImage = BitmapFactory.decodeStream(is, null, bmo);
		} else {
			uploadImage = BitmapFactory.decodeStream(is);
		}
		is.close();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (type.equals("image/png")) {
			uploadImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
		} else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
			uploadImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return bMapArray;
	}

	public static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public String sendFileToServer(String filename, String targetUrl) {
		String response = "error";
		Log.e("Image filename", filename);
		Log.e("url", targetUrl);
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		// DataInputStream inputStream = null;

		String pathToOurFile = filename;
		String urlServer = targetUrl;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024;
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setChunkedStreamingMode(1024);
			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);

			String connstr = null;
			connstr = "Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ pathToOurFile + "\"" + lineEnd;
			Log.i("Connstr", connstr);

			outputStream.writeBytes(connstr);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			Log.e("Image length", bytesAvailable + "");
			try {
				while (bytesRead > 0) {
					try {
						outputStream.write(buffer, 0, bufferSize);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						response = "outofmemoryerror";
						return response;
					}
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = "error";
				return response;
			}
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			Log.i("Server Response Code ", "" + serverResponseCode);
			Log.i("Server Response Message", serverResponseMessage);

			if (serverResponseCode == 200) {
				response = "true";
			}

			String CDate = null;
			Date serverTime = new Date(connection.getDate());
			try {
				CDate = df.format(serverTime);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Date Exception", e.getMessage() + " Parse Exception");
			}
			Log.i("Server Response Time", CDate + "");

			filename = CDate
					+ filename.substring(filename.lastIndexOf("."),
							filename.length());
			Log.i("File Name in Server : ", filename);

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			outputStream = null;
		} catch (Exception ex) {
			// Exception handling
			response = "error";
			Log.e("Send file Exception", ex.getMessage() + "");
			ex.printStackTrace();
		}
		return response;
	}

}
