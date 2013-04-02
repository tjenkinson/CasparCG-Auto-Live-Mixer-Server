package tjenkinson.caspar_auto_live_mixer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import tjenkinson.caspar_serverconnection.CaspSocket;


public class Server {
	
	private CaspSocket caspSocket = null;
	
	private String caspAddress = null;
	private int caspPort = -1;
	private int caspChan = -1;
	private int serverPort = -1;
	private ServerSocketManager serverSocketManager = null;
	private int[] caspLayers = {-1, -1, -1, -1};
	private LayerGroup[] layerGroups = new LayerGroup[2];
	private int liveLayerGroup = 1;
	private Section queuedLayerSection = null;
	private Object lock1 = new Object();
	private int currentActionNo = -1;
	private int tickerTime = 10;
	
	
	private ArrayList<Section> sections = new ArrayList<Section>();
	
	ArrayList<Action> actionQueue = new ArrayList<Action>();
	Action currentAction = null;
	
	public Server(String caspAddress, int caspPort, int caspChan, int caspLayer1, int caspLayer2, int caspLayer3, int caspLayer4, int serverPort, String sectionsXmlFilePath) throws IOException {

		this.caspAddress = caspAddress;
		this.caspPort = caspPort;
		this.caspChan = caspChan;
		this.caspLayers[0] = caspLayer1;
		this.caspLayers[1] = caspLayer2;
		this.caspLayers[2] = caspLayer3;
		this.caspLayers[3] = caspLayer4;
		this.serverPort = serverPort;
		
		
		loadSections(sectionsXmlFilePath);
		initSocket();
		
		initLayerGroups();
		
		Timer timer = new Timer(false);
		timer.schedule(new Ticker(), 0, tickerTime);
		
		// start server to listen to action requests
		initServerSocket();
		
		
	}
	
	public Action createAction(int sectionNo, int plays, boolean keepPlaying) {
		
		Action action = null;
		// validate and create
		if (sectionNo < sections.size() && plays >= 0 && (plays != 0 || keepPlaying)) {
			action = new Action(sections.get(sectionNo), plays, keepPlaying);
		}
		return action;
	}
	
	private void loadSections(String sectionsXmlFilePath) throws IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    DocumentBuilder builder=null; 
	    Document document = null;

        try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}  
        try {
			document = builder.parse(new InputSource(sectionsXmlFilePath));
		} catch (SAXException e) {
			e.printStackTrace();
		}

        XPathExpression expr = null;
        Object hits = null;
		try {
			expr = XPathFactory.newInstance().newXPath().compile("/sections/section");
			hits = expr.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (hits instanceof NodeList) {
			NodeList list = (NodeList) hits;
			// loop through each <section>
			for (int i=0; i<list.getLength(); i++) {
				Element node = (Element) list.item(i);
				String file = node.getElementsByTagName("file").item(0).getTextContent();
				int start = Integer.parseInt(node.getElementsByTagName("start").item(0).getTextContent(), 10);
				int duration = Integer.parseInt(node.getElementsByTagName("duration").item(0).getTextContent(), 10);
				int inOffset = Integer.parseInt(node.getElementsByTagName("inOffset").item(0).getTextContent(), 10);
				int outOffset = Integer.parseInt(node.getElementsByTagName("outOffset").item(0).getTextContent(), 10);
				
				// validate
				if (	start < 0 ||
						duration <= 0 ||
						inOffset < 0 || inOffset >= duration ||
						outOffset < 0 || outOffset > duration) {
					// invalid
					// TODO: do something better than exiting
					System.exit(2);
				}
				
				Section addedSection = new Section(file, start, duration, inOffset, outOffset);
				sections.add(addedSection);
				
				boolean hasBreakpoint = false;
				if (node.getElementsByTagName("breakpoints").getLength() == 1) {
					Element breakpointsNode = (Element) node.getElementsByTagName("breakpoints").item(0);
					for (int j=0; j<breakpointsNode.getElementsByTagName("offset").getLength(); j++) {
						Element breakpoint = (Element) breakpointsNode.getElementsByTagName("offset").item(j);
						int breakpointOffset = Integer.parseInt(breakpoint.getTextContent(), 10);
						// check valid
						if (breakpointOffset < 0 || breakpointOffset > duration) {
							// invalid
							// TODO: do something better than exiting

							System.exit(2);
						}
						addedSection.addBreakPoint(breakpointOffset);
						hasBreakpoint = true;
					}	
				}
				// if none have been set set one at the end
				if (!hasBreakpoint) {
					addedSection.addBreakPoint(duration);
				}
			}
		}
	}
	
	private void initServerSocket() {
		serverSocketManager = new ServerSocketManager(this, serverPort);
	}
	
	private void initSocket() {
		
		try {
			caspSocket = new CaspSocket(caspAddress, caspPort);
		} catch (IOException e) {

			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void initLayerGroups() {
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
	
	public void clearActions() {
		synchronized (lock1) {
			actionQueue.clear();
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
