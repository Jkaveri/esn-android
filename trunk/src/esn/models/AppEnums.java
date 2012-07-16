package esn.models;

public class AppEnums {
	
	public static class GeneralStatus
	{
		public static int Active = 1;
		public static int Inactive = 2;
		public static int Deleted = 3;
	    
	}
	public static class EventStatus{
		public static int Waiting = 0;
		public static int Confirmed = 1;
		public static int Deleted = 3;
	}
	public static class ShareTypes
	{
	    public static int Private = 0;
	   public static int Public = 1;
	   public static int Custom = 2;
	}
	public static class AccountStatus
	{
	   public static int NotConfirmed = 0;
	   public static int Confirmed = 1;
	   public static int Locked = 2;
	}
	public static class AccountRoles
	{
	   public static int Admin = 1;
	   public static int Moderator = 2;
	   public static int User = 3;
	}
	public static class RelationStatus
	{
	   public static int Pending = 0;
	   public static int Confirmed = 1;
	}
	public static class RelationType
	{
	    public static int Friend = 1;
	    public static int Family = 2;
	}
	public static class NotificationStatus
	{
	    public static int UnRead = 0;
	   public static int Read = 1;
	}
	public static class TargetTypes
	{
	   public static int Relation = 0;
	   public static int Events = 1;
	   public static int Comment = 2;
	}

}
