package tjenkinson.caspar_auto_live_mixer;

import java.util.ArrayList;

public class Action {
	private Section section;
	
	// counts down amount of required plays
	private int playsRemaining;
	
	// keep playing when no required remaining
	private boolean keepPlaying;

	private ArrayList<Long> breakPoints;
	private int lookingBreakPointAfter = -1;
	
	public Action(Section section, int playCount, boolean keepPlaying) {
		//TODO validate
		this.section = section;
		this.playsRemaining = playCount;
		this.keepPlaying = keepPlaying;
		breakPoints = getSection().getBreakPoints();
	}
	
	public Section getSection() {
		return section;
	}
	
	public boolean keepPlaying() {
		return (getRemainingPlays() > 0 || getKeepPlaying());
	}
	
	public void registerPlay() {
		if (playsRemaining > 0) {
			playsRemaining--;
		}
		lookingBreakPointAfter = -1;
	}
	
	public int getRemainingPlays() {
		return playsRemaining;
	}
	
	private boolean getKeepPlaying() {
		return keepPlaying;
	}
	
	// passed a point were this section could end
	public boolean passedBreakPoint(long time) {
		if (getRemainingPlays() > 0) {
			return true;
		}
		for (int i=lookingBreakPointAfter+1; i<breakPoints.size(); i++) {
			if (time >= breakPoints.get(i)) {
				lookingBreakPointAfter = i;
				return true;
			}
		}
		return false;
	}
	
	public boolean passedOutPoint(long time) {
		return (getRemainingPlays() == 0 && !getKeepPlaying() && time >= getSection().getOutPoint());
	}
	
	// no longer playing
	public boolean passedEndPoint(long time) {
		return getSection().passedEnd(time);
	}
}
