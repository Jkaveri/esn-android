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

	private Codec(int mode) {
		System.loadLibrary("ilbc-codec");
		init(mode);
	}

	public static Codec instance(int mode) {
		return new Codec(mode);
	}
}