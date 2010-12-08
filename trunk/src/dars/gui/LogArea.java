/**
 * 
 */
package dars.gui;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.Queue;
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
  private Object signal = new Object();

  private class Appender extends Thread {
    public void run() {
      LinkedList<String> lineList = new LinkedList<String>();
      StringBuilder sb = new StringBuilder(10000);
      while (true) {

        try {
          lineList.clear();
          sb.setLength(0);
          lineList.add(lines.take());
        } catch (InterruptedException e) {
          continue;
        }
        String line;
        while ((line = lines.poll()) != null)
          lineList.add(line);

        for (String l : lineList) {
          sb.append(l);
        }

        line = sb.toString();

        // Append
        textArea.append(line);

        // Maintain the buffer
        clampBuffer();

        // Move the caret to chase the log as it grows downward
        textArea.setCaretPosition(textArea.getDocument().getLength());
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
        + " : " + log + newline);
    lines.add(line);

  }

  void clampBuffer() {
    Document doc = textArea.getDocument();
    // If the document is > buf size, trunc it down to 50 percent of buf size.
    int overLength = doc.getLength() - Defaults.LOG_AREA_BUF_SIZE;

    if (overLength > 0) {
      try {
        // Chomp off from 0 to the end of the line where offset
        // LOG_AREA_BUF_SIZE/2 falls.
        doc.remove(0, textArea.getLineEndOffset(textArea
            .getLineOfOffset(Defaults.LOG_AREA_BUF_SIZE / 2)));
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

    setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Console", TitledBorder.CENTER,
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

  // Add a text area

}
