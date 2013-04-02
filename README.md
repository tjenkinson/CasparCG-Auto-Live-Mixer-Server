CasparCG-Auto-Live-Mixer-Server
===============================

A server written in java that reads an xml file containing the in, out and breakpoints of audio/video and then receives commands to flow smoothly between the different sections.

THIS IS IN BETA. There are still many things that need fixing and tidying up but I've tested it and it seems to work alright with the demo I created.

I haven't really got time to write any proper documentation at the moment but you should be able to figure it out from the demo.

Basically the Auto Live Mixer server receives commands from you and then sends the necessary commands to Caspar at the correct times.

The command structure is:

To queue another section which will start playing after the playing section reaches a break point:
ADD [section no (starting from 0 in the same order as the xml):int] [minimum no of plays:int] [keep looping until recieve next command]:0|1]

To clear any sections in the queue:
REMOVE

The path to the xml file that contains the information about the various sections is passed in to the CasparLiveAutoMixerServer.jar as a parameter (along with other paramaters which are listed in the demo file).
