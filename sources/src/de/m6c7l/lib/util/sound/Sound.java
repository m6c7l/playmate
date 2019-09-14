/*
 * Copyright (c) 2017, Manfred Constapel
 * This file is licensed under the terms of the MIT license.
 */


package de.m6c7l.lib.util.sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	private AudioFormat format = null;
	private int size = -1;
	private byte[] audio = null;
	private DataLine.Info info = null;
	private Clip clip = null;
	private boolean drain = false;

	public Sound(URI uri) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		this(uri.toURL());
	}
	
	public Sound(URL url) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		this(AudioSystem.getAudioInputStream(url));
	}
	
	public Sound(AudioInputStream audioInputStream) throws IOException, LineUnavailableException {
	    BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);
	    format = audioInputStream.getFormat();
	    size = (int)(format.getFrameSize()*audioInputStream.getFrameLength());
	    audio = new byte[size];
	    info = new DataLine.Info(Clip.class, format, size);
	    bufferedInputStream.read(audio, 0, size);
		clip = (Clip) AudioSystem.getLine(info);
	}
	
	public void setDrain(boolean value) {
		this.drain = value;
	}
	
	public boolean isDrain() {
		return this.drain;
	}
	
	public byte[] getSamples() {
		return audio;
	}
	  
	public boolean loop() {
	    try {
	    	if (!clip.isOpen()) clip.open(format, audio, 0, size);
	    	clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (LineUnavailableException e) {
			return false;
		}
	    return true;
	}
	
	public boolean stop() {
        if (clip==null) return false;
        clip.stop();
        return true;
	}
	
	public boolean start() {
		return start(0.0f,0.0f);
	}
    
	public boolean start(float delay, float decay) {
		if (decay==0.0f) {
		    try {
		    	if (!clip.isOpen()) clip.open(format, audio, 0, size);
		        clip.start();
				if (drain) clip.drain();
			} catch (LineUnavailableException e) {
				return false;
			}			
		} else {
		    // create the sound stream
		    InputStream is = new ByteArrayInputStream(audio);
		    // create an echo with a 1/x-sample buffer (1/x sec for y Hz sound) and a z % decay
		    EchoFilter filter = new EchoFilter((int)(format.getSampleRate()*delay),decay);
		    // create the filtered sound stream
		    FilteredSoundStream fss = new FilteredSoundStream(is, filter);
		    // go and play
		    try {
		    	byte[] data = toByteArray(fss);
		        clip.open(format, data, 0, data.length);
		        clip.start();
				if (drain) clip.drain();
			} catch (IOException e) {
			} catch (LineUnavailableException e) {
				return false;
			}		
		}
	    return true;
	}

	private byte[] toByteArray(InputStream source) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nread = 0;
		byte[] data = new byte[16384];
		while ((nread=source.read(data,0,data.length)) != -1) {
			buffer.write(data, 0, nread);
		}
		buffer.flush();
		return buffer.toByteArray();
	}
	
	public static void stream(InputStream source, AudioFormat format, boolean drain) throws IOException, LineUnavailableException  {
		// use a short, 100ms (1/10th sec) buffer for real-time
		// change to the sound stream
		int bufferSize = format.getFrameSize() * Math.round(format.getSampleRate() / 10);
		byte[] buffer = new byte[bufferSize];
		// create a line to play to
		SourceDataLine line;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format, bufferSize);
		// start the line
		line.start();
		// copy data to the line
		int numBytesRead = 0;
		while (numBytesRead != -1) {
			numBytesRead = source.read(buffer, 0, buffer.length);
			if (numBytesRead != -1) {
				line.write(buffer, 0, numBytesRead);
			}
		}
		// wait until all data is played, then close the line
		if (drain) line.drain();
		line.close();
	}
	
	/**
	 * The EchoFilter class is a SoundFilter that emulates an echo.
	 */
	protected static class EchoFilter extends SoundFilter {

		private short[] delayBuffer;
		private int delayBufferPos;
		private float decay;

		public EchoFilter(int numDelaySamples, float decay) {
			delayBuffer = new short[numDelaySamples];
			this.decay = decay;
		}

		public int getRemainingSize() {
			float finalDecay = 0.01f;
			// derived from Math.pow(decay,x) <= finalDecay
			int numRemainingBuffers = (int) Math.ceil(Math.log(finalDecay) / Math.log(decay));
			int bufferSize = delayBuffer.length * 2;
			return bufferSize * numRemainingBuffers;
		}

		public void reset() {
			for (int i = 0; i < delayBuffer.length; i++) {
				delayBuffer[i] = 0;
			}
			delayBufferPos = 0;
		}

		public void filter(byte[] samples, int offset, int length) {
			for (int i = offset; i < offset + length; i += 2) {
				// update the sample
				short oldSample = getSample(samples, i);
				short newSample = (short) (oldSample + decay * delayBuffer[delayBufferPos]);
				setSample(samples, i, newSample);
				// update the delay buffer
				delayBuffer[delayBufferPos] = newSample;
				delayBufferPos++;
				if (delayBufferPos == delayBuffer.length) {
					delayBufferPos = 0;
				}
			}
		}

	}

	/**
	 * The FilteredSoundStream class is a FilterInputStream that applies a
	 * SoundFilter to the underlying input stream.
	 */
	protected static class FilteredSoundStream extends FilterInputStream {

		private static final int REMAINING_SIZE_UNKNOWN = -1;

		private SoundFilter soundFilter;

		private int remainingSize;

		public FilteredSoundStream(InputStream in, SoundFilter soundFilter) {
			super(in);
			this.soundFilter = soundFilter;
			remainingSize = REMAINING_SIZE_UNKNOWN;
		}

		public int read(byte[] samples, int offset, int length) throws IOException {
			// read and filter the sound samples in the stream
			int bytesRead = super.read(samples, offset, length);
			if (bytesRead > 0) {
				soundFilter.filter(samples, offset, bytesRead);
				return bytesRead;
			}
			// if there are no remaining bytes in the sound stream,
			// check if the filter has any remaining bytes ("echoes").
			if (remainingSize == REMAINING_SIZE_UNKNOWN) {
				remainingSize = soundFilter.getRemainingSize();
				// round down to nearest multiple of 4
				// (typical frame size)
				remainingSize = remainingSize / 4 * 4;
			}
			if (remainingSize > 0) {
				length = Math.min(length, remainingSize);
				// clear the buffer
				for (int i = offset; i < offset + length; i++) {
					samples[i] = 0;
				}
				// filter the remaining bytes
				soundFilter.filter(samples, offset, length);
				remainingSize -= length;
				// return
				return length;
			} else {
				// end of stream
				return -1;
			}
		}

	}

	/**
	 * A abstract class designed to filter sound samples. Since SoundFilters may
	 * use internal buffering of samples, a new SoundFilter object should be
	 * created for every sound played. However, SoundFilters can be reused after
	 * they are finished by called the reset() method.
	 * Assumes all samples are 16-bit, signed, little-endian format.
	 */
	protected static abstract class SoundFilter {

		public void reset() {
			// do nothing
		}

		public int getRemainingSize() {
			return 0;
		}

		public void filter(byte[] samples) {
			filter(samples, 0, samples.length);
		}

		public abstract void filter(byte[] samples, int offset, int length);

		public short getSample(byte[] buffer, int position) {
			return (short) (((buffer[position + 1] & 0xff) << 8) | (buffer[position] & 0xff));
		}

		public void setSample(byte[] buffer, int position, short sample) {
			buffer[position] = (byte) (sample & 0xff);
			buffer[position + 1] = (byte) ((sample >> 8) & 0xff);
		}

	}

}
