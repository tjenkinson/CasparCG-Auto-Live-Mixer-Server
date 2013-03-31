package tjenkinson.caspar_audio_looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tjenkinson.caspar_serverconnection.CaspSocket;


public class Program {
	
	private CaspSocket caspSocket = null;
	
	private String caspAddress = "localhost";
	private int caspPort = 5250;
	private int caspChan = 1;
	private int[] caspLayers = {5, 6, 7, 8};
	private LayerGroup[] layerGroups = new LayerGroup[2];
	private int liveLayerGroup = 1;
	private Section queuedLayerSection = null;
	private Object lock1 = new Object();
	private int currentActionNo = -1;
	private int tickerTime = 10;
	
	
	private ArrayList<Section> sections = new ArrayList<Section>();
	
	ArrayList<Action> actionQueue = new ArrayList<Action>();
	Action currentAction = null;
	
	public Program() throws IOException {
				
		initSocket();
		
		initLayerGroups();

		// load in sections
		Section section1 = new Section("opening", 0, 4950, 0, 4950);
		sections.add(section1);
		
		Section section2 = new Section("part1", 0, 9905, 0, 9910);
		section2.addBreakPoint(0);
		section2.addBreakPoint(4969);
		section2.addBreakPoint(9910);
		sections.add(section2);
		
		Section section3 = new Section("part2", 0, 4916, 0, 4916);
		sections.add(section3);
		
		Section section4 = new Section("ending", 0, 5929, 0, 5929);
		sections.add(section4);
		
		Timer timer = new Timer(false);
		timer.schedule(new Ticker(), 0, tickerTime);
		
		addAction(new Action(sections.get(0), 1, false));
		addAction(new Action(sections.get(1), 0, true));

		addAction(new Action(sections.get(2), 1, false));
		addAction(new Action(sections.get(1), 0, true));
		addAction(new Action(sections.get(2), 1, false));
		addAction(new Action(sections.get(1), 0, true));
		addAction(new Action(sections.get(3), 1, false));
		
		// actions would be queued dynamically
	}
	
	public void initSocket() {
		
		try {
			caspSocket = new CaspSocket(caspAddress, caspPort);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void initLayerGroups() {
		layerGroups[0] = new LayerGroup(caspSocket, caspChan, caspLayers[0], caspLayers[1]);
		layerGroups[1] = new LayerGroup(caspSocket, caspChan, caspLayers[2], caspLayers[3]);
	}
	
	public void addAction(Action action) throws IOException {
		synchronized (lock1) {
			actionQueue.add(action);
			// determine if the action before this one should now be removed
			// special case for this is if it has no loops and a break point at 0
			if (actionQueue.size() > 1) {
				Action previousAction = actionQueue.get(actionQueue.size()-2);
				if (previousAction.getRemainingPlays() == 0 && previousAction.getSection().getBreakPoints().get(0) == 0) {
					actionQueue.remove(actionQueue.size()-2);
				}
			}
			// load into queued layer group if not loaded already
			loadQueuedLayer();
		}
	}
	
	private void loadQueuedLayer() throws IOException {
		synchronized (lock1) {
			int queuedLayerGroup = 0;
			if (liveLayerGroup == 0) {
				queuedLayerGroup = 1;
			}
			
			// nothing to queue
			if (actionQueue.isEmpty()) {
				return;
			}
			
			Section nextSection = actionQueue.get(0).getSection();
			// section already queued
			if (queuedLayerSection == nextSection) {
				return;
			}
			queuedLayerSection = nextSection;
			layerGroups[queuedLayerGroup].load(nextSection);
			return;
		}
	}
	
	private class Ticker extends TimerTask {
		private Action getNextAction() {
			synchronized (lock1) {
				Action action = null;
				if (!actionQueue.isEmpty()) {
					currentActionNo++;
					action = actionQueue.remove(0);
				}
				return action;
			}
		}
		
		public synchronized void run() {
			
			// stop the playing layer group and play the other one
			final int DO_NOTHING = 0;
			// stop the playing layer group and play the other one
			final int SWAP_LAYER = 1;
			// send the play signal to the current layer group
			final int SEND_PLAY = 2;
			// send the stop signal to the current layer group (which unloads the section from it)
			final int SEND_STOP = 3;
			
			int action = DO_NOTHING;
			
			boolean justStarted = false;
				
			synchronized (lock1) {
				
				if (currentAction == null) {
					currentAction = getNextAction();
					// will have been queued when it was added
					if (currentAction == null) {
						return;
					}
					justStarted = true;
				}
				
				if (justStarted) {
					action = SWAP_LAYER;
				}
				else {
					
					// current time in the playing music
					long currentTime = layerGroups[liveLayerGroup].getTime();
					
					// at an out point and there is something waiting in the queue
					if (currentAction.passedBreakPoint(currentTime) && !actionQueue.isEmpty()) {
						currentAction = getNextAction();
						action = SWAP_LAYER;
					}
					// if reached the out point.
					// will only be true if done required loops and is set not to keep playing 
					else if (currentAction.passedOutPoint(currentTime)) {
						if (!actionQueue.isEmpty()) {
							currentAction = getNextAction();
							action = SWAP_LAYER;
						}
						else {
							action = SEND_STOP;
						}
					}
					// if passed the end of the file then start it playing again
					else if (currentAction.passedEndPoint(currentTime)) {
						action = SEND_PLAY;
					}
				}
			
				if (action == SWAP_LAYER) {
					
					int oldLiveLayerGroup = liveLayerGroup;
					if (liveLayerGroup == 1) {
						liveLayerGroup = 0;
					}
					else {
						liveLayerGroup = 1;
					}
					queuedLayerSection = null;
					
					// stop old layer group
					try {
						layerGroups[oldLiveLayerGroup].stop();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// play new layer group
					try {
						layerGroups[liveLayerGroup].play();
						currentAction.registerPlay();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// update the queued layer
					try {
						loadQueuedLayer();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (action == SEND_PLAY) {
					try {
						layerGroups[liveLayerGroup].play();
						currentAction.registerPlay();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (action == SEND_STOP) {
					try {
						layerGroups[liveLayerGroup].stop();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentAction = null;
				}
			}
		}
	}
}
