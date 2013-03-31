package tjenkinson.caspar_audio_looper;

import java.io.IOException;

import tjenkinson.caspar_serverconnection.CaspSocket;
import tjenkinson.caspar_serverconnection.commands.Clear;
import tjenkinson.caspar_serverconnection.commands.LoadBg;
import tjenkinson.caspar_serverconnection.commands.Play;
import tjenkinson.caspar_serverconnection.commands.Stop;

public class LayerGroup {

		private CaspSocket caspSocket = null;
	
		private int caspChan = -1;
		private int[] caspLayers = {-1, -1};
		
		private Section section = null;
		
		private int liveLayer = -1;
		private boolean firstPlay = false;
		
		private AudioTimer audioTimer = null;
		
		private boolean ready = false;
	
		public LayerGroup(CaspSocket caspSocket, int caspChan, int caspLayer0, int caspLayer1) {
			this.caspSocket = caspSocket;
			this.caspChan = caspChan;
			caspLayers[0] = caspLayer0;
			caspLayers[1] = caspLayer1;
		}
		
		public boolean ready() {
			return ready;
		}
		
		public synchronized void play() throws IOException {
			if (!ready) {
				load(section); // reload first
			}
			int oldLiveLayer = liveLayer;
			if (liveLayer == 1 || liveLayer == -1) {
				liveLayer = 0;
			}
			else {
				liveLayer = 1;
			}
			// initialise the timer
			unloadAudioTimer();
			audioTimer = new AudioTimer(caspSocket, caspChan, caspLayers[liveLayer], this.section.getStartTime(firstPlay), this.section.getDuration(firstPlay));// initialise the timer
			if (oldLiveLayer != -1) {
				stopLayer(oldLiveLayer);
				loadLayer(oldLiveLayer, false); // reload this stopped layer
			}
			caspSocket.runCmd(new Play(caspChan+"-"+caspLayers[liveLayer]));
			audioTimer.start();
			firstPlay = false;
			if (oldLiveLayer != -1) {
				stopLayer(oldLiveLayer);
				loadLayer(oldLiveLayer, false); // reload this stopped layer
			}
		}
		
		private synchronized void loadLayer(int layerNo, boolean firstPlay) throws IOException {
			// TODO: get actual frame rate.
			// the music should never reach LENGTH before it is stopped. LENGTH is just so that it doesn't buffer more than required.
			caspSocket.runCmd(new LoadBg(caspChan+"-"+caspLayers[layerNo]+" \""+section.getFile()+"\" SEEK "+Calculations.timeToFrame(this.section.getStartTime(firstPlay), 25)+" LENGTH "+Calculations.timeToFrame(this.section.getDuration(firstPlay)+4000, 25)));
		}
		
		private synchronized void stopLayer(int layerNo) throws IOException {
			caspSocket.runCmd(new Stop(caspChan+"-"+caspLayers[layerNo]));
			caspSocket.runCmd(new Clear(caspChan+"-"+caspLayers[layerNo]));
		}
		
		public synchronized void stop() throws IOException {
			if (!ready) {
				return;
			}
			ready = false;
			stopLayer(0);
			stopLayer(1);
			unloadAudioTimer();
			liveLayer = -1;
		}
		
		private synchronized void unloadAudioTimer() {
			if (audioTimer != null) {
				audioTimer.unload();
				audioTimer = null;
			}
		}
		
		public long getTime() {
			if (audioTimer == null) {
				return -1;
			}
			return audioTimer.getTime();
		}
		
		public synchronized void load(Section section) throws IOException {
			stop();
			this.section = section;
			// load the file into both layers
			loadLayer(0, true);
			loadLayer(1, false);
			firstPlay = true;
			ready = true;
		}
}
