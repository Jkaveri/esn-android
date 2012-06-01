package esn.classes;

import java.util.Date;

public class EventLabels {
	private int EventTypeID;
	private String EventTypeName;
	private String LabelImage;
	private Date Time;
	private Boolean Status;
	
	public int getEventTypeID() {
		return EventTypeID;
	}
	public void setEventTypeID(int eventTypeID) {
		EventTypeID = eventTypeID;
	}
	public String getEventTypeName() {
		return EventTypeName;
	}
	public void setEventTypeName(String eventTypeName) {
		EventTypeName = eventTypeName;
	}
	public String getLabelImage() {
		return LabelImage;
	}
	public void setLabelImage(String labelImage) {
		LabelImage = labelImage;
	}
	public Date getTime() {
		return Time;
	}
	public void setTime(Date time) {
		Time = time;
	}
	public Boolean getStatus() {
		return Status;
	}
	public void setStatus(Boolean status) {
		this.Status = status;
	}
	
}
