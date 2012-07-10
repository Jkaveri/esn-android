package esn.classes;

import esn.models.S2TResult;

public interface IVoiceCallBack {
	public void s2tHviteCall(final S2TResult result);
	public void autoStopRecording();
}
