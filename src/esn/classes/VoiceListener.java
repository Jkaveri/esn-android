package esn.classes;

import esn.models.S2TResult;

public interface VoiceListener {
	public void onS2TPostBack(S2TResult result);
	public void onStopedRecord();
}
