JFLAGS = -g 
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	dars/GUIStarter.java \
	dars/Defaults.java \
	dars/proto/Node.java \
	dars/proto/dsdv/DsdvDialog.java \
	dars/proto/dsdv/RouteEntry.java \
	dars/proto/dsdv/Dsdv.java \
	dars/proto/aodv/RouteEntry.java \
	dars/proto/aodv/AodvDialog.java \
	dars/proto/aodv/WaitQueueEntry.java \
	dars/proto/aodv/Aodv.java \
	dars/proto/NodeFactory.java \
	dars/SimulationTimeKeeper.java \
	dars/gui/NodeAttributesArea.java \
	dars/gui/OpenBrowser.java \
	dars/gui/GUI.java \
	dars/gui/HTMLWindow.java \
	dars/gui/NodeControls.java \
	dars/gui/SimArea.java \
	dars/gui/ColorLegend.java \
	dars/gui/GNodeListener.java \
	dars/gui/DARSAppMenu.java \
	dars/gui/Animations.java \
	dars/gui/TextWindow.java \
	dars/gui/LogArea.java \
	dars/gui/SendNodeMessageDialog.java \
	dars/gui/XYTickPanel.java \
	dars/gui/ImageFactory.java \
	dars/gui/HelpWindow.java \
	dars/gui/GNode.java \
	dars/NodeInspector.java \
	dars/DARSMain.java \
	dars/replayer/Replayer.java \
	dars/Utilities.java \
	dars/NodeStore.java \
	dars/SimEngine.java \
	dars/logger/Logger.java \
	dars/logger/Parser.java \
	dars/InputConsumer.java \
	dars/event/DARSEvent.java \
	dars/OutputHandler.java \
	dars/NodeAttributes.java \
	dars/Message.java \
	dars/InputHandler.java \
	dars/OutputConsumer.java \
	dars/console/Console.java 

default: classes jar

classes: $(CLASSES:.java=.class)

jar: 
	find -iname *.class | xargs jar cfm DARSApp.jar manifest ../img/*.png *.txt

clean:
	find . -iname *.class | xargs rm
	rm -f ../bin/DARSApp.jar

