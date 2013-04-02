package tjenkinson.caspar_auto_live_mixer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import tjenkinson.caspar_serverconnection.CaspSocket;
import tjenkinson.caspar_serverconnection.commands.CaspReturn;
import tjenkinson.caspar_serverconnection.commands.Info;

public class AudioTimer {
	
	private CaspSocket caspSocket = null;
	private AudioTimerTimer timerTask = null;
	private ScheduledFuture<?> timerHandle = null;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private int caspChannel = -1;
	private int caspLayer = -1;
	
	private int refreshInterval = 15000; // mseconds between each resync. it always tries again a few secs before end
	
	private long startTime = -1;
	
	// so that the program can determine the best times to try and sync the timer
	private long endTime = -1;
	
	private Object lock1 = new Object(); // Synchronisation lock
	
	private boolean timerCancelled = false;
	
	public AudioTimer(CaspSocket caspSocket, int caspChannel, int caspLayer, long startTime, long playDuration) throws IOException {
		this.caspSocket = caspSocket;
		this.startTime = startTime;
		this.endTime = startTime + playDuration;
		this.caspChannel = caspChannel;
		this.caspLayer = caspLayer;
		
		timerTask = new AudioTimerTimer();
		
	}
	
	public void start() throws IOException {
		timerTask.refresh(startTime); // initialise and set timer start point (because caspar gets it wrong when paused)
		  								// this also kicks of the java timer.
	}
	
	// stop timer makes sure garbage collector can do it's stuff
	public void unload() {
		synchronized(lock1) {
			timerCancelled = true;
			if (timerHandle != null) {
				timerHandle.cancel(false);
			}
			scheduler.shutdown();
		}
	}
	
	public long getTime() {
		return timerTask.getTime();
	}
	
	public void refresh(long timeOverride) throws IOException {
		timerTask.refresh(timeOverride);
	}
	
	private class AudioTimerTimer implements Runnable {
		
		private int fps = -1;
		// offset from current time in milliseconds
		private long timeOffset = -1;
		//time offset will always be >= to this
		private long actualTimeOffset = -1;
		// time is overrided before starts playing as caspar only starts frame count when playing
		private long timeOverride = -1;
		//stores the actual value when time is overrided to determine when changed
		private long timeOverrideRemembered = -1;
		
		
		@Override
		public void run() {
			try {
				refresh(-1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public synchronized long getTime() {
			if (actualTimeOffset >= timeOffset) {
				timeOffset = actualTimeOffset;
			}
			if (timeOverride != -1) {
				return timeOverride;
			}
			return timeOffset == -1 ? 0 : new Date().getTime()-timeOffset;
		}
		
		public synchronized void refresh(long passedTimeOverride) throws IOException {
			
			if (passedTimeOverride != -1) {
				this.timeOverride = passedTimeOverride;
			}
			
			//TODO: un hard code channel and layer
			int currentFrame = -1;
			CaspReturn response = caspSocket.runCmd(new Info(caspChannel+"-"+caspLayer+" F"));
			
			
			// GET CURRENT FRAME FROM XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		    DocumentBuilder builder=null; 
		    Document document = null;

	        try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			}  
	        try {
				document = builder.parse(new InputSource(new StringReader(response.getResponse())));
			} catch (SAXException e) {
				e.printStackTrace();
			} 
	        
	        XPathExpression expr;
	        Object hits = null;
			try {
				expr = XPathFactory.newInstance().newXPath().compile("/producer/file-frame-number");
				hits = expr.evaluate(document, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (hits instanceof NodeList) {
				NodeList list = (NodeList) hits;
				if (list.getLength() > 0) {
					currentFrame = Integer.parseInt(list.item(0).getTextContent(), 10);
				}
			}
			
			
			// GET FRAME RATE FROM XML
			if (fps == -1) {
					
				try {
					expr = XPathFactory.newInstance().newXPath().compile("/producer/fps");
					hits = expr.evaluate(document, XPathConstants.NODESET);
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (hits instanceof NodeList) {
					NodeList list = (NodeList) hits;
					if (list.getLength() > 0) {
						fps = Integer.parseInt(list.item(0).getTextContent(), 10);
					}
				}
			}
			
			long actualTime = 0;
			if (currentFrame != -1) {
				// convert frame to time
				actualTime = (long)(currentFrame * (1.0/fps) * 1000) + (new Date().getTime() - response.getRequestTime());
			//	float predictedTime = getTime();
				
				// recalculate time offset to make accurate again.
				long tempTimeOffset = new Date().getTime()- actualTime;
				// update if more than a second out
				if (Math.abs(tempTimeOffset - actualTimeOffset) > 500) {
					actualTimeOffset = tempTimeOffset;
				}
				
			}
			
			if (passedTimeOverride != -1) {
				timeOverrideRemembered = currentFrame;
			}
			
			
			if (timeOverride != -1 && timeOverrideRemembered != currentFrame) {
				timeOverride = -1;
			}
			
			synchronized(lock1) {
				if (!timerCancelled) {
				//	timer.cancel();
				//	timer.purge();
				//	timer = new Timer(false);
					// the music is not playing yet
					if (timeOverride != -1) {
						// run this again in 1s to see if started
						// it should be starting really soon because the first time refresh is called is after the play signal has been sent
						timerHandle = scheduler.schedule(timerTask, 1000, java.util.concurrent.TimeUnit.MILLISECONDS);
					}
					else {
						// music is playing now
						long remainingTime = endTime - getTime();
						long nextRun = getTime() + refreshInterval;
						
						if (nextRun < endTime - 6000) {
							timerHandle = scheduler.schedule(timerTask, nextRun, java.util.concurrent.TimeUnit.MILLISECONDS);
						}
						else if (remainingTime > 5000) {
							// run 4 seconds before end
							timerHandle = scheduler.schedule(timerTask, remainingTime-4000, java.util.concurrent.TimeUnit.MILLISECONDS);
						}
						else {
							// don't set another timer as the music should have ended/right near end anyway
						}
					}
				}
			}
		}
		
	}
}
