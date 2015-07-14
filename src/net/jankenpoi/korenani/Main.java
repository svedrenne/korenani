/**
 * 
 */
package net.jankenpoi.korenani;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 */
public class Main {

	/**
	 * @param args (ignored)
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		PrintStream errorFile = new PrintStream(new FileOutputStream("korenani_err.txt"));
		errorFile.append("- Korenani -\n");
		System.setOut(errorFile);
		System.setErr(errorFile);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		final AppFrame frame = new AppFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setVisible(true);
		frame.positionSplitPane();
	}

}
