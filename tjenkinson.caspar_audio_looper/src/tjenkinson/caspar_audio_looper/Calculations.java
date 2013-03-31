package tjenkinson.caspar_audio_looper;

public class Calculations {
	public static long timeToFrame(long time, int frameRate) {
		return (long) ((time/1000.0) * frameRate);
	}
}
