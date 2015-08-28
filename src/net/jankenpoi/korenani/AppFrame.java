package net.jankenpoi.korenani;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.polarcloud.rikaichan.RcxData;
import com.polarcloud.rikaichan.RcxData.DictionaryEntry;

public class AppFrame extends JFrame {
	JTextPane textPane;
	AbstractDocument doc;
	static final int MAX_CHARACTERS = 300;
	JTextArea translationsArea;
	String newline = "\n";
	HashMap<Object, Action> actions;

	private JSplitPane splitPane;
	private JScrollPane scrollPaneForTranslations;

	MyWindowFocusListener myWindowFocusListener = new MyWindowFocusListener();
	
	public AppFrame() {
		super("Korenani 0.1");

		if (null == AppConfig.INSTANCE.getDictionary()) {
			throw new RuntimeException("Dictionary not found!");
		}
		
		textPane = new JTextPane();
		textPane.setCaretPosition(0);
		textPane.setMargin(new Insets(5,5,5,5));
		StyledDocument styledDoc = textPane.getStyledDocument();
		if (styledDoc instanceof AbstractDocument) {
			doc = (AbstractDocument)styledDoc;
			//            doc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
		} else {
			System.err.println("Text pane's document isn't an AbstractDocument!");
			System.exit(-1);
		}
		JScrollPane scrollPane = new JScrollPane(textPane);
		//        scrollPane.setPreferredSize(new Dimension(200, 200));

		translationsArea = new JTextArea(10, 100);
		translationsArea.setEditable(false);
		translationsArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		scrollPaneForTranslations = new JScrollPane(translationsArea);

		splitPane = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				scrollPane, scrollPaneForTranslations);
		//        splitPane.setOneTouchExpandable(true);

		JPanel statusPane = new JPanel(new GridLayout(1, 1));
		CaretListenerLabel caretListenerLabel =
				new CaretListenerLabel(".");
		caretListenerLabel.displaySelectionInfo(0, 0);
		statusPane.add(caretListenerLabel);

		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(statusPane, BorderLayout.PAGE_END);

		actions = createActionTable(textPane);
		JMenu editMenu = createEditMenu();
		JMenuBar mb = new JMenuBar();
		mb.add(editMenu);
		setJMenuBar(mb);

		addBindings();

		initDocument();
		textPane.setCaretPosition(0);

		textPane.addCaretListener(caretListenerLabel);
		doc.addDocumentListener(new MyDocumentListener());

		setPreferredSize(new Dimension(getToolkit().getScreenSize().width, getToolkit().getScreenSize().height/2));
		try {
			RcxData.deinflectInit();
		} catch (IOException e) {
			//TODO: log error
			e.printStackTrace();
		}
		addWindowFocusListener(myWindowFocusListener);
	}

	//TODO: extract this path to make it customizable
