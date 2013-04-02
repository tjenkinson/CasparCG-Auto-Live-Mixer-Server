@echo off
echo "The server should have started now. There are no error messages so if there was a problem it will have terminated."
echo "Now run the Demo.java from the caspar_auto_live_mixer_demo package."
echo "To stop the server you will need to kill it from the task manager. I haven't got round to coming up with a proper method yet."
CasparLiveAutoMixerServer.jar 127.0.0.1 5250 1 5 6 7 8 5150 "..\src\tjenkinson\caspar_auto_live_mixer_demo\demo.xml"
pause