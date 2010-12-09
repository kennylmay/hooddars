/**
 * 
 */
package dars.gui;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import dars.Defaults;
import dars.Utilities;

/**
 * @author Mike
 * 
 */
public class LogArea extends javax.swing.JPanel {

  /**
   * 
   */

  private static final StringBuilder sb = new StringBuilder(
                                            Defaults.LOG_AREA_BUF_SIZE);

  private class Appender extends Thread {
    public void run() {
      LinkedList<String> lineList = new LinkedList<String>();

      while (true) {
        try {
          lineList.clear();
          sb.setLength(0);
          lineList.add(lines.take());
          synchronized (lines) {
            lines.drainTo(lineList);
          }

          // Build the buffer from the end to the beginning
          int bufSize = 0;
          int strLen = 0;
          String l;
          Iterator<String> di = lineList.descendingIterator();
          boolean shouldBreak = false;
          while (di.hasNext() && !shouldBreak) {
            l = di.next();
            strLen = l.length();

            // If there's no more room in the buffer, break out.
            if (bufSize + strLen > Defaults.LOG_AREA_BUF_SIZE) {
              shouldBreak = true;
            } else {
              sb.insert(0, l);
              bufSize += strLen;

            }
          }

          // Maintain the buffer
          clampBuffer(bufSize);

          // Append
          textArea.append(sb.toString());

          // Move the caret to chase the log as it grows downward
          textArea.setCaretPosition(textArea.getDocument().getLength());
        } catch (Exception e) {
          continue;
        }
      }
    }

  }

  private static final long           serialVersionUID = 1L;
  public static String                newline          = System
                                                           .getProperty("line.separator");

  /**
   * Debug flag to determine whether or not to show debug messages in the log.
   */
  private LinkedBlockingQueue<String> lines            = new LinkedBlockingQueue<String>();

  public void appendLog(String logType, String log, long quantum) {

    // Construct the log line
    String line = (Utilities.timeStamp() + " Q" + quantum + " : " + logType
        + " : " + log + "\n");
    synchronized (lines) {
      lines.add(line);
    }

  }

  void clampBuffer(int incomingBufSize) {
    Document doc = textArea.getDocument();
    // If the document is > buf size, trunc it down to 50 percent of buf size.
    int overLength = doc.getLength() + incomingBufSize
        - Defaults.LOG_AREA_BUF_SIZE;

    if (overLength > 0) {
      try {
        // Chomp off from 0 to the end of the line where offset
        // LOG_AREA_BUF_SIZE/2 falls.
        textArea.replaceRange("", 0, textArea.getLineEndOffset(textArea
            .getLineOfOffset(Math.min(doc.getLength(), doc.getLength()
                - doc.getLength() / 2 + incomingBufSize))));
      } catch (BadLocationException e) {
        Utilities
            .showError("An error occurred while truncating the buffer in the LogArea. Please file a bug report.");
        System.exit(1);
      }
    }
  }

  private JScrollPane jsp;
  private JTextArea   textArea = new JTextArea();

  public LogArea() {

    setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), "Console", TitledBorder.CENTER,
        TitledBorder.TOP, Defaults.BOLDFACED_FONT));

    // Setup the text area.
    configureTextArea(textArea);

    // Pack the text area into a scroll pane
    jsp = new JScrollPane(textArea);
    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    setLayout(new BorderLayout());

    add(jsp, BorderLayout.CENTER);

  }

  private void configureTextArea(JTextArea ta) {
    // not editable
    ta.setEditable(false);

    Appender app = new Appender();
    app.start();
  }

  public void clear() {
    textArea.setText("");
  }

}