//	private static String pathToDictionary = ":resource:jar:file:korenani-dict-en-2-01-140601.jar!/dictionaries/rikaichan-en-2-01-140601/dict.sqlite";
//	private static String pathToDictionary = ":resource:jar:file:default_jp_2_en_dict.jar!/rikaichan-en-2-01-140601/dict.sqlite";

	protected class CaretListenerLabel extends JLabel
	implements CaretListener {
		public CaretListenerLabel(String label) {
			super(label);
		}

		//Might not be invoked from the event dispatch thread.
		public void caretUpdate(CaretEvent e) {
			displaySelectionInfo(e.getDot(), e.getMark());
		}

		private void displaySelectionInfo(final int dot,
				final int mark) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					int start = 0;
					int length = doc.getLength();

					if (dot == mark) {  // no selection
						try {
							Rectangle caretCoords = textPane.modelToView(dot);
							//Convert it to view coordinates.
							start = dot;
							length = Math.min(15,  doc.getLength() - dot);
						} catch (BadLocationException ble) {
//							setText("caret: text position: " + dot + newline);
						}
					} else if (dot < mark) {
//						setText("selection from: " + dot + " to " + mark + newline);

						start = dot;
						length = mark - dot;
					} else {
//						setText("selection from: " + mark + " to " + dot + newline);

						start = mark;
						length = dot - mark;

					}

					System.out.println("LET'S TRY...");
					String word = null;
					try {
						word = doc.getText(start, length);
					} catch (Exception e) {
						System.out.println("caught: "+e);
					}
					System.out.println("  word = "+word);
					//                      String word = text.substring(i, Math.min(i + 15, text.length()));
					RcxData.DictionaryEntry[] dicoEntries = new RcxData.DictionaryEntry[0];
					RcxData.KanjiEntry kanjiEntry = new RcxData.KanjiEntry("-", "-", "-", "-", "-");
					try {
						if (word.length() > 0) {
							kanjiEntry = RcxData.kanjiSearch(word.substring(0, 1));
						}
						System.out.println("kanjiSearch result : " + kanjiEntry);
						dicoEntries = RcxData._wordSearch(word, AppConfig.INSTANCE.getDictionary(), 10);
						//							System.out.println("=> found "+dicoEntries.length+" dictionary entries");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					StringBuilder translationsToShow = new StringBuilder();
					for (int j = 0; j < dicoEntries.length; j++) {
						//							System.out.println("Korenani.main() dicoEntry[" + j + "] = " + dicoEntries[j]);
						DictionaryEntry entry = dicoEntries[j];
						if (!entry.word().equals("")) {
							translationsToShow.append(entry.word()).append("  ");
						}
						translationsToShow.append(entry.reading()).append("  ");
						translationsToShow.append(entry.gloss()).append("  ");
						translationsToShow.append('\n');
					}                            
					translationsArea.setText(translationsToShow.toString());
					translationsArea.setCaretPosition(0);
				}
			});
		}
	}

	protected class MyDocumentListener
	implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			displayEditInfo(e);
		}
		public void removeUpdate(DocumentEvent e) {
			displayEditInfo(e);
		}
		public void changedUpdate(DocumentEvent e) {
			displayEditInfo(e);
		}
		private void displayEditInfo(DocumentEvent e) {
			Document document = e.getDocument();
			int changeLength = e.getLength();
			translationsArea.append(e.getType().toString() + ": " +
					changeLength + " character" +
					((changeLength == 1) ? ". " : "s. ") +
					" Text length = " + document.getLength() +
					"." + newline);
		}
	}

	//Add a couple of emacs key bindings for navigation.
	protected void addBindings() {
		InputMap inputMap = textPane.getInputMap();

		//Ctrl-b to go backward one character
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.backwardAction);

		//Ctrl-f to go forward one character
		key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.forwardAction);

		//Ctrl-p to go up one line
		key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.upAction);

		//Ctrl-n to go down one line
		key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.downAction);
	}

	protected JMenu createEditMenu() {
		JMenu menu = new JMenu("Edit");

		menu.addSeparator();

		//These actions come from the default editor kit.
		//Get the ones we want and stick them in the menu.
		menu.add(getActionByName(DefaultEditorKit.cutAction));
		menu.add(getActionByName(DefaultEditorKit.copyAction));
		menu.add(getActionByName(DefaultEditorKit.pasteAction));

		menu.addSeparator();

		menu.add(getActionByName(DefaultEditorKit.selectAllAction));
		return menu;
	}

	protected void initDocument() {
		String initString[] = 
//			{ "別に、問題ないんです。又ね。" };
			{ getClipboardText() };

		SimpleAttributeSet[] attrs = initAttributes(initString.length);

		try {
			for (int i = 0; i < initString.length; i ++) {
				doc.insertString(/*doc.getLength()*/0, initString[i] + newline,
						attrs[i]);
			}

		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	protected SimpleAttributeSet[] initAttributes(int length) {
		//Hard-code some attributes.
		SimpleAttributeSet[] attrs = new SimpleAttributeSet[length];

		attrs[0] = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attrs[0], Font.SANS_SERIF);
//		StyleConstants.setFontSize(attrs[0], 20); // font size is overridden when clipboard text is transfered

		//        attrs[1] = new SimpleAttributeSet(attrs[0]);
		//        StyleConstants.setBold(attrs[1], true);
		//
		//        attrs[2] = new SimpleAttributeSet(attrs[0]);
		//        StyleConstants.setItalic(attrs[2], true);
		//
		//        attrs[3] = new SimpleAttributeSet(attrs[0]);
		//        StyleConstants.setFontSize(attrs[3], 20);
		//
		//        attrs[4] = new SimpleAttributeSet(attrs[0]);
		//        StyleConstants.setFontSize(attrs[4], 12);
		//
		//        attrs[5] = new SimpleAttributeSet(attrs[0]);
		//        StyleConstants.setForeground(attrs[5], Color.red);

		return attrs;
	}

	//The following two methods allow us to find an
	//action provided by the editor kit by its name.
	private HashMap<Object, Action> createActionTable(JTextComponent textComponent) {
		HashMap<Object, Action> actions = new HashMap<Object, Action>();
		Action[] actionsArray = textComponent.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
		return actions;
	}

	private Action getActionByName(String name) {
		return actions.get(name);
	}

	public void positionSplitPane() {
		splitPane.setDividerLocation(0.33);
	}

	public static String getClipboardText() {
		String clipboardText;
		Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);
		try {
			if (trans != null
					&& trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				clipboardText = (String) trans
						.getTransferData(DataFlavor.stringFlavor);
				System.out.println(clipboardText);
				return clipboardText;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "(no text found in clipboard)";
	}
	
	private class MyWindowFocusListener implements WindowFocusListener {

		@Override
		public void windowGainedFocus(WindowEvent e) {
			String clipString = getClipboardText();
			try {
				doc.remove(0, doc.getLength());
				doc.insertString(0, clipString + newline, null);
				textPane.setCaretPosition(0);
				textPane.requestFocusInWindow();
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void windowLostFocus(WindowEvent e) {
		}
		
	}
}
