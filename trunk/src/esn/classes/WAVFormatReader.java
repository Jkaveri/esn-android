/*
 * By lnbienit@gmail.com
 */


package esn.classes;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class WAVFormatReader {
	private DataInputStream is;
	private long dataChunkSize;
	private byte[] data;
	private int bitsPerSample;
	private int blockAlign;
	private long byteRate;
	private long sampleRate;
	private int channels;
	private int format;
	private long subChunkSize;
	private long chunkSize;
	
	public long getDataChunkSize() {
		return dataChunkSize;
	}

	public byte[] getData() {
		return data;
	}

	public int getBitsPerSample() {
		return bitsPerSample;
	}

	public int getBlockAlign() {
		return blockAlign;
	}

	public long getByteRate() {
		return byteRate;
	}

	public long getSampleRate() {
		return sampleRate;
	}

	public int getChannels() {
		return channels;
	}

	public int getFormat() {
		return format;
	}

	public long getsubChunkSize() {
		return subChunkSize;
	}

	public long getChunkSize() {
		return chunkSize;
	}

	public void setBuffer(byte[] byteArrWAV){
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		is = new DataInputStream(new ByteArrayInputStream(byteArrWAV));
	}
	
	public void setBuffer(InputStream ips){
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		is = new DataInputStream(ips);
	}
	
	public void clearBuffer(){
		data = null;
	}
	
	public boolean read()
	{
		byte[] tmpLong = new byte[4];
		byte[] tmpInt = new byte[2];

		try
		{
			String chunkID = "" + (char)is.readByte() + (char)is.readByte() + (char)is.readByte() + (char)is.readByte();
			Log.i("WAVFormatReader", "chunkID: " + chunkID);
			
			is.read(tmpLong); // read the ChunkSize
			chunkSize = WAVUtils.byteArrayToLong(tmpLong);
			
			String formatTag = "" + (char)is.readByte() + (char)is.readByte() + (char)is.readByte() + (char)is.readByte();
			Log.i("WAVFormatReader", "formatTag: " + formatTag);
			
			String subChunkID = "" + (char)is.readByte() + (char)is.readByte() + (char)is.readByte() + (char)is.readByte();
			Log.i("WAVFormatReader", "subChunkID: " + subChunkID);
			
			is.read(tmpLong); // read the subChunkSize
			subChunkSize = WAVUtils.byteArrayToLong(tmpLong);

			is.read(tmpInt); // read the audio format.  This should be 1 for PCM
			format = WAVUtils.byteArrayToInt(tmpInt);

			is.read(tmpInt); // read the # of channels (1 or 2)
			channels = WAVUtils.byteArrayToInt(tmpInt);
			
			is.read(tmpLong); // read the samplerate
			sampleRate = WAVUtils.byteArrayToLong(tmpLong);

			is.read(tmpLong); // read the byterate
			byteRate = WAVUtils.byteArrayToLong(tmpLong);

			is.read(tmpInt); // read the blockalign
			blockAlign = WAVUtils.byteArrayToInt(tmpInt);

			is.read(tmpInt); // read the bitspersample
			bitsPerSample = WAVUtils.byteArrayToInt(tmpInt);

			
			String dataChunkID = "" + (char)is.readByte() + (char)is.readByte() + (char)is.readByte() + (char)is.readByte();
			Log.i("WAVFormatReader", "dataChunkID: " + dataChunkID);
			
			is.read(tmpLong); // read the size of the data
			dataChunkSize = WAVUtils.byteArrayToLong(tmpLong);

			// read the data chunk
			data = new byte[(int)dataChunkSize];
			is.read(data);

			// close the input stream
			is.close();
		}
		catch(IOException e)
		{
			try {
				is.close();
			} catch (IOException x) {
			}
			is = null;
			return false;
		}
		return true; // this should probably be something more descriptive
	}
}
