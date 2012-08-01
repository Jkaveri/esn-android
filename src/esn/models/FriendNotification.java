package esn.models;

public class FriendNotification {

	public Boolean emailComment;
	public Boolean emailCreateEvent;
	public Boolean emailFriendFbJoin;
	public Boolean emailFriendRequest;
	public Boolean emailSharePlace;
	public Boolean emailConfirmEvent;
	public Boolean phoneComment;
	public Boolean phoneCreateEvent;
	public Boolean phoneFriendFbJoin;
	public Boolean phoneFriendRequest;
	public Boolean phoneSharePlace;
	public Boolean phoneConfirmEvent;

	public FriendNotification() {
		emailComment=false;
		emailCreateEvent=false;
		emailFriendFbJoin=false;
		emailFriendRequest=false;
		emailSharePlace=false;
		emailConfirmEvent=false;
		phoneComment=false;
		phoneCreateEvent=false;
		phoneFriendFbJoin=false;
		phoneFriendRequest=false;
		phoneSharePlace=false;
		phoneConfirmEvent=false;
	}
}
