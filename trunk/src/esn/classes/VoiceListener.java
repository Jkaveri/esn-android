package esn.classes;

import esn.models.S2TResult;

public interface VoiceListener {
	public void onS2TPostBack(final S2TResult result);
	public void onStopedRecord();
}
