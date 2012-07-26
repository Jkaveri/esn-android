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
		init(30);
	}

	static final private Codec INSTANCE = new Codec();
	
	public static Codec instance() {
		return INSTANCE;
	}
}