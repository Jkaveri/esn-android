package esn.classes;

public interface RecordHandler {
	public void onSpeaking();
	public void onSilenting();
	public void onStartingRecord();
	public void onStopingRecord();
}