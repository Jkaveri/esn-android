package esn.classes;

public interface VoiceListener {
	public void onS2TPostBack(S2TParser result);
	public void onStopedRecord();
}
