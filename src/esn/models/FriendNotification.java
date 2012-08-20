package esn.models;

import esn.classes.Sessions;

public class FriendNotification {

	private Sessions sessions;

	public FriendNotification(Sessions sessions) {
				
		this.sessions = sessions;
		
	}

	public Boolean getEmailComment() {
		return sessions.get("esn.setting.notification.emailComment", false);
	}

	public void setEmailComment(Boolean emailComment) {
		sessions.put("esn.setting.notification.emailComment", emailComment);
	}

	public Boolean getEmailCreateEvent() {
		return sessions.get("esn.setting.notification.emailCreateEvent", false);
	}

	public void setEmailCreateEvent(Boolean emailCreateEvent) {
		sessions.put("esn.setting.notification.emailCreateEvent", emailCreateEvent);
	}

	public Boolean getEmailFriendFbJoin() {
		return sessions.get("esn.setting.notification.emailFriendFbJoin", false);
	}

	public void setEmailFriendFbJoin(Boolean emailFriendFbJoin) {
		sessions.put("esn.setting.notification.emailFriendFbJoin", emailFriendFbJoin);
	}

	public Boolean getEmailFriendRequest() {
		return sessions.get("esn.setting.notification.emailFriendRequest", false);
	}

	public void setEmailFriendRequest(Boolean emailFriendRequest) {
		sessions.put("esn.setting.notification.emailFriendRequest", emailFriendRequest);
	}

	public Boolean getEmailSharePlace() {
		return sessions.get("esn.setting.notification.emailSharePlace", false);
	}

	public void setEmailSharePlace(Boolean emailSharePlace) {
		sessions.put("esn.setting.notification.emailSharePlace", emailSharePlace);
	}

	public Boolean getEmailConfirmEvent() {
		return sessions.get("esn.setting.notification.emailConfirmEvent", false);
	}

	public void setEmailConfirmEvent(Boolean emailConfirmEvent) {
		sessions.put("esn.setting.notification.emailConfirmEvent", emailConfirmEvent);
	}

	public Boolean getPhoneComment() {
		return sessions.get("esn.setting.notification.phoneComment", false);
	}

	public void setPhoneComment(Boolean phoneComment) {
		sessions.put("esn.setting.notification.phoneComment", phoneComment);
	}

	public Boolean getPhoneCreateEvent() {
		return sessions.get("esn.setting.notification.phoneCreateEvent", false);
	}

	public void setPhoneCreateEvent(Boolean phoneCreateEvent) {
		sessions.put("esn.setting.notification.phoneCreateEvent", phoneCreateEvent);
	}

	public Boolean getPhoneFriendFbJoin() {
		return sessions.get("esn.setting.notification.emailCreateEvent", false);
	}

	public void setPhoneFriendFbJoin(Boolean phoneFriendFbJoin) {
		sessions.put("esn.setting.notification.phoneFriendFbJoin", phoneFriendFbJoin);
	}

	public Boolean getPhoneFriendRequest() {
		return sessions.get("esn.setting.notification.phoneFriendRequest", false);
	}

	public void setPhoneFriendRequest(Boolean phoneFriendRequest) {
		sessions.put("esn.setting.notification.phoneFriendRequest", phoneFriendRequest);
	}

	public Boolean getPhoneSharePlace() {
		return sessions.get("esn.setting.notification.phoneSharePlace", false);
	}

	public void setPhoneSharePlace(Boolean phoneSharePlace) {
		sessions.put("esn.setting.notification.phoneSharePlace", phoneSharePlace);
	}

	public Boolean getPhoneConfirmEvent() {
		return sessions.get("esn.setting.notification.phoneConfirmEvent", false);
	}

	public void setPhoneConfirmEvent(Boolean phoneConfirmEvent) {
		sessions.put("esn.setting.notification.phoneConfirmEvent", phoneConfirmEvent);
	}

	public Sessions getSessions() {
		return sessions;
	}

	public void setSessions(Sessions sessions) {
		this.sessions = sessions;
	}	
}
