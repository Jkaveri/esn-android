/*
 * By lnbienit@gmail.com
 */



package esn.classes;
/*
WAV File Specification
FROM http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
The canonical WAVE format starts with the RIFF header:
0         4   ChunkID          Contains the letters "RIFF" in ASCII form
                              (0x52494646 big-endian form).
4         4   ChunkSize        36 + SubChunk2Size, or more precisely:
                              4 + (8 + SubchunkSize) + (8 + SubChunk2Size)
                              This is the size of the rest of the chunk 
                              following this number.  This is the size of the 
                              entire file in bytes minus 8 bytes for the
                              two fields not included in this count:
                              ChunkID and ChunkSize.
8         4   Format           Contains the letters "WAVE"
                              (0x57415645 big-endian form).

The "WAVE" format consists of two subchunks: "fmt " and "data":
The "fmt " subchunk describes the sound data's format:
12        4   SubchunkID      Contains the letters "fmt "
                              (0x666d7420 big-endian form).
16        4   SubchunkSize    16 for PCM.  This is the size of the
                              rest of the Subchunk which follows this number.
20        2   AudioFormat      PCM = 1 (i.e. Linear quantization)
                              Values other than 1 indicate some 
                              form of compression.
22        2   NumChannels      Mono = 1, Stereo = 2, etc.
24        4   SampleRate       8000, 44100, etc.
28        4   ByteRate         == SampleRate * NumChannels * BitsPerSample/8
32        2   BlockAlign       == NumChannels * BitsPerSample/8
                              The number of bytes for one sample including
                              all channels. I wonder what happens when
                              this number isn't an integer?
34        2   BitsPerSample    8 bits = 8, 16 bits = 16, etc.

The "data" subchunk contains the size of the data and the actual sound:
36        4   dataChunkID      Contains the letters "data"
                              (0x64617461 big-endian form).
40        4   dataChunkSize    == NumSamples * NumChannels * BitsPerSample/8
                              This is the number of bytes in the data.
                              You can also think of this as the size
                              of the read of the subchunk following this 
                              number.
44        *   Data             The actual sound data.


NOTE TO READERS:

The thing that makes reading wav files tricky is that java has no unsigned types.  This means that the
binary data can't just be read and cast appropriately.  Also, we have to use larger types
than are normally necessary.

In many languages including java, an integer is represented by 4 bytes.  The issue here is
that in most languages, integers can be signed or unsigned, and in wav files the  integers
are unsigned.  So, to make sure that we can store the proper values, we have to use longs
to hold integers, and integers to hold shorts.

Then, we have to convert back when we want to save our wav data.

It's complicated, but ultimately, it just results in a few extra functions at the bottom of
this file.  Once you understand the issue, there is no reason to pay any more attention
to it.


ALSO:

This code won't read ALL wav files.  This does not use to full specification.  It just uses
a trimmed down version that most wav files adhere to.
*/



import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WAVFormatConver {
	private long chunkSize;
	private long subchunkSize;
	private int bitsPerSample;
	private int format;
	private long channels;
	private long sampleRate;
	private long byteRate;
	private int blockAlign;
	private long dataChunkSize;
	private byte[] dataPCM;
	
	private byte[] wavData;
	private DataOutputStream dos;
	private ByteArrayOutputStream bufferStream;
	
	public void setBuffer(byte[] dataPCM){
		this.dataPCM = dataPCM;
		dataPCM = null;//giai phong bo nho
		if(dos != null){
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		bufferStream = new ByteArrayOutputStream();
		dos = new DataOutputStream(bufferStream);
	}
	
	public void setBuffer(FileOutputStream fos){
		bufferStream = null;
		if(dos != null){
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dos = new DataOutputStream(fos);
	}
	
	public long getSubchunkSize() {
		return subchunkSize;
	}

	public void setSubchunkSize(long subchunkSize) {
		this.subchunkSize = subchunkSize;
	}

	public int getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(int bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}
	
	public void setSampleRate(long sampleRate) {
		this.sampleRate = sampleRate;
	}

	public long getChannels() {
		return channels;
	}

	public void setChannels(long channels) {
		this.channels = channels;
	}

	public long getSampleRate() {
		return sampleRate;
	}

	public long getByteRate() {
		return byteRate;
	}

	public int getBlockAlign() {
		return blockAlign;
	}

	public long getdataChunkSize() {
		return dataChunkSize;
	}

	public void setDefaultWAVFormat(){
		subchunkSize = 16; //16 => PCM
		bitsPerSample = 16; //16 => 16BIT, 8 => 8BIT
		format = 1;// 1 FOR PCM
		channels = 1; //1 => MONO, 2 => STEREO
		sampleRate = AudioRecorder.SAMPLE_RATE;
	}
	
	public void prepare(){
		dataChunkSize = dataPCM.length;
		byteRate = sampleRate * channels * bitsPerSample/8;
		blockAlign = (int) (channels * bitsPerSample/8);
		chunkSize = 36 + (dataChunkSize * channels * bitsPerSample/8);
	}
	
	public boolean conver(){
		boolean returnBol = true;
		try{
			dos.writeBytes("RIFF");                                 // 00 - RIFF
			dos.write(WAVUtils.intToByteArray((int)chunkSize), 0, 4);      // 04 - how big is the rest of this file?
			dos.writeBytes("WAVE");                                 // 08 - WAVE
			dos.writeBytes("fmt ");                                 // 12 - fmt 
			dos.write(WAVUtils.intToByteArray((int)subchunkSize), 0, 4);  // 16 - size of this chunk
			dos.write(WAVUtils.shortToByteArray((short)format), 0, 2);     // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
			dos.write(WAVUtils.shortToByteArray((short)channels), 0, 2);   // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
			dos.write(WAVUtils.intToByteArray((int)sampleRate), 0, 4);     // 24 - samples per second (numbers per second)
			dos.write(WAVUtils.intToByteArray((int)byteRate), 0, 4);       // 28 - bytes per second
			dos.write(WAVUtils.shortToByteArray((short)blockAlign), 0, 2); // 32 - # of bytes in one sample, for all channels
			dos.write(WAVUtils.shortToByteArray((short)bitsPerSample), 0, 2);  // 34 - how many bits in a sample(number)?  usually 16 or 24
			dos.writeBytes("data");                                 // 36 - data
			dos.write(WAVUtils.intToByteArray((int)dataChunkSize), 0, 4);       // 40 - how big is this data chunk
			dos.write(dataPCM);                        // 44 - the actual data itself - just a long string of numbers
			dos.flush();
			dos.close();
		} catch (IOException e) {
			returnBol = false;
		} finally{
			try {
				dos.close();
			} catch (IOException e) {}
		}
		
		dataPCM = null;
		if(bufferStream != null){
			wavData = bufferStream.toByteArray();
			bufferStream = null;
		}else{
			wavData = null;
		}
		
		dos = null;
		return returnBol;
	}
	
    public byte[] getWAVData(){
    	return wavData;
    }
    
    public void clearBuffer(){
    	wavData = null;
    }
}
