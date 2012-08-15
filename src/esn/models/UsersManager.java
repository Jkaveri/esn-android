package esn.models;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

		params.put("name", user.Name);
		params.put("email", user.Email);
		params.put("password", user.Password);
		params.put("birthday", format.format(user.Birthday));
		params.put("phone", user.Phone);
		params.put("gender", user.Gender);
		params.put("access_token", user.AccessToken);
		params.put("fbID", user.fbID);
		params.put("avatar", "http://myesn.vn/images/interface/esnmainlogo.png");
		params.put("location", "");

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
			user.Email = jsonUser.getString("Email");
			user.AccessToken = jsonUser.getString("AccessToken");

			user.Name = p.getString("Name");

			user.Birthday = Utils
					.GetDateFromJSONString(p.getString("Birthday"));

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

		JSONObject response = helper.invokeWebMethod("Retrieve", params);

		if (response != null && response.has("d") && !response.isNull("d")) {
			Users user = new Users();
			JSONObject jsonUser = response.getJSONObject("d");
			user.AccID = jsonUser.getInt("AccID");
			user.Password = jsonUser.getString("Password");
			user.AccessToken = jsonUser.getString("AccessToken");
			user.VerificationCode = jsonUser.getString("VerificationCode");
			user.Email = jsonUser.getString("Email");
			user.Role = jsonUser.getString("RoleID");

			user.DateCreated = Utils.GetDateFromJSONString(jsonUser
					.getString("DateCreated"));
			user.fbID = jsonUser.getString("fbID");
			String status = jsonUser.getString("Status");
			user.Status = status.equals("Confirmed") ? AppEnums.AccountStatus.Confirmed
					: status.equals("Confirmed") ? AppEnums.AccountStatus.NotConfirmed
							: status.equals("Locked") ? AppEnums.AccountStatus.Locked
									: 3;
			// profile
			JSONObject p = jsonUser.getJSONObject("Profile");

			user.Name = p.getString("Name");

			user.Birthday = Utils
					.GetDateFromJSONString(p.getString("Birthday"));

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
				user.AccID = jsonUser.getInt("AccID");
				user.Password = jsonUser.getString("Password");
				user.AccessToken = jsonUser.getString("AccessToken");
				user.VerificationCode = jsonUser.getString("VerificationCode");
				user.Email = jsonUser.getString("Email");
				user.Role = jsonUser.getString("RoleID");

				user.DateCreated = Utils.GetDateFromJSONString(jsonUser
						.getString("DateCreated"));
				user.fbID = jsonUser.getString("fbID");
				String status = jsonUser.getString("Status");
				user.Status = status.equals("Confirmed") ? AppEnums.AccountStatus.Confirmed
						: status.equals("Confirmed") ? AppEnums.AccountStatus.NotConfirmed
								: status.equals("Locked") ? AppEnums.AccountStatus.Locked
										: 3;
				// profile
				JSONObject p = jsonUser.getJSONObject("Profile");

				user.Name = p.getString("Name");

				user.Birthday = Utils.GetDateFromJSONString(p
						.getString("Birthday"));

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

	public Users RetrieveByFbID(String fbId) throws ClientProtocolException,
			IOException, JSONException {
		if (fbId != null && !fbId.equals("")) {
			HttpHelper helper = new HttpHelper(URL);
			JSONObject params = new JSONObject();

			params.put("fbId", fbId);

			JSONObject response = helper.invokeWebMethod("GetAccountByFbID",
					params);

			if (response != null && response.has("d") && !response.isNull("d")) {
				Users user = new Users();
				JSONObject jsonUser = response.getJSONObject("d");
				user.AccID = jsonUser.getInt("AccID");
				user.Password = jsonUser.getString("Password");
				user.AccessToken = jsonUser.getString("AccessToken");
				user.VerificationCode = jsonUser.getString("VerificationCode");
				user.Email = jsonUser.getString("Email");
				user.Role = jsonUser.getString("RoleID");

				user.DateCreated = Utils.GetDateFromJSONString(jsonUser
						.getString("DateCreated"));
				user.fbID = jsonUser.getString("fbID");
				String status = jsonUser.getString("Status");
				user.Status = status.equals("Confirmed") ? AppEnums.AccountStatus.Confirmed
						: status.equals("Confirmed") ? AppEnums.AccountStatus.NotConfirmed
								: status.equals("Locked") ? AppEnums.AccountStatus.Locked
										: 3;
				// profile
				JSONObject p = jsonUser.getJSONObject("Profile");

				user.Name = p.getString("Name");

				user.Birthday = Utils.GetDateFromJSONString(p
						.getString("Birthday"));

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

	public Boolean UpdateAccessToken(int accID, String accessToken, String fbId)
			throws JSONException, IOException {

		JSONObject params = new JSONObject();

		params.put("accId", accID);

		params.put("accesstoken", accessToken);

		params.put("fbID", fbId);

		JSONObject response = helper.invokeWebMethod("UpdateAccessToken",
				params);

		if (response.has("d"))
			return response.getBoolean("d");
		else
			return false;
	}

	public int GetRelationStatus(int accId, int otherId)
			throws ClientProtocolException, IOException, JSONException {
		JSONObject params = new JSONObject();

		params.put("accID", accId);

		params.put("otherID", otherId);

		JSONObject response = helper.invokeWebMethod("GetRelationStatus",
				params);

		int rs = response.getInt("d");

		return rs;
	}

	public Users[] GetFbAccountHasRegistered(JSONArray friends, int accId)
			throws JSONException, IOException {
		JSONArray fbIds = new JSONArray();

		int count = friends.length();
		for (int i = 0; i < count; i++) {
			JSONObject obj = friends.getJSONObject(i);
			String id = obj.getString("id");
			fbIds.put(id);
		}

		HttpHelper helper = new HttpHelper(URL);
		JSONObject params = new JSONObject();

		params.put("fbIDs", fbIds);
		params.put("accountID", accId);
		JSONObject response = helper.invokeWebMethod(
				"GetFbAccountHasRegistered", params);

		if (response != null && response.has("d") && !response.isNull("d")) {
			Users user = new Users();
			JSONArray arrayUsers = response.getJSONArray("d");
			count = arrayUsers.length();
			Users[] users = new Users[count];
			for (int i = 0; i < count; i++) {
				JSONObject jsonUser = arrayUsers.getJSONObject(i);

				JSONObject p = jsonUser.getJSONObject("Profile");

				user.AccID = jsonUser.getInt("ID");
				user.AccID = jsonUser.getInt("AccID");
				user.Password = jsonUser.getString("Password");

				user.Name = p.getString("Name");

				user.Birthday = Utils.GetDateFromJSONString(p
						.getString("Birthday"));

				user.Gender = p.getBoolean("Gender");

				user.Phone = p.getString("Phone");

				user.Address = p.getString("Address");

				user.Street = p.getString("Street");

				user.District = p.getString("District");

				user.City = p.getString("City");

				user.Country = p.getString("Country");

				user.Favorite = p.getString("Favorite");

				user.Avatar = p.getString("Avatar");

				users[i] = user;
			}
			return users;

		}

		return null;
	}

	public boolean AddFriend(int accID, int friendID) throws JSONException,
			ClientProtocolException, IOException {
		JSONObject params = new JSONObject();
		params.put("accID", accID);
		params.put("friendID", friendID);
		HttpHelper helper = new HttpHelper(URL);
		JSONObject response = helper.invokeWebMethod("AddFriend", params);
		if (response != null && response.has("d") && !response.isNull("d")) {

			return response.getBoolean("d");
		} else {
			return false;
		}

	}

	public boolean UnFriend(int accID, int friendID) throws JSONException,
			ClientProtocolException, IOException {
		JSONObject params = new JSONObject();
		params.put("accID", accID);
		params.put("friendID", friendID);
		JSONObject result = helper.invokeWebMethod("Unfriend", params);

		return result.getBoolean("d");
	}

	public List<Users> SearchFriend(int accID, String name)
			throws ClientProtocolException, IOException, JSONException,
			IllegalArgumentException, IllegalAccessException {
		JSONObject params = new JSONObject();
		params.put("accId", accID);
		params.put("name", name);
		HttpHelper helper = new HttpHelper(URL);
		JSONObject response = helper.invokeWebMethod("SearchFriendByName",
				params);
		if (response != null && response.has("d") && !response.isNull("d")) {
			List<Users> users = new ArrayList<Users>();
			JSONArray arrayUsers = response.getJSONArray("d");
			int count = arrayUsers.length();
			for (int i = 0; i < count; i++) {
				JSONObject userJSON = arrayUsers.getJSONObject(i);
				Users user = new Users();
				Utils.JsonToObject(userJSON, user);
				users.add(user);
			}

			return users;

		} else {
			return null;
		}

	}

	public ArrayList<Users> getFriendsList(int pageNum, int pageSize, int accID)
			throws JSONException, IOException, IllegalArgumentException,
			IllegalAccessException, ParseException {

		ArrayList<Users> frds = new ArrayList<Users>();
		JSONObject params = new JSONObject();
		params.put("accountID", accID);
		params.put("pageNum", pageNum);
		params.put("pageSize", pageSize);
		JSONObject result = helper
				.invokeWebMethod("GetListFriendsJSON", params);
		if (result != null) {
			if (result.has("d")) {
				JSONArray jsonCall = result.getJSONArray("d");
				for (int i = 0; i < jsonCall.length(); i++) {
					JSONObject json = jsonCall.getJSONObject(i);
					Users frd = new Users();
					Utils.JsonToObject(json, frd);
					frds.add(frd);
				}
			}
		}
		return frds;
	}
}
