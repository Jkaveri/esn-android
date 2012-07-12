package esn.classes;

public interface RecordListener {
	public void onSpeaking();
	public void onSilenting();
	public void onStartingRecord();
	public void onStopedRecord();
}