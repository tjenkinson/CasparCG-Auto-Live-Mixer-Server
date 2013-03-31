package tjenkinson.caspar_audio_looper;

import java.util.ArrayList;

public class Section {
	
	private long start;
	private long duration;
	private String file = null;
	
	// offset of where to start on first play
	private long introOffset = -1;
	private long outPointOffset = -1;
	
	private ArrayList<Long> breakPoints = new ArrayList<Long>();
	
	public Section (String file, long start, long duration, long introOffset, long outPointOffset) {
		//TODO validate
		this.file = file;
		this.start = start;
		this.duration = duration;
		this.introOffset = introOffset;
		this.outPointOffset = outPointOffset;
	}
	
	public String getFile() {
		return file;
	}
	
	public long getDuration(boolean firstPlay) {
		long time = duration;
		
		if (firstPlay) {
			time -= introOffset;
		}
		
		return time;
	}
	
	public void addBreakPoint(long offset) {
		// TODO validate
		breakPoints.add(offset);
	}
	
	public long getStartTime(boolean firstPlay) {
		long time = start;
		if (firstPlay) {
			time += introOffset;
		}
		return time;
	}
	
	//TODO: maybe clone and pass that to prevent modification
	public ArrayList<Long> getBreakPoints() {
		return breakPoints;
	}
	
	public boolean passedEnd(long time) {
		return time>=start+duration;
	}
	
	public long getOutPoint() {
		return start+outPointOffset;
	}
}
