package com.jagex.runescape;// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Sound {

	public Sound(int i) {
		anInt665 = -573;
		anInt666 = -252;
		for (tracks = new SoundTrack[10]; i >= 0;)
			throw new NullPointerException();

	}

	public static void load(Buffer _buf) {
		data = new byte[0x6baa8];
		buf = new Buffer(data);
		SoundTrack.init();
		do {
			int id = _buf.getUnsignedLEShort();
			if (id == 65535)
				return;
			sounds[id] = new Sound(-524);
			sounds[id].init(aByte664, _buf);
			anIntArray669[id] = sounds[id].method368(0);
		} while (true);
	}

	public static Buffer forId(int i, int id) {
		if (sounds[id] != null) {
			Sound sound = sounds[id];
			return sound.toWaveform(i);
		} else {
			return null;
		}
	}

	public void init(byte byte0, Buffer buf) {
		for (int track = 0; track < 10; track++) {
			int present = buf.getUnsignedByte();
			if (present != 0) {
				buf.currentPosition--;
				tracks[track] = new SoundTrack();
				tracks[track].init(aByte664, buf);
			}
		}

		anInt673 = buf.getUnsignedLEShort();
		anInt674 = buf.getUnsignedLEShort();
		if (byte0 == 6) {
			byte0 = 0;
			return;
		} else {
			anInt666 = 64;
			return;
		}
	}

	public int method368(int i) {
		int j = 0x98967f;
		for (int k = 0; k < 10; k++)
			if (tracks[k] != null && tracks[k].pauseMillis / 20 < j)
				j = tracks[k].pauseMillis / 20;

		if (anInt673 < anInt674 && anInt673 / 20 < j)
			j = anInt673 / 20;
		if (j == 0x98967f || j == 0)
			return 0;
		for (int l = 0; l < 10; l++)
			if (tracks[l] != null)
				tracks[l].pauseMillis -= j * 20;

		if (i != 0)
			aBoolean667 = !aBoolean667;
		if (anInt673 < anInt674) {
			anInt673 -= j * 20;
			anInt674 -= j * 20;
		}
		return j;
	}

	public Buffer toWaveform(int j) {
		int length = method370(j);
		buf.currentPosition = 0;
		buf.putInt(0x52494646);    // "RIFF"
		buf.putLEInt(36 + length); // chunk length
		buf.putInt(0x57415645);    // "WAVE" (format)
		buf.putInt(0x666d7420);    // "FMT " (subchunk id)
		buf.putLEInt(16);       // subchunk size
		buf.putLEShort(1);      // PCM
		buf.putLEShort(1);      // channels (mono)
		buf.putLEInt(22050);    // sample rate
		buf.putLEInt(22050);    // byte rate
		buf.putLEShort(1);      // block alignment
		buf.putLEShort(8);      // bits per sample
		buf.putInt(0x64617461); // "DATA" (subchunk id)
		buf.putLEInt(length);   // length
		buf.currentPosition += length;
		return buf;
	}

	public int method370(int i) {
		int millis = 0;
		for (int track = 0; track < 10; track++)
			if (tracks[track] != null && tracks[track].soundMillis + tracks[track].pauseMillis > millis)
				millis = tracks[track].soundMillis + tracks[track].pauseMillis;

		if (millis == 0)
			return 0;
		int l = (22050 * millis) / 1000;
		int i1 = (22050 * anInt673) / 1000;
		int j1 = (22050 * anInt674) / 1000;
		if (i1 < 0 || i1 > l || j1 < 0 || j1 > l || i1 >= j1)
			i = 0;
		int length = l + (j1 - i1) * (i - 1);
		for (int sample = 44; sample < length + 44; sample++)
			data[sample] = -128;

		for (int track = 0; track < 10; track++)
			if (tracks[track] != null) {
				int soundSamples = (tracks[track].soundMillis * 22050) / 1000;
				int pauseSamples = (tracks[track].pauseMillis * 22050) / 1000;
				int samples[] = tracks[track].method191(soundSamples, tracks[track].soundMillis);
				for (int soundSample = 0; soundSample < soundSamples; soundSample++) {
					int sample = (data[soundSample + pauseSamples + 44] & 0xff) + (samples[soundSample] >> 8);
					if ((sample & 0xffffff00) != 0)
						sample = ~(sample >> 31);
					data[soundSample + pauseSamples + 44] = (byte) sample;
				}

			}

		if (i > 1) {
			i1 += 44;
			j1 += 44;
			l += 44;
			int k2 = (length += 44) - l;
			for (int j3 = l - 1; j3 >= j1; j3--)
				data[j3 + k2] = data[j3];

			for (int k3 = 1; k3 < i; k3++) {
				int l2 = (j1 - i1) * k3;
				for (int i4 = i1; i4 < j1; i4++)
					data[i4 + l2] = data[i4];

			}

			length -= 44;
		}
		return length;
	}

	public static byte aByte664 = 6;
	public int anInt665;
	public int anInt666;
	public static boolean aBoolean667 = true;
	public static Sound sounds[] = new Sound[5000];
	public static int anIntArray669[] = new int[5000];
	public static byte data[];
	public static Buffer buf;
	public SoundTrack tracks[];
	public int anInt673;
	public int anInt674;

}
