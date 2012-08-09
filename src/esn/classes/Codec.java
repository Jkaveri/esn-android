/*
 * By lnbienit@gmail.com
 */

package esn.classes;

public class Codec {
	public native int encode(byte[] data, int dataOffset, int dataLength,
			byte[] samples, int samplesOffset);

	public native int decode(byte[] samples, int samplesOffset,
			int samplesLength, byte[] data, int dataOffset);

	private native int init(int mode);

	private Codec() {
		System.loadLibrary("ilbc-codec");
		//iLBC encode block 30msec
		init(30);
	}

	private static Codec INSTANCE;
	
	public static Codec instance() {
		if(INSTANCE == null){
			INSTANCE = new Codec();
		}
		return INSTANCE;
	}
}