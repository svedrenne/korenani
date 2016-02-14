package jp.osdn.korenani;

import java.util.Locale;

import jp.osdn.i18n.L10nComponent;
import jp.osdn.i18n.LocaleListener;
import static jp.osdn.i18n.I18n.gtxt;

class LocaleListenerImpl implements LocaleListener {

	final private L10nComponent l10nComp;

	LocaleListenerImpl(L10nComponent menu) {
		l10nComp = menu;
	}
	
	@Override
	public void onLocaleChanged(Locale locale) {
		String languageCode = gtxt("DETECTED_LANGUAGE");
		l10nComp.setL10nMessages(locale, languageCode);
	}
	
}
