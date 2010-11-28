JFLAGS = -g 
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	dars/error/InputError.java \
	dars/OutputConsumer.java \
	dars/proto/aodv/Aodv.java \
	dars/proto/aodv/AodvDialog.java \
	dars/proto/aodv/WaitQueueEntry.java \
	dars/proto/aodv/RouteEntry.java \
	dars/proto/Node.java \
	dars/proto/NodeFactory.java \
	dars/proto/dsdv/Dsdv.java \
	dars/proto/dsdv/DsdvDialog.java \
	dars/proto/dsdv/RouteEntry.java \
	dars/OutputHandler.java \
	dars/SimulationTimeKeeper.java \
	dars/Message.java \
	dars/DARSMain.java \
	dars/gui/GUI.java \
	dars/gui/Animations.java \
	dars/gui/DARSAppMenu.java \
	dars/gui/SimArea.java \
	dars/gui/GNode.java \
	dars/gui/LogArea.java \
	dars/gui/XYTickPanel.java \
	dars/gui/HelpWindow.java \
	dars/gui/GNodeListener.java \
	dars/gui/ImageFactory.java \
	dars/gui/NodeAttributesArea.java \
	dars/gui/SendNodeMessageDialog.java \
	dars/gui/GetNodeDialog.java \
	dars/Defaults.java \
	dars/event/DARSEvent.java \
	dars/SimEngine.java \
	dars/NodeStore.java \
	dars/replayer/Replayer.java \
	dars/InputConsumer.java \
	dars/InputHandler.java \
	dars/GUIStarter.java \
	dars/console/Console.java \
	dars/logger/Logger.java \
	dars/logger/Parser.java \
	dars/Utilities.java \
	dars/NodeAttributes.java \
	dars/NodeInspector.java \

default: classes jar

classes: $(CLASSES:.java=.class)

jar: 
	find -iname *.class | xargs jar cfm DARSApp.jar manifest ../img/*.png *.txt

clean:
	find . -iname *.class | xargs rm
	rm -f ../bin/DARSApp.jar

