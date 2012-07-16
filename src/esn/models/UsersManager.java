package esn.models;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import esn.classes.HttpHelper;
import esn.classes.Utils;

public class UsersManager {
	// String NAMESPACE = "http://esnservice.somee.com/";
	// String URL = "http://esnservice.somee.com/accountservice.asmx";
	String NAMESPACE = "http://esn.com.vn/";
	String URL = "http://bangnl.info/ws/AccountsWS.asmx";
	HttpHelper helper = new HttpHelper(URL);

	public UsersManager() {

	}

	public int Login(String email, String password) throws JSONException,
			IOException {

		JSONObject params = new JSONObject();

		params.put("email", email);
		params.put("password", password);

		JSONObject jsonObject = helper.invokeWebMethod("Login", params);

		int rs = jsonObject.getInt("d");

		return rs;
	}

	public int Register(Users user) throws JSONException, IOException {
		int rs = 0;

		JSONObject params = new JSONObject();

		params.put("name", user.Name);
		params.put("email", user.Email);
		params.put("password", user.Password);
		params.put("birthday", user.Birthday);
		params.put("phone", user.Phone);
		params.put("gender", user.Gender);
		params.put("accessToken", user.AccessToken);

		JSONObject jsonObject = helper.invokeWebMethod("Register", params);
		rs = jsonObject.getInt("d");
		return rs;
	}

	public boolean CheckEmailExists(String email) throws JSONException,
			IOException {

		JSONObject params = new JSONObject();

		params.put("email", email);

		JSONObject jsonObject = helper.invokeWebMethod("CheckEmailExisted",
				params);

		boolean rs = jsonObject.getBoolean("d");

		return rs;
	}

	public Boolean ChangePassword(String email, String currentPass,
			String newPass) throws JSONException, IOException {

		JSONObject params = new JSONObject();

		params.put("email", email);
		params.put("oldPassword", currentPass);
		params.put("newPassword", newPass);

		JSONObject jsonObject = helper
				.invokeWebMethod("ChangePassword", params);

		boolean rs = jsonObject.getBoolean("d");

		return rs;

	}

	public Boolean UpdateProfile(Users user) throws JSONException, IOException {
		Boolean rs = false;

		JSONObject params = new JSONObject();

		params.put("accID", user.AccID);
		params.put("name", user.Name);
		params.put("gender", user.Gender);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		params.put("birthday", format.format(user.Birthday));
		params.put("phone", user.Phone);
		params.put("address", user.Address);
		params.put("street", user.Street);
		params.put("district", user.District);
		params.put("city", user.City);
		params.put("country", user.Country);
		params.put("favorite", user.Favorite);

		params.put("avatar", user.Avatar);

		JSONObject jsonObject = helper.invokeWebMethod("UpdateProfile", params);

		rs = jsonObject.getBoolean("d");

		return rs;
	}

	public Users RetrieveByEmail(String email) throws JSONException,
			IOException, IllegalArgumentException, IllegalAccessException {
		HttpHelper helper = new HttpHelper(URL);
		JSONObject params = new JSONObject();

		params.put("email", email);

		JSONObject response = helper.invokeWebMethod("RetrieveByEmail", params);

		if (response != null) {

			Users user = new Users();
			JSONObject jsonUser = response.getJSONObject("d");

			JSONObject p = jsonUser.getJSONObject("Profile");

			user.AccID = jsonUser.getInt("ID");
			user.AccID = jsonUser.getInt("AccID");
			user.Password = jsonUser.getString("Password");

			user.Name = p.getString("Name");

			user.Birthday = Utils.GetDateFromJSONString(p.getString("Birthday"));

			user.Gender = p.getBoolean("Gender");

			user.Phone = p.getString("Phone");

			user.Address = p.getString("Address");

			user.Street = p.getString("Street");

			user.District = p.getString("District");

			user.City = p.getString("City");

			user.Country = p.getString("Country");

			user.Favorite = p.getString("Favorite");

			user.Avatar = p.getString("Avatar");
			return user;
		}
		return null;
	}

	public Users RetrieveById(int id) throws JSONException, IOException,
			IllegalArgumentException, IllegalAccessException {
		HttpHelper helper = new HttpHelper(URL);
		JSONObject params = new JSONObject();

		params.put("id", id);

		JSONObject response = helper.invokeWebMethod("RetrieveJSON", params);

		if (response != null) {

			Users user = new Users();
			JSONObject jsonUser = response.getJSONObject("d");

			Utils.JsonToObject(jsonUser, user);
			
			return user;
		}
		return null;
	}

	public Users RetrieveByAccessToken(String accessToken)
			throws JSONException, IOException {
		if (accessToken != null && !accessToken.equals("")) {
			HttpHelper helper = new HttpHelper(URL);
			JSONObject params = new JSONObject();

			params.put("accesstoken", accessToken);

			JSONObject response = helper.invokeWebMethod(
					"GetAccountsByAccessToken", params);

			if (response != null && response.has("d") && !response.isNull("d")) {
				Users user = new Users();
				JSONObject jsonUser = response.getJSONObject("d");

				JSONObject p = jsonUser.getJSONObject("Profile");

				user.AccID = jsonUser.getInt("ID");
				user.AccID = jsonUser.getInt("AccID");
				user.Password = jsonUser.getString("Password");

				user.Name = p.getString("Name");

				user.Birthday = Utils.GetDateFromJSONString(p.getString("Birthday"));

				user.Gender = p.getBoolean("Gender");

				user.Phone = p.getString("Phone");

				user.Address = p.getString("Address");

				user.Street = p.getString("Street");

				user.District = p.getString("District");

				user.City = p.getString("City");

				user.Country = p.getString("Country");

				user.Favorite = p.getString("Favorite");

				user.Avatar = p.getString("Avatar");
				return user;
			}
		}
		return null;
	}
	/*
	 * public Boolean CheckAccessTokenIsExisted(String accessToken){
	 * 
	 * }
	 */
}
