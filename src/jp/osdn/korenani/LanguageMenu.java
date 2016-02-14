package jp.osdn.korenani;

import static jp.osdn.i18n.I18n.gtxt;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import jp.osdn.i18n.I18n;
import jp.osdn.i18n.LocaleListener;
import jp.osdn.i18n.L10nComponent;

@SuppressWarnings("serial")
public class LanguageMenu extends JMenu implements L10nComponent {

        private HashMap<String, JRadioButtonMenuItem> itemsMap = new HashMap<String, JRadioButtonMenuItem>();

        private String langCode;
        
        public LanguageMenu() {
                addItems();
                setIcon(languageIcon(gtxt("DETECTED_LANGUAGE")));
                
                addMenuListener(new MenuListener() {
                        
                        @Override
                        public void menuSelected(MenuEvent arg0) {
                                final String detectedLanguage = gtxt("DETECTED_LANGUAGE");
                                langCode = detectedLanguage;
                        }
                        
                        @Override
                        public void menuDeselected(MenuEvent arg0) {
                                I18n.reset(langCode);
                        }
                        
						@Override
                        public void menuCanceled(MenuEvent arg0) {
                                I18n.reset(langCode);
                        }
                });
                
                final String detectedLanguage = gtxt("DETECTED_LANGUAGE");
                JRadioButtonMenuItem selectedItem = itemsMap.get(detectedLanguage);
                setText(gtxt("Language"));
                if (selectedItem != null) {
                        selectedItem.setSelected(true);
                }
                localeListener = new LocaleListenerImpl(this);
                I18n.addLocaleListener(localeListener);
        }
		        
        private void addItems() {
                ButtonGroup myGroup = new ButtonGroup();
                addItem("en", "English", myGroup);
                addItem("fr", "Fran\u00e7ais", myGroup);
                addItem("ja", "\u65e5\u672c\u8a9e", myGroup);
        }

        private void addItem(final String code, String language, ButtonGroup group) {
                JRadioButtonMenuItem radioItem;

                radioItem = new JRadioButtonMenuItem(language);
                itemsMap.put(code, radioItem);
                radioItem.setAction(new AbstractAction(language, languageIcon(code)) {

                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                                I18n.reset(code);
                        }
                });

                radioItem.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseEntered(MouseEvent e) {
                                I18n.reset(code);
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                                I18n.reset(langCode);
                        }

                });
                group.add(radioItem);
                add(radioItem);
        }

        private final LocaleListener localeListener;
		@Override
		public void setL10nMessages(Locale locale, String languageCode) {
			setText(gtxt("Language"));
			setIcon(languageIcon(languageCode));

		JRadioButtonMenuItem selectedItem = itemsMap.get(languageCode);
		if (selectedItem != null) {
			selectedItem.setSelected(true);
		}								
			
			if (this.isSelected()) {
				return;
			}
		}

		public static Icon languageIcon(final String langCode) {
			if ("en".equals(langCode)) {
				return StockIcons.ICON_FLAG_EN;
			} else if ("fr".equals(langCode)) {
				return StockIcons.ICON_FLAG_FR;
			} else if ("ja".equals(langCode)) {
				return StockIcons.ICON_FLAG_JA;
			}
			return StockIcons.ICON_GO_HOME;
		}
		
}
