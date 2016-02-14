package jp.osdn.korenani;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
	
	public static final AppConfig INSTANCE = new AppConfig();
	
	/*
	 * Custom location where to search the dictionary in a stand-alone SQLITE file
	 */
	private static final String CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE = System.getProperty("korenani.dictionary.database.custom.direct.path", null);
	
	/*
	 * Custom location where to search the dictionary SQLITE file inside a JAR
	 */
	private static final String CUSTOM_PATH_TO_DICTIONARY_JAR = System.getProperty("korenani.dictionary.jar.custom.path", null);

	/*
	 * Default location where to search the dictionary in a stand-alone SQLITE file
	 */
	private static final String DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE = "dictionaries/dict.sqlite";

	/*
	 * Default location where to search the dictionary SQLITE file inside a JAR
	 */
	private static final String DEFAULT_PATH_TO_DICTIONARY_JAR = "dictionaries/default_jp_2_en_dict.jar";

	/*
	 * Default location where to search the ...
	 */
	private static final String DEFAULT_PATH_TO_RIKAICHAN_JAR = "depends/rikaichan.jar";

	/*
	 * This variable derives from 'DEFAULT_PATH_TO_RIKAICHAN_JAR'
	 */
	private static final String DEFAULT_PATH_TO_RIKAICHAN_RESOURCES; // DO NOT EDIT

	/*
	 * This variable derives from 'CUSTOM_PATH_TO_DICTIONARY_JAR'
	 */
	private static final String CUSTOM_PATH_TO_DICTIONARY_DATABASE; // DO NOT EDIT
	
	/*
	 * This variable derives from 'DEFAULT_PATH_TO_DICTIONARY_JAR'
	 */
	private static final String DEFAULT_PATH_TO_DICTIONARY_DATABASE; // DO NOT EDIT
	static {
		if (isPathExists(DEFAULT_PATH_TO_DICTIONARY_JAR)) {
			DEFAULT_PATH_TO_DICTIONARY_DATABASE = ":resource:jar:file:"+DEFAULT_PATH_TO_DICTIONARY_JAR+"!/dict.sqlite";
		} else {
			DEFAULT_PATH_TO_DICTIONARY_DATABASE = null;
		}
		CUSTOM_PATH_TO_DICTIONARY_DATABASE = !isPathExists(CUSTOM_PATH_TO_DICTIONARY_JAR)?null:":resource:jar:file:"+CUSTOM_PATH_TO_DICTIONARY_JAR+"!/dict.sqlite";
		
		System.out.println("AppConfig (static block) - CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE == "+CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE+(isPathExists(CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE)?" (INFO: path exists)":" (INFO: PATH NOT FOUND)"));
		System.out.println("AppConfig (static block) - CUSTOM_PATH_TO_DICTIONARY_JAR == "+CUSTOM_PATH_TO_DICTIONARY_JAR+(isPathExists(CUSTOM_PATH_TO_DICTIONARY_JAR)?" (INFO: path exists)":" (INFO: PATH NOT FOUND)"));
		System.out.println("AppConfig (static block) - CUSTOM_PATH_TO_DICTIONARY_DATABASE == "+(isPathExists(CUSTOM_PATH_TO_DICTIONARY_JAR)?CUSTOM_PATH_TO_DICTIONARY_DATABASE:" (INFO: JAR CONTAINER NOT FOUND)"));
		System.out.println("AppConfig (static block) - DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE == "+DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE+(isPathExists(DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE)?" (INFO: path exists)":" (INFO: PATH NOT FOUND)"));
		System.out.println("AppConfig (static block) - DEFAULT_PATH_TO_DICTIONARY_JAR == "+DEFAULT_PATH_TO_DICTIONARY_JAR+(isPathExists(DEFAULT_PATH_TO_DICTIONARY_JAR)?" (INFO: path exists)":" (INFO: PATH NOT FOUND)"));
		System.out.println("AppConfig (static block) - DEFAULT_PATH_TO_DICTIONARY_DATABASE == "+(isPathExists(DEFAULT_PATH_TO_DICTIONARY_JAR)?DEFAULT_PATH_TO_DICTIONARY_DATABASE:" (INFO: JAR CONTAINER NOT FOUND)"));
		
		if (isPathExists(DEFAULT_PATH_TO_RIKAICHAN_JAR)) {
			DEFAULT_PATH_TO_RIKAICHAN_RESOURCES = ":resource:jar:file:"+DEFAULT_PATH_TO_RIKAICHAN_JAR+"!/resources/com/polarcloud/rikaichan/chrome/";
		} else {
			DEFAULT_PATH_TO_RIKAICHAN_RESOURCES = null;
		}
		System.out.println("AppConfig (static block) - DEFAULT_PATH_TO_RIKAICHAN_JAR == "+DEFAULT_PATH_TO_RIKAICHAN_JAR+(isPathExists(DEFAULT_PATH_TO_RIKAICHAN_JAR)?" (INFO: path exists)":" (INFO: PATH NOT FOUND)"));
		System.out.println("AppConfig (static block) - DEFAULT_PATH_TO_RIKAICHAN_RESOURCES == "+(isPathExists(DEFAULT_PATH_TO_RIKAICHAN_JAR)?DEFAULT_PATH_TO_RIKAICHAN_RESOURCES:" (INFO: JAR CONTAINER NOT FOUND)"));
	}
	
	public static String getDictionary() {
		if (CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE != null && isPathExists(CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE)) {
			return CUSTOM_DIRECT_PATH_TO_DICTIONARY_DATABASE;
		}
		if (CUSTOM_PATH_TO_DICTIONARY_DATABASE != null) {
			/* check if JAR exists - the DB is inside the JAR */
			if (isPathExists(CUSTOM_PATH_TO_DICTIONARY_JAR)) {
				return CUSTOM_PATH_TO_DICTIONARY_DATABASE;
			}
		}
		if (DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE != null && isPathExists(DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE)) {
			return DEFAULT_DIRECT_PATH_TO_DICTIONARY_DATABASE;
		}
		if (DEFAULT_PATH_TO_DICTIONARY_DATABASE != null) {
			/* check if JAR exists - the DB is inside the JAR */
			if (isPathExists(DEFAULT_PATH_TO_DICTIONARY_JAR)) {
				return DEFAULT_PATH_TO_DICTIONARY_DATABASE;
			}
			return DEFAULT_PATH_TO_DICTIONARY_DATABASE;
		}
		return null;
	}

	private static boolean isPathExists(String pathString) {
		if (pathString == null) {
			return false;
		}
		Path path = Paths.get(pathString);
		if (Files.exists(path)) {
			return true;
		}
		return false;
	}

}
