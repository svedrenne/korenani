package jp.osdn.korenani;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import static jp.osdn.i18n.I18n.gtxt;

import jp.osdn.i18n.I18n;
import jp.osdn.i18n.L10nComponent;
import jp.osdn.i18n.LocaleListener;

@SuppressWarnings("serial")
public class EditMenu extends JMenu implements L10nComponent {

        private final LocaleListener localeListener;
        
        @Override
        public void setL10nMessages(Locale locale, String languageCode) {
                setText(gtxt("Edit"));
        }

        EditMenu(ActionsRepository actions, JFrame parent) {
                setMnemonic(KeyEvent.VK_E);
                getAccessibleContext().setAccessibleDescription(
                                "Edit menu");
                
                addItems(actions, parent);
                
                setL10nMessages(null, gtxt("DETECTED_LANGUAGE"));
                localeListener = new LocaleListenerImpl(this);
                I18n.addLocaleListener(localeListener);
        }

        private void addItems(ActionsRepository actions, JFrame parent) {
                
                add(new LanguageMenu());
        }

}
