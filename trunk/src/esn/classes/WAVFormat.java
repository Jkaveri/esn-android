/*
WAV File Specification
FROM http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
The canonical WAVE format starts with the RIFF header:
0         4   ChunkID          Contains the letters "RIFF" in ASCII form
                              (0x52494646 big-endian form).
4         4   ChunkSize        36 + SubChunk2Size, or more precisely:
                              4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
                              This is the size of the rest of the chunk 
                              following this number.  This is the size of the 
                              entire file in bytes minus 8 bytes for the
                              two fields not included in this count:
                              ChunkID and ChunkSize.
8         4   Format           Contains the letters "WAVE"
                              (0x57415645 big-endian form).

The "WAVE" format consists of two subchunks: "fmt " and "data":
The "fmt " subchunk describes the sound data's format:
12        4   Subchunk1ID      Contains the letters "fmt "
                              (0x666d7420 big-endian form).
16        4   Subchunk1Size    16 for PCM.  This is the size of the
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
36        4   Subchunk2ID      Contains the letters "data"
                              (0x64617461 big-endian form).
40        4   Subchunk2Size    == NumSamples * NumChannels * BitsPerSample/8
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

package esn.classes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class WAVFormat {
	private long mySubChunk1Size;
	private int myBitsPerSample;
	private int myFormat;
	private long myChannels;
	private long mySampleRate;
	private long myByteRate;
	private int myBlockAlign;
	private byte[] bufferData;
	private long myDataSize;
	private long myChunk2Size;
	private long myChunkSize;
	private ByteArrayOutputStream bufferStream;
	private DataOutputStream dos;
	
	public WAVFormat(byte[] data){
		mySubChunk1Size = 16;
		myBitsPerSample= 16;
		myFormat = 1;
		myChannels = 1;
		mySampleRate = 8000;
		myDataSize = data.length;
		myByteRate = mySampleRate * myChannels * myBitsPerSample/8;
		myBlockAlign = (int) (myChannels * myBitsPerSample/8);
		myChunk2Size =  myDataSize * myChannels * myBitsPerSample/8;
		myChunkSize = 36 + myChunk2Size;
		
		
		bufferStream = new ByteArrayOutputStream();
		dos = new DataOutputStream(bufferStream);
		try{
			dos.writeBytes("RIFF");                                 // 00 - RIFF
			dos.write(intToByteArray((int)myChunkSize), 0, 4);      // 04 - how big is the rest of this file?
			dos.writeBytes("WAVE");                                 // 08 - WAVE
			dos.writeBytes("fmt ");                                 // 12 - fmt 
			dos.write(intToByteArray((int)mySubChunk1Size), 0, 4);  // 16 - size of this chunk
			dos.write(shortToByteArray((short)myFormat), 0, 2);     // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
			dos.write(shortToByteArray((short)myChannels), 0, 2);   // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
			dos.write(intToByteArray((int)mySampleRate), 0, 4);     // 24 - samples per second (numbers per second)
			dos.write(intToByteArray((int)myByteRate), 0, 4);       // 28 - bytes per second
			dos.write(shortToByteArray((short)myBlockAlign), 0, 2); // 32 - # of bytes in one sample, for all channels
			dos.write(shortToByteArray((short)myBitsPerSample), 0, 2);  // 34 - how many bits in a sample(number)?  usually 16 or 24
			dos.writeBytes("data");                                 // 36 - data
			dos.write(intToByteArray((int)myDataSize), 0, 4);       // 40 - how big is this data chunk
			dos.write(data);                        // 44 - the actual data itself - just a long string of numbers
			dos.flush();
			dos.close();
		} catch (IOException e) {
			Log.e("WAVFormat", "Khong ghi duoc, OutputStream", e);
		}
		bufferData = bufferStream.toByteArray();
	}
	
	private static byte[] intToByteArray(int i)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0x00FF);
        b[1] = (byte) ((i >> 8) & 0x000000FF);
        b[2] = (byte) ((i >> 16) & 0x000000FF);
        b[3] = (byte) ((i >> 24) & 0x000000FF);
        return b;
    }

    // convert a short to a byte array
	private static byte[] shortToByteArray(short data)
    {
        /*
         * NB have also tried:
         * return new byte[]{(byte)(data & 0xff),(byte)((data >> 8) & 0xff)};
         * 
         */

        return new byte[]{(byte)(data & 0xff),(byte)((data >>> 8) & 0xff)};
    }
    
    public byte[] getBufferData(){
    	return bufferData;
    }
}
