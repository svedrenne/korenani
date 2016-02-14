package jp.osdn.korenani;

import static jp.osdn.i18n.I18n.gtxt;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import jp.osdn.i18n.I18n;
import jp.osdn.i18n.LocaleListener;
import jp.osdn.i18n.L10nComponent;

@SuppressWarnings("serial")
public class FileMenu extends JMenu implements L10nComponent {

	private final JMenuItem itemQuit = new JMenuItem();
	private final Action actionQuit = new QuitAction();
	
	private final LocaleListener localeListener;
//	@Override
	public void setL10nMessages(Locale locale, String languageCode) {
		setText(gtxt("File"));

		itemQuit.setText(gtxt("Quit"));
		actionQuit.putValue(Action.SMALL_ICON, StockIcons.ICON_QUIT);
		actionQuit
				.putValue(Action.SHORT_DESCRIPTION, gtxt("Quit the application"));
		actionQuit.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_Q));
	}

	FileMenu(JFrame parent, ActionsRepository actions) {
		super();
		setMnemonic(KeyEvent.VK_F);
		getAccessibleContext().setAccessibleDescription("File menu");

		addItems();
		setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
		localeListener = new LocaleListenerImpl(this);
		I18n.addLocaleListener(localeListener);
	}

	private void addItems() {
	
//		addSeparator();

		itemQuit.setText("Quit");
		itemQuit.setAction(actionQuit);
		itemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		add(itemQuit);
	}
}
