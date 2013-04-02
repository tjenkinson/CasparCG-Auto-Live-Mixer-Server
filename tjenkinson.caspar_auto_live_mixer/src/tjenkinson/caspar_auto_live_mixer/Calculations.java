package tjenkinson.caspar_auto_live_mixer;

public class Calculations {
	public static long timeToFrame(long time, int frameRate) {
		return (long) ((time/1000.0) * frameRate);
	}
}
