/**
 * Korenani is a Java program that helps you read and understand Japanese
 * text on your computer.
 * 
 * Korenani is Copyright (C) 2014 Sylvain Vedrenne
 * 
 * Home page: http://sourceforge.jp/projects/korenani/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * ---
 * 
 * Package 'com.polarcloud.rikaichan' contains code ported to Java
 * by Sylvain Vedrenne based upon JavaScript functions from Rikaichan
 * (version 2.07), while remaining as close as possible to the syntax
 * of the original JavaScript code from Rikaichan. This Java port
 * is licensed under the GPL v3+.
 * 
 * ---
 *  
 * Information about Rikaichan (version 2.07, licensed under the GPL v2+):
 *   Rikaichan is Copyright (C) 2005-2012 Jonathan Zarate
 *   Rikaichan home page: http://www.polarcloud.com/
 *  
 */
package com.polarcloud.rikaichan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Below are the contents of the JavaScript file 'data.js' (from Rikaichan 2.07)
 * that Sylvain Vedrenne is porting to Java for project Korenani.
 */

//var rcxData = {
//    ready: false,
//    kanjiPos: 0,
//    dicList: [],
public class RcxData {

    private static boolean DEBUG = false;
    
    static {
        // load the sqlite-JDBC driver using the current class loader
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//
//    loadConfig: function() {
//        let reinit = false;
//
//        if (this.ready) {
//            this.done();
//            reinit = true;
//        }
//
//        if (typeof(rcxDicList) == 'undefined') {
//            rcxDicList = {};
//            this.missing = true;
//        }
//        if (rcxDicList['kanji@local'] == null) {
//            rcxDicList['kanji@local'] = {
//                name: 'Kanji',
//                id: 'kanji@local',
//                isKanji: true
//            };
//        }
//
//        //  rcxMain.global().rcxDicList = rcxDicList;
//
//        let prefs = new rcxPrefs();
//        let order = prefs.getString('dpriority');
//        if (order == '') order = 'rikaichan-jpen@polarcloud.com#|rikaichan-jpde@polarcloud.com#|rikaichan-jpfr@polarcloud.com#|rikaichan-jpru@polarcloud.com#|rikaichan-jpnames@polarcloud.com#|kanji@local#';
//
//        this.dicList = [];
//        this.kanjiPos = 0;
//
//        let done = {};
//
//        // arrange dicList based on user setting
//        let oa = order.split('|');
//        for (let i = 0; i < oa.length; ++i) {
//            if (oa[i].match(/^(.+?)#/)) {
//                let dic = rcxDicList[RegExp.$1];
//                if (dic) {
//                    this.dicList.push(dic);
//                    done[dic.id] = true;
//                }
//            }
//        }
//
//        // anything new is added at the end
//        let addedNew = false;
//        for (let id in rcxDicList) {
//            if (!done[id]) {
//                this.dicList.push(rcxDicList[id]);
//                addedNew = true;
//            }
//        }
//
//        let ids = [];
//
//        // rebuild dpriority string which is also used by Options
//        let order2 = [];
//        for (let i = 0; i < this.dicList.length; ++i) {
//            let dic = this.dicList[i];
//            let s = dic.id + '#' + dic.name;
//            if (dic.version) s += ' v' + dic.version;
//            order2.push(s)
//
//            if (dic.isKanji) this.kanjiPos = i; // keep track of position
//                else ids.push(dic.id);
//        }
//        order2 = this.missing ? '' : order2.join('|');
//        if (order != order2) prefs.setString('dpriority', order2);
//
//        if (addedNew) {
//            // show dictionary tab if we have a new dictionary
//            window.openDialog('chrome://rikaichan/content/options.xul', '', 'chrome,centerscreen', 'dic');
//        }
//
//        // FF 3.7a workaround; @@ revisit later
//        if (!rcxData.dicPath) {
//            rcxData.dicPath = { ready: false };
//            try {
//                Components.utils.import('resource://gre/modules/AddonManager.jsm');
//                // ! asynchronous
//                AddonManager.getAddonsByIDs(ids, function(addons) {
//                    for (let i = 0; i < addons.length; ++i) {
//                        let a = addons[i];
//                        // URL->URI changed in 3.7a6?
//                        rcxData.dicPath[a.id] = a.getResourceURI('install.rdf')
//                                .QueryInterface(Components.interfaces.nsIFileURL)
//                                .file.parent.path;
//                        //  alert(a.id + ': path=' + rcxData.dicPath[a.id]);
//                    }
//                    rcxData.dicPath.ready = true;
//                    rcxMain.rcxObs.notifyState('dready');
//                });
//                return;
//            }
//            catch (ex) { }
//            rcxData.dicPath.ready = true;
//        }
//
//        if (reinit) this.init();
//    },
//
//    init: function() {
//        if (this.ready) return;
//
//        this.kanjiShown = {};
//        let a = rcxConfig.kindex.split(',');
//        for (let i = a.length - 1; i >= 0; --i) {
//            this.kanjiShown[a[i]] = 1;
//        }
//
//        for (let i = this.dicList.length - 1; i >= 0; --i) {
//            let dic = this.dicList[i];
//            if (dic.isKanji) continue;
//            if ((!dic.findWord) || (!dic.findText)) this.dicList[i] = dic = new RcxDic(dic);
//            if (dic.open) dic.open();
//        }
//
//        this.ready = true;
//    },
//
//    done: function() {
//        this.ready = false;
//        this.kanjiData = null;
//        this.kanjiShown = null;
//        this.radData = null;
//        this.deinflect.done();
//
//        for (let i = this.dicList.length - 1; i >= 0; --i) {
//            try {
//                let dic = this.dicList[i];
//                if (dic.close) dic.close();
//            }
//            catch (ex) { }
//        }
//    },
//
//
//    selected: 0,
//
//    selectNext: function() {
//        this.selected = (this.selected + this.searchSkipped + 1) % this.dicList.length;
//        this.searchSkipped = 0;
//    },
//
//    select: function(n) {
//        if ((n < 0) || (n >= this.dicList.length)) return;
//        this.selected = n;
//        this.searchSkipped = 0;
//    },
//
//    deinflect: {
//        init: function() {
    public static void deinflectInit() throws IOException {
//            this.reasons = [];
//            this.rules = [];
//
//            var buffer = rcxFile.readArray('chrome://rikaichan/content/deinflect.dat');
        List<String> buffer = new ArrayList<String>(100);
//        try {
        final InputStream inputStream;
        {
            InputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream("resources/com/polarcloud/rikaichan/chrome/content/deinflect.dat");
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
//    		PrintStream errorFile = new PrintStream(new FileOutputStream("korenani_err2.txt"));
//    		errorFile.append("- Korenani2 -\n");
//    		System.setErr(errorFile);
            if (fileInputStream != null) {
            	System.err.println("FOUND Ok: resources/com/polarcloud/rikaichan/chrome/content/deinflect.dat");
                inputStream = fileInputStream;
            } else {
            	System.err.println("NOT FOUND: resources/com/polarcloud/rikaichan/chrome/content/deinflect.dat");
                final InputStream resInputStream = RcxData.class.getResourceAsStream("/resources/com/polarcloud/rikaichan/chrome/content/deinflect.dat");
                inputStream = resInputStream;
                if (resInputStream == null) {
                	System.err.println("not found either: <JAR_FILE>/resources/com/polarcloud/rikaichan/chrome/content/deinflect.dat");
                } else {
                	System.err.println("but found Ok: <JAR_FILE>/resources/com/polarcloud/rikaichan/chrome/content/deinflect.dat");
                }
            }
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            
            try {
                String line = br.readLine(); // discard first line of the file
                for (;;) {
                    line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    buffer.add(line);
                    // System.out.println("added new line = "+line);
                }
            } finally {
                br.close();
            }
//        } catch (IOException e) {
//            System.out.println("RcxData.deinflectInit() - caught exception " + e);
//        }
//            var ruleGroup = [];
//            ruleGroup.fromLen = -1;
        RuleGroup ruleGroup = new RuleGroup(-1);
        int ruleGroupFromLen = -1;
//
//            // i = 1: skip header
//            for (var i = 1; i < buffer.length; ++i) {
        for (String line : buffer) {
//                var f = buffer[i].split('\t');
            String[] f = line.split("\t");
//
//                if (f.length == 1) {
            if (f.length == 1) {
//                    this.reasons.push(f[0]);
                reasons.add(f[0]);
                // System.out.println("RcxData.deinflectInit() just added to reasons f[0] = "+f[0]);
            }
//                }
//                else if (f.length == 4) {
            else if (f.length == 4) {
//                    var r = { from: f[0], to: f[1], type: f[2], reason: f[3] };
                Rule r = new Rule(f[0], f[1], Integer.parseInt(f[2]), Integer.parseInt(f[3]));
//                    if (ruleGroup.fromLen != r.from.length) {
//                        ruleGroup = [];
//                        ruleGroup.fromLen = r.from.length;
//                        this.rules.push(ruleGroup);
//                    }
//                    ruleGroup.push(r);
                if (ruleGroupFromLen != r.from.length()) {
                    ruleGroup = new RuleGroup(r.from.length());
                    ruleGroupFromLen = r.from.length();
                    rules.add(ruleGroup);
                    // System.out.println("RcxData.deinflectInit() just added to rules this group = "+ruleGroup);
                }
                ruleGroup.add(r);
                // System.out.println("RcxData.deinflectInit() just added this rule r = "+r);
                // System.out.println("RcxData.deinflectInit() current group = "+ruleGroup);
//                }
            }
//            }
        }
//            this.ready = true;
//        },
    }
//
//        done: function() {
//            this.reasons = null;
//            this.rules = null;
//            this.ready = false;
//        },
//

    static class Rule {
        int type;
        String to;
        String from;
        int reason;

        Rule(String from, String to, int type, int reason) {
            this.from = from;
            this.to = to;
            this.type = type;
            this.reason = reason;
        }

        public String toString() {
            return "from = " + from + ", to = " + to + ", type = " + type + ", reason = " + reason;
        }
    }

    static class RuleGroup {
        private int fromLen;
        List<Rule> rules = new ArrayList<Rule>();

        public int size() {
            return rules.size();
        }

        public void add(Rule rule) {
            rules.add(rule);
        }

        public Rule get(int k) {
            return rules.get(k);
        }

        RuleGroup(int fromLen) {
            this.fromLen = fromLen;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Rule rule : rules) {
                sb.append(rule).append(", ");
            }
            return sb.toString();
        }
    }

    private static List<String> reasons = new ArrayList<String>();
    private static List<RuleGroup> rules = new ArrayList<RuleGroup>();

//        go: function(word) { // deinflect.go(word)
    private static Variant[] deinflect(String word) {
//            if (!this.ready) this.init();
//
//            var have = [];
//            have[word] = 0;
        Set<String> previousWords = new HashSet<String>();
//
//            var r = [{ word: word, type: 0xFF, reason: '' }];
        List<Variant> r = new ArrayList<Variant>();
        r.add(new Variant(word, 0xFF, ""));

        previousWords.add(word);

//            var i = 0;
        int i = 0;
//            do {
        do {
//                word = r[i].word;
            word = r.get(i).word;
//                var wordLen = word.length;
            int wordLen = word.length();
//                var type = r[i].type;
            int type = r.get(i).type;
//
//                for (var j = 0; j < this.rules.length; ++j) {
            for (int j = 0; j < rules.size(); ++j) {
//                    var ruleGroup = this.rules[j];
                RuleGroup ruleGroup = rules.get(j);
//                    if (ruleGroup.fromLen <= wordLen) {
                if (ruleGroup.fromLen <= wordLen) {
//                        var end = word.substr(-ruleGroup.fromLen);
                    String end = word.substring(word.length() - ruleGroup.fromLen);
//                        for (var k = 0; k < ruleGroup.length; ++k) {
                    for (int k = 0; k < ruleGroup.size(); k++) {
//                            var rule = ruleGroup[k];
                        Rule rule = ruleGroup.get(k);
//                            if ((type & rule.type) && (end == rule.from)) {
                        if ((type & rule.type) > 0 && end.equals(rule.from)) {
//                                var newWord = word.substr(0, word.length - rule.from.length) + rule.to;
                            String newWord = word.substring(0, word.length() - rule.from.length()) + rule.to;
//                                if (newWord.length <= 1) continue;
                            if (newWord.length() <= 1)
                                continue;

//                                var o = {};
                            /*
                             * N.B: I don't understand the if() below. Seems to me this if() does nothing
                             * except 'continue'.
                             */
//                                if (have[newWord] != undefined) {
                            if (previousWords.contains(newWord)) {
//                                    o = r[have[newWord]];
//                                    o.type |= (rule.type >> 8);
//                                    continue;
                                continue;
//                                }
                            }
//                                have[newWord] = r.length;
                            previousWords.add(newWord);
                            final String variantReason;
//                                if (r[i].reason.length) o.reason = this.reasons[rule.reason] + ' &lt; ' + r[i].reason;
                            if (r.get(i).reason.length() > 0) {
                                variantReason = reasons.get(rule.reason) + " < " + r.get(i).reason;
                            }
//                                    else o.reason = this.reasons[rule.reason];
                            else {
                                variantReason = reasons.get(rule.reason);
                            }
//                                o.type = rule.type >> 8;
//                                o.word = newWord;
                            Variant o = new Variant(newWord, rule.type >> 8, variantReason);
//                                r.push(o);
                            r.add(o);
                            // System.out.println("RcxData.deinflect() r.length = "+r.size());
//                            }
                        }
//                        }
                    }
//                    }
                }
//                }
            }
//            } while (++i < r.length);
        } while (++i < r.size());
//
//            return r;
        return r.toArray(new Variant[r.size()]);
//        }
    }
//    },
//
//
//    // katakana -> hiragana conversion tables
//    ch:[0x3092,0x3041,0x3043,0x3045,0x3047,0x3049,0x3083,0x3085,0x3087,0x3063,0x30FC,0x3042,0x3044,0x3046,
//        0x3048,0x304A,0x304B,0x304D,0x304F,0x3051,0x3053,0x3055,0x3057,0x3059,0x305B,0x305D,0x305F,0x3061,
//        0x3064,0x3066,0x3068,0x306A,0x306B,0x306C,0x306D,0x306E,0x306F,0x3072,0x3075,0x3078,0x307B,0x307E,
//        0x307F,0x3080,0x3081,0x3082,0x3084,0x3086,0x3088,0x3089,0x308A,0x308B,0x308C,0x308D,0x308F,0x3093],
    public final static char[] ch = new char[] { '\u3092','\u3041','\u3043','\u3045','\u3047','\u3049','\u3083','\u3085','\u3087','\u3063','\u30FC','\u3042','\u3044','\u3046',
        '\u3048','\u304A','\u304B','\u304D','\u304F','\u3051','\u3053','\u3055','\u3057','\u3059','\u305B','\u305D','\u305F','\u3061',
        '\u3064','\u3066','\u3068','\u306A','\u306B','\u306C','\u306D','\u306E','\u306F','\u3072','\u3075','\u3078','\u307B','\u307E',
        '\u307F','\u3080','\u3081','\u3082','\u3084','\u3086','\u3088','\u3089','\u308A','\u308B','\u308C','\u308D','\u308F','\u3093' };
//    cv:[0x30F4,0xFF74,0xFF75,0x304C,0x304E,0x3050,0x3052,0x3054,0x3056,0x3058,0x305A,0x305C,0x305E,0x3060,
//        0x3062,0x3065,0x3067,0x3069,0xFF85,0xFF86,0xFF87,0xFF88,0xFF89,0x3070,0x3073,0x3076,0x3079,0x307C],
    public final static char[] cv = new char[] { '\u30F4','\uFF74','\uFF75','\u304C','\u304E','\u3050','\u3052','\u3054','\u3056','\u3058','\u305A','\u305C','\u305E','\u3060',
        '\u3062','\u3065','\u3067','\u3069','\uFF85','\uFF86','\uFF87','\uFF88','\uFF89','\u3070','\u3073','\u3076','\u3079','\u307C' };
//    cs:[0x3071,0x3074,0x3077,0x307A,0x307D],
    public final static char[] cs = new char[] { '\u3071','\u3074','\u3077','\u307A','\u307D' };

// ## called by wordSearch(word, noKanji), and by translate(text)
//    _wordSearch: function(word, dic, max) {
    public static DictionaryEntry[] _wordSearch(String word, String dictionaryPath, int max) throws SQLException {

          if (word.length() == 0) {
              return new DictionaryEntry[0];
          }

//        if (!this.ready) this.init();
//
//        // half & full-width katakana to hiragana conversion
//        // note: katakana vu is never converted to hiragana

//        var trueLen = [0];
          int[] trueLen = new int[word.length() + 1];
//        var p = 0;
          char p = 0;
//        var r = '';
          String r = "";
          
//        for (let i = 0; i < word.length; ++i) {
          for (int i = 0; i < word.length(); ++i) {
//            let u = word.charCodeAt(i);
              char u = word.charAt(i);
//            let v = u;
              char v = u;
//
//            if (u <= 0x3000) break;
              if (u <= 0x3000)
                  break;
//
//            // full-width katakana to hiragana
//            if ((u >= 0x30A1) && (u <= 0x30F3)) {
              if ((u >= 0x30A1) && (u <= 0x30F3)) {
//                u -= 0x60;
                  u -= 0x60;
//            }
              }
//            // half-width katakana to hiragana
//            else if ((u >= 0xFF66) && (u <= 0xFF9D)) {
              else if ((u >= 0xFF66) && (u <= 0xFF9D)) {
//                u = this.ch[u - 0xFF66];
                  u = ch[u - 0xFF66];
//            }
              }
//            // voiced (used in half-width katakana) to hiragana
//            else if (u == 0xFF9E) {
              else if (u == 0xFF9E) {
//                if ((p >= 0xFF73) && (p <= 0xFF8E)) {
                  if ((p >= 0xFF73) && (p <= 0xFF8E)) {
//                    r = r.substr(0, r.length - 1);
                      r = r.substring(0, r.length() - 1);
//                    u = this.cv[p - 0xFF73];
                      u = cv[p - 0xFF73];
//                }
                  }
//            }
              }
//            // semi-voiced (used in half-width katakana) to hiragana
//            else if (u == 0xFF9F) {
              else if (u == 0xFF9F) {
//                if ((p >= 0xFF8A) && (p <= 0xFF8E)) {
                  if ((p >= 0xFF8A) && (p <= 0xFF8E)) {
//                    r = r.substr(0, r.length - 1);
                      r = r.substring(0, r.length() - 1);
//                    u = this.cs[p - 0xFF8A];
                      u = cs[p - 0xFF8A];
//                }
                  }
//            }
              }
//            // ignore J~
//            else if (u == 0xFF5E) {
              else if (u == 0xFF5E) {
//                p = 0;
                  p = 0;
//                continue;
                  continue;
//            }
              }
//
//            r += String.fromCharCode(u);
              r += u;
//            trueLen[r.length] = i + 1;  // need to keep real length because of the half-width semi/voiced conversion
              trueLen[r.length()] = i + 1; // need to keep real length because of the half-width semi/voiced
//            p = v;
              p = v;
//        }
          }
//        word = r;
        word = r;
        // System.out.println("RcxData._wordSearch() word (kata2hira-converted) = "+r);

//
//
//        var result = { data: [] };
        List<DictionaryEntry> result = new ArrayList<DictionaryEntry>();
//        var maxTrim;
        int maxTrim = 10;
//
//        if (dic.isName) {//            maxTrim = rcxConfig.namax;

//            result.names = 1;
//        }
//        else {
//            maxTrim = rcxConfig.wmax;
//        }
//
//
//        if (max != null) maxTrim = max;
//
//        var have = [];
//        var count = 0;
        int count = 0;
//        var maxLen = 0;
//
        Connection connection = DriverManager.getConnection("jdbc:sqlite:"+dictionaryPath);

//        while (word.length > 0) {
        while (word.length() > 0) {
//            var showInf = (count != 0);
            boolean showInf = (count != 0);
//            var variants = dic.isName ? [{word: word, type: 0xFF, reason: null}] : this.deinflect.go(word);
            final Variant[] variants;
            if (false) {
                // TODO? case of dic.isName()
                variants = new Variant[] { new Variant(word, 0xFF, null) };
            } else {
                if (DEBUG) {
                    System.out.println("RcxData._wordSearch() calling deinflect(" + word + ")");
                }
                variants = deinflect(word);
                // System.out.println("RcxData._wordSearch() variants.length = "+variants.length);
                if (DEBUG) {
                    for (Variant var : variants) {
                        System.out.println("RcxData._wordSearch() var = " + var);
                    }
                }
            }
//            for (var i = 0; i < variants.length; i++) {
//                var v = variants[i];
            for (int i = 0; i < variants.length; i++) {
                Variant variant = variants[i];
//                var entries = dic.findWord(v.word);
                ResultSet rs = findWord(connection, variant.word);
//                for (var j = 0; j < entries.length; ++j) {
                while (rs.next()) {
//                    var dentry = entries[j];
                    String dentry = rs.getString(3);
//                    if (have[dentry]) continue;  <= ???????
//
//                    var ok = true;
                    boolean ok = true;
//                    if ((dic.hasType) && (i > 0)) {
//                        // i > 0 a de-inflected word
                    if (i > 0) { // variants(i>0) is a deinflected word

//                        var gloss = dentry.split(/[,()]/);
                        String[] gloss = dentry.split("[,\\(\\)]");
//                        var y = v.type;
                        int y = variant.type;
//                        var z;
                        int z;
//                        for (z = gloss.length - 1; z >= 0; --z) {
                        for (z = gloss.length - 1; z >= 0; --z) {
//                            var g = gloss[z];
                            final String g = gloss[z];
//                            if ((y & 1) && (g == 'v1')) break;
                            if ((variant.type & 1) != 0 && ("v1".equals(g)))
                                break;
//                            if ((y & 4) && (g == 'adj-i')) break;
                            if ((y & 4) != 0 && ("adj-i".equals(g)))
                                break;
//                            if ((y & 2) && (g.substr(0, 2) == 'v5')) break;
                            if ((y & 2) != 0 && ("v5".equals(g.substring(0, Math.min(2, g.length())))))
                                break;
//                            if ((y & 16) && (g.substr(0, 3) == 'vs-')) break;
                            if ((y & 16) != 0 && ("vs-".equals(g.substring(0, Math.min(3, g.length())))))
                                break;
//                            if ((y & 8) && (g == 'vk')) break;
                            if ((y & 8) != 0 && ("vk".equals(g)))
                                break;
//                        }
                        }
//                        ok = (z != -1);
                        ok = (z != -1);
//                    }
                    }
//                    if ((ok) && (dic.hasType) && (rcxConfig.hidex)) {
//                        if (dentry.match(/\/\([^\)]*\bX\b.*?\)/)) ok = false;
//                    }
//                    if (ok) {
                    if (ok) {
//                        if (count >= maxTrim) {
//                            result.more = 1;
//                            break;
//                        }
//
//                        have[dentry] = 1;
//                        ++count;
//                        if (maxLen == 0) maxLen = trueLen[word.length];
//
//                        var r = null;
                        String r2 = "";
//                        if (v.reason) {
                        if (variant.reason != "") {
//                            if (showInf) r = '&lt; ' + v.reason + ' &lt; ' + word;
                            if (showInf) {
                                r2 = "<" + variant.reason + "<" + word;
                            }
//                                else r = '&lt; ' + v.reason;
                            else {
                                r2 = "<" + variant.reason;
                            }
//                        }
                        }
//                        result.data.push([dentry, r]);
                        result.add(new DictionaryEntry(rs.getString(1), rs.getString(2), rs.getString(3), r2));
//                    }
                    }
//                }   // for j < entries.length
                } // while (rs.next()) {
//                if (count >= maxTrim) break;
//            }   // for i < variants.length
            } // for variant
//            if (count >= maxTrim) break;
//            word = word.substr(0, word.length - 1);
            word = word.substring(0, word.length() - 1);
//        }   // while word.length > 0
        } // while word.length > 0

        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e);
        }

//
//        if (result.data.length == 0) return null;
//
//        result.matchLen = maxLen;
//        return result;
//    },
        DictionaryEntry[] arrayResult = result.toArray(new DictionaryEntry[result.size()]);
        return arrayResult;

    }

    //
    static class Variant {
        private final String word;
        private final int type;
        final String reason;

        public Variant(String word, int type, String reason) {
            this.word = word;
            this.type = type;
            this.reason = reason;
        }

        public String toString() {
            return " " + word + ", type = " + type + ", reason = " + reason;
        }
    }

    public static class KanjiEntry {
        private String kanji;
        private String codes;
        private String onkun;
        private String nanori;
        private String eigo;
        
    	public KanjiEntry(String a, String b, String c, String d, String e) {
    		this.kanji = a;
    		this.codes = b;
    		this.onkun = c;
    		this.nanori = d;
    		this.eigo = e;
    	}
    	
        public String toString() {
            return "[ kanji = " + kanji + ", codes = " + codes + ", onkun = " + onkun + ", nanori = " + nanori + " eigo = " + eigo + " ]";
        }

    }
    
    public static class DictionaryEntry {
        private String word;
        private String reading;
        private String gloss;
        private String reason;

        DictionaryEntry(String a, String b, String c, String reason) {
            this.word = a;
            this.reading = b;
            this.gloss = c;
            this.reason = reason;
        }

        public String toString() {
            return "[ word = " + word + ", reading = " + reading + ", gloss = " + gloss + ", reason = " + reason + " ]";
        }
        
        public String word() {
        	return word == null?"":word;
        }

        public String reading() {
        	return reading == null?"":reading;
        }

        public String gloss() {
        	return gloss == null?"":gloss;
        }

        public String reason() {
        	return reason == null?"":reason;
        }

    }

    // ////////////////////////////////////////////
    //
    // RcxMain.lookupSearch() ===> wordSearch()
    // RcxMain.show() ===> wordSearch()
    // ////////////////////////////////////////////

//    wordSearch: function(word, noKanji) {
//        this.searchSkipped = 0;
//        let ds = this.selected;
//        do {
//            let dic = this.dicList[ds];
//            if ((!noKanji) || (!dic.isKanji)) {
//                let e;
//                if (dic.isKanji) e = this.kanjiSearch(word.charAt(0));
//                    else e = this._wordSearch(word, dic, null);
//                if (e) {
//                    if (ds != 0) e.title = dic.name;
//                    return e;
//                }
//            }
//            this.searchSkipped++;
//            ds = (ds + 1) % this.dicList.length;
//        } while (ds != this.selected);
//        return null;
//    },
//
//    translate: function(text) {
//        var result = { data: [], textLen: text.length };
//        while (text.length > 0) {
//            var e = null;
//            var ds = this.selected;
//            do {
//                if (!this.dicList[ds].isKanji) {
//                    e = this._wordSearch(text, this.dicList[ds], 1);
//                    if (e != null) break;
//                }
//                ds = (ds + 1) % this.dicList.length;
//            } while (ds != this.selected);
//
//            if (e != null) {
//                if (result.data.length >= rcxConfig.wmax) {
//                    result.more = 1;
//                    break;
//                }
//                result.data.push(e.data[0]);
//                text = text.substr(e.matchLen);
//            }
//            else {
//                text = text.substr(1);
//            }
//        }
//        this.searchSkipped = (this.selected == this.kanjiPos) ? 1 : 0;
//        if (result.data.length == 0) return null;
//        result.textLen -= text.length;
//        return result;
//    },

      // RcxMain.lookupSearch() ====> textSearch()
//    textSearch: function(text) {
//        this.searchSkipped = 0;
//        if (!this.ready) this.init();
//        text = text.toLowerCase();
//        let ds = this.selected;
//        do {
//            let dic = this.dicList[ds];
//            if (!dic.isKanji) {
//                let result = { data: [], reason: [], kanji: 0, more: 0, names: dic.isName };
//
//                let r = dic.findText(text);
//
//                // try priorizing
//                let list = [];
//                let sW = /[\sW]/;
//                let slashText = '/' + text + '/';
//                for (let i = 0; i < r.length; ++i) {
//                    let t = r[i].replace(/\(.+?\)/g, '').toLowerCase();
//
//                    // closer to the beginning = better
//                    let d = t.indexOf(text);
//                    if (d >= 0) {
//                        // the exact text within an entry = best
//                        if (t.replace(/\s+/g, '').indexOf(slashText) != -1) {
//                            d -= 100;
//                        }
//                        // a word within an entry = better
//                        else if (((d == 0) || (sW.test(t.substr(d - 1, 1)))) &&
//                                (((d + text.length) >= t.length) || (sW.test(t.substr(d + text.length, 1))))) {
//                            d -= 50;
//                        }
//                    }
//                    else d = 9999;
//                    list.push({ rank: d, text: r[i] });
//                }
//
//                let max = dic.isName ? rcxConfig.namax : rcxConfig.wmax;
//                list.sort(function(a, b) { return a.rank - b.rank });
//                for (let i = 0; i < list.length; ++i) {
//                    if (result.data.length >= max) {
//                        result.more = 1;
//                        break;
//                    }
//                    //  result.data.push([list[i].text + '[' + list[i].rank + ']/', null]);
//                    result.data.push([list[i].text, null]);
//                }
//
//                /*
//                let j = (list.length > 100) ? 100 : list.length;
//                for (let i = 0; i < j; ++i) {
//                    rcxDebug.echo(i + ': [' + list[i].rank + '] ' + list[i].text);
//                }*/
//
//                /*
//                for (let i = 0; i < r.length; ++i) {
//                    if (result.data.length >= max) {
//                        result.more = 1;
//                        break;
//                    }
//                    result.data.push([r[i], null]);
//                }
//                */
//
//                if (result.data.length) {
//                    if (ds != 0) result.title = dic.name;
//                    return result;
//                }
//            }
//            this.searchSkipped++;
//            ds = (ds + 1) % this.dicList.length;
//        } while (ds != this.selected);
//        return null;
//    },
//
  // RcxMain.lookupSearch() =============================> kanjiSearch()
  // RcxMain.lookupSearch() ===> RcxData.wordSearch() ===> kanjiSearch()
  // RcxMain.show() ===> RcxData.wordSearch() ===> kanjiSearch()
//    // @@@ todo later...
//    kanjiSearch: function(kanji) {
//        const hex = '0123456789ABCDEF';
//        var kde;
//        var result;
//        var a, b;
//        var i;
//
//        i = kanji.charCodeAt(0);
//        if (i < 0x3000) return null;
//
//        if (!this.kanjiData) {
//            this.kanjiData = rcxFile.read((typeof(rcxKanjiURI) == 'string') ? rcxKanjiURI : 'chrome://rikaichan/content/kanji.dat');
//        }
//
//        kde = this.find(this.kanjiData, kanji);
//        if (!kde) return null;
//
//        a = kde.split('|');
//        if (a.length != 6) return null;
//
//        result = { };
//        result.kanji = a[0];
//
//        result.misc = {};
//        result.misc['U'] = hex[(i >>> 12) & 15] + hex[(i >>> 8) & 15] + hex[(i >>> 4) & 15] + hex[i & 15];
//
//        b = a[1].split(' ');
//        for (i = 0; i < b.length; ++i) {
//            if (b[i].match(/^([A-Z]+)(.*)/)) {
//                if (!result.misc[RegExp.$1]) result.misc[RegExp.$1] = RegExp.$2;
//                    else result.misc[RegExp.$1] += ' ' + RegExp.$2;
//            }
//        }
//
//        result.onkun = a[2].replace(/\s+/g, '\u3001 ');
//        result.nanori = a[3].replace(/\s+/g, '\u3001 ');
//        result.bushumei = a[4].replace(/\s+/g, '\u3001 ');
//        result.eigo = a[5];
//
//        return result;
//    },
//
    public static KanjiEntry kanjiSearch(String kanji) throws IOException {
    	
        final InputStream inputStream;
        {
            InputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream("resources/com/polarcloud/rikaichan/chrome/content/kanji.dat");
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
//    		PrintStream errorFile = new PrintStream(new FileOutputStream("korenani_err2.txt"));
//    		errorFile.append("- Korenani2 -\n");
//    		System.setErr(errorFile);
            if (fileInputStream != null) {
            	System.err.println("FOUND Ok: resources/com/polarcloud/rikaichan/chrome/content/kanji.dat");
                inputStream = fileInputStream;
            } else {
            	System.err.println("NOT FOUND: resources/com/polarcloud/rikaichan/chrome/content/kanji.dat");
                final InputStream resInputStream = RcxData.class.getResourceAsStream("/resources/com/polarcloud/rikaichan/chrome/content/kanji.dat");
                inputStream = resInputStream;
                if (resInputStream == null) {
                	System.err.println("not found either: <JAR_FILE>/resources/com/polarcloud/rikaichan/chrome/content/kanji.dat");
                } else {
                	System.err.println("but found Ok: <JAR_FILE>/resources/com/polarcloud/rikaichan/chrome/content/kanji.dat");
                }
            }
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
        
        try {
            String line = br.readLine(); // discard first line of the file
            for (;;) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
          	  if (kanji.equals(line.substring(0, 1))) {
        		  String[] details = line.split("\\|");
        		  return new KanjiEntry(kanji, details[1], details[2], details[3], details[5]);
        	  }

            }
        } finally {
            br.close();
        }
    	return new KanjiEntry(kanji, "-", "-", "-", "-");

    	
    	
//    	List<String> lines=Files.readAllLines(Paths.get("resources/com/polarcloud/rikaichan/chrome/content/kanji.dat"), Charset.forName("UTF-8"));
//    	for(String line:lines){
//    	  if (kanji.equals(line.substring(0, 1))) {
//    		  String[] details = line.split("\\|");
//    		  return new KanjiEntry(kanji, details[1], details[2], details[3], details[5]);
//    	  }
//    	}    	
//    	return new KanjiEntry(kanji, "-", "-", "-", "-");
    }
    
//    // ---
//
//    numList: [
///*
//        'C',    'Classical Radical',
//        'DR',   'Father Joseph De Roo Index',
//        'DO',   'P.G. O\'Neill Index',
//        'O',    'P.G. O\'Neill Japanese Names Index',
//        'Q',    'Four Corner Code',
//        'MN',   'Morohashi Daikanwajiten Index',
//        'MP',   'Morohashi Daikanwajiten Volume/Page',
//        'K',    'Gakken Kanji Dictionary Index',
//        'W',    'Korean Reading',
//*/
//        'H',    'Halpern',
//        'L',    'Heisig',
//        'E',    'Henshall',
//        'DK',   'Kanji Learners Dictionary',
//        'N',    'Nelson',
//        'V',    'New Nelson',
//        'Y',    'PinYin',
//        'P',    'Skip Pattern',
//        'IN',   'Tuttle Kanji &amp; Kana',
//        'I',    'Tuttle Kanji Dictionary',
//        'U',    'Unicode'
//    ],
//
//    makeHtml: function(entry) {
//        var e;
//        var b;
//        var c, s, t;
//        var i, j, n;
//
//        if (entry == null) return '';
//
//        if (!this.ready) this.init();
//        if (!this.radData) this.radData = rcxFile.readArray('chrome://rikaichan/content/radicals.dat');
//
//        b = [];
//
//        if (entry.kanji) {
//            var yomi;
//            var box;
//            var bn;
//            var k;
//            var nums;
//
//            yomi = entry.onkun.replace(/\.([^\u3001]+)/g, '<span class="k-yomi-hi">$1</span>');
//            if (entry.nanori.length) {
//                yomi += '<br/><span class="k-yomi-ti">\u540D\u4E57\u308A</span> ' + entry.nanori;
//            }
//            if (entry.bushumei.length) {
//                yomi += '<br/><span class="k-yomi-ti">\u90E8\u9996\u540D</span> ' + entry.bushumei;
//            }
//
//            bn = entry.misc['B'] - 1;
//            k = entry.misc['G'];
//            switch (k) {
//            case 8:
//                k = 'general<br/>use';
//                break;
//            case 9:
//                k = 'name<br/>use';
//                break;
//            default:
//                k = isNaN(k) ? '-' : ('grade<br/>' + k);
//                break;
//            }
//            box = '<table class="k-abox-tb"><tr>' +
//                '<td class="k-abox-r">radical<br/>' + this.radData[bn].charAt(0) + ' ' + (bn + 1) + '</td>' +
//                '<td class="k-abox-g">' + k + '</td>' +
//                '</tr><tr>' +
//                '<td class="k-abox-f">freq<br/>' + (entry.misc['F'] ? entry.misc['F'] : '-') + '</td>' +
//                '<td class="k-abox-s">strokes<br/>' + entry.misc['S'] + '</td>' +
//                '</tr></table>';
//            if (this.kanjiShown['COMP']) {
//                k = this.radData[bn].split('\t');
//                box += '<table class="k-bbox-tb">' +
//                        '<tr><td class="k-bbox-1a">' + k[0] + '</td>' +
//                        '<td class="k-bbox-1b">' + k[2] + '</td>' +
//                        '<td class="k-bbox-1b">' + k[3] + '</td></tr>';
//                j = 1;
//                for (i = 0; i < this.radData.length; ++i) {
//                    s = this.radData[i];
//                    if ((bn != i) && (s.indexOf(entry.kanji) != -1)) {
//                        k = s.split('\t');
//                        c = ' class="k-bbox-' + (j ^= 1);
//                        box += '<tr><td' + c + 'a">' + k[0] + '</td>' +
//                                '<td' + c + 'b">' + k[2] + '</td>' +
//                                '<td' + c + 'b">' + k[3] + '</td></tr>';
//                    }
//                }
//                box += '</table>';
//            }
//
//            nums = '';
//            j = 0;
//
//            for (i = 0; i < this.numList.length; i += 2) {
//                c = this.numList[i];
//                if (this.kanjiShown[c]) {
//                    s = entry.misc[c];
//                    c = ' class="k-mix-td' + (j ^= 1) + '"';
//                    nums += '<tr><td' + c + '>' + this.numList[i + 1] + '</td><td' + c + '>' + (s ? s : '-') + '</td></tr>';
//                }
//            }
//            if (nums.length) nums = '<table class="k-mix-tb">' + nums + '</table>';
//
//            b.push('<table class="k-main-tb"><tr><td valign="top">');
//            b.push(box);
//            b.push('<span class="k-kanji">' + entry.kanji + '</span><br/>');
//            if (!rcxConfig.hidedef) b.push('<div class="k-eigo">' + entry.eigo + '</div>');
//            b.push('<div class="k-yomi">' + yomi + '</div>');
//            b.push('</td></tr><tr><td>' + nums + '</td></tr></table>');
//            return b.join('');
//        }
//
//        s = t = '';
//
//        if (entry.names) {
//            c = [];
//
//            b.push('<div class="w-title">Names Dictionary</div><table class="w-na-tb"><tr><td>');
//            for (i = 0; i < entry.data.length; ++i) {
//                e = entry.data[i][0].match(/^(.+?)\s+(?:\[(.*?)\])?\s*\/([\S\s]+)\//);
//                if (!e) continue;
//
//                if (s != e[3]) {
//                    c.push(t);
//                    t = '';
//                }
//
//                if (e[2]) c.push('<span class="w-kanji">' + e[1] + '</span> &#32; <span class="w-kana">' + e[2] + '</span><br/> ');
//                    else c.push('<span class="w-kana">' + e[1] + '</span><br/> ');
//
//                s = e[3];
//                if (rcxConfig.hidedef) t = '';
//                    else t = '<span class="w-def">' + s.replace(/\//g, '; ').replace(/\n/g, '<br/>') + '</span><br/>';
//            }
//            c.push(t);
//            if (c.length > 4) {
//                n = (c.length >> 1) + 1;
//                b.push(c.slice(0, n + 1).join(''));
//
//                t = c[n];
//                c = c.slice(n, c.length);
//                for (i = 0; i < c.length; ++i) {
//                    if (c[i].indexOf('w-def') != -1) {
//                        if (t != c[i]) b.push(c[i]);
//                        if (i == 0) c.shift();
//                        break;
//                    }
//                }
//
//                b.push('</td><td>');
//                b.push(c.join(''));
//            }
//            else {
//                b.push(c.join(''));
//            }
//            if (entry.more) b.push('...<br/>');
//            b.push('</td></tr></table>');
//        }
//        else {
//            if (entry.title) {
//                b.push('<div class="w-title">' + entry.title + '</div>');
//            }
//
//            var pK = '';
//            var k;
//
//            for (i = 0; i < entry.data.length; ++i) {
//                e = entry.data[i][0].match(/^(.+?)\s+(?:\[(.*?)\])?\s*\/([\S\s]+)\//);
//                if (!e) continue;
//
//                /*
//                    e[1] = kanji/kana
//                    e[2] = kana
//                    e[3] = definition
//                */
//                if (s != e[3]) {
//                    b.push(t);
//                    pK = k = '';
//                }
//                else {
//                    k = t.length ? '<br/>' : '';
//                }
//
//                if (e[2]) {
//                    if (pK == e[1]) k = '\u3001 <span class="w-kana">' + e[2] + '</span>';
//                        else k += '<span class="w-kanji">' + e[1] + '</span> &#32; <span class="w-kana">' + e[2] + '</span>';
//                    pK = e[1];
//                }
//                else {
//                    k += '<span class="w-kana">' + e[1] + '</span>';
//                    pK = '';
//                }
//                b.push(k);
//
//                if (entry.data[i][1]) b.push(' <span class="w-conj">(' + entry.data[i][1] + ')</span>');
//
//                s = e[3];
//                if (rcxConfig.hidedef) {
//                    t = '<br/>';
//                }
//                else {
//                    t = s.replace(/\//g, '; ');
//                    if (!rcxConfig.wpos) t = t.replace(/^\([^)]+\)\s*/, '');
//                    if (!rcxConfig.wpop) t = t.replace('; (P)', '');
//                    t = t.replace(/\n/g, '<br/>');
//                    t = '<br/><span class="w-def">' + t + '</span><br/>';
//                }
//            }
//            b.push(t);
//            if (entry.more) b.push('...<br/>');
//        }
//
//        return b.join('');
//    },
//
//    makeText: function(entry, max) {
//        var e;
//        var b;
//        var i, j;
//        var t;
//
//        if (entry == null) return '';
//        if (!this.ready) this.init();
//
//        b = [];
//
//        if (entry.kanji) {
//            b.push(entry.kanji + '\n');
//            b.push((entry.eigo.length ? entry.eigo : '-') + '\n');
//
//            b.push(entry.onkun.replace(/\.([^\u3001]+)/g, '\uFF08$1\uFF09') + '\n');
//            if (entry.nanori.length) {
//                b.push('\u540D\u4E57\u308A\t' + entry.nanori + '\n');
//            }
//            if (entry.bushumei.length) {
//                b.push('\u90E8\u9996\u540D\t' + entry.bushumei + '\n');
//            }
//
//            for (i = 0; i < this.numList.length; i += 2) {
//                e = this.numList[i];
//                if (this.kanjiShown[e]) {
//                    j = entry.misc[e];
//                    b.push(this.numList[i + 1].replace('&amp;', '&') + '\t' + (j ? j : '-') + '\n');
//                }
//            }
//        }
//        else {
//            if (max > entry.data.length) max = entry.data.length;
//            for (i = 0; i < max; ++i) {
//                e = entry.data[i][0].match(/^(.+?)\s+(?:\[(.*?)\])?\s*\/(.+)\//);
//                if (!e) continue;
//
//                if (e[2]) {
//                    b.push(e[1] + '\t' + e[2]);
//                }
//                else {
//                    b.push(e[1]);
//                }
//
//                t = e[3].replace(/\//g, '; ');
//                if (!rcxConfig.wpos) t = t.replace(/^\([^)]+\)\s*/, '');
//                if (!rcxConfig.wpop) t = t.replace('; (P)', '');
//                b.push('\t' + t + '\n');
//            }
//        }
//        return b.join('');
//    },
//
//    // ---
//
//    find: function(data, text) {
//        const tlen = text.length;
//        var beg = 0;
//        var end = data.length - 1;
//        var i;
//        var mi;
//        var mis;
//
//        while (beg < end) {
//            mi = (beg + end) >> 1;
//            i = data.lastIndexOf('\n', mi) + 1;
//
//            mis = data.substr(i, tlen);
//            if (text < mis) end = i - 1;
//                else if (text > mis) beg = data.indexOf('\n', mi + 1) + 1;
//                    else return data.substring(i, data.indexOf('\n', mi + 1));
//        }
//        return null;
//    }
//};
//
//var rcxFile = {
//    read: function(uri) {
//        var inp = Components.classes['@mozilla.org/network/io-service;1']
//                .getService(Components.interfaces.nsIIOService)
//                .newChannel(uri, null, null)
//                .open();
//
//        var is = Components.classes['@mozilla.org/intl/converter-input-stream;1']
//                    .createInstance(Components.interfaces.nsIConverterInputStream);
//        is.init(inp, 'UTF-8', 4 * 1024 * 1024,
//            Components.interfaces.nsIConverterInputStream.DEFAULT_REPLACEMENT_CHARACTER);
//
//        var buffer = '';
//        var s = {};
//        while (is.readString(-1, s) > 0) {
//            buffer += s.value;
//        }
//        is.close();
//
//        return buffer;
//    },
//
//    readArray: function(name) {
//        var a = this.read(name).split('\n');
//        while ((a.length > 0) && (a[a.length - 1].length == 0)) a.pop();
//        return a;
//    }
//};
//
//function RcxDb(name)
//{
//    this.open = function() {
//        var f;
//
//        if (name.match(/(.+)\|(.+)/)) {
//            let id = RegExp.$1;
//            let nm = RegExp.$2;
//            try {
//                f = Components.classes['@mozilla.org/extensions/manager;1']
//                    .getService(Components.interfaces.nsIExtensionManager)
//                    .getInstallLocation(id).getItemFile(id, nm);
//            }
//            catch (ex) {
//                if ((rcxData.dicPath) && (rcxData.dicPath[id])) {
//                    f = Components.classes['@mozilla.org/file/local;1']
//                        .createInstance(Components.interfaces.nsILocalFile);
//                    f.initWithPath(rcxData.dicPath[id]);
//                    f.append(nm);
//                }
//
//                if (!f) throw 'Could not find or open ' + id + '/' + nm;
//            }
//        }
//        else {
//            f = Components.classes['@mozilla.org/file/local;1']
//                .createInstance(Components.interfaces.nsILocalFile);
//            f.initWithPath(name);
//        }
//
//        // The files may get installed as read-only, breaking
//        // index creation. Try changing the file permission.
//        if (!f.isWritable()) f.permissions |= 0x180;    // 0x180=0600 strict mode doesn't like octals
//
//        this.db = Components.classes['@mozilla.org/storage/service;1']
//            .getService(Components.interfaces.mozIStorageService)
//            .openDatabase(f);
//    };
//
//    this.close = function() {
//        if (this.db) {
//            try {
//                this.db.close();
//            }
//            catch (ex) {
//            }
//            this.db = null;
//        }
//    };
//
//    this.exec = function(stm) {
//        var rows = [];
//        if (!this.db) this.open();
//        var st = this.db.createStatement(stm);
//        for (var i = arguments.length - 1; i > 0; --i) {
//            if (arguments[i] != null) st.bindUTF8StringParameter(i - 1, arguments[i]);
//        }
//        while (st.executeStep()) {
//            var r = [];
//            for (var i = st.columnCount - 1; i >= 0; --i) {
//                r[st.getColumnName(i)] = st.getUTF8String(i);
//            }
//            rows.push(r);
//        }
//        return rows;
//    };
//
//    this.indexExists = function(index) {
//        if (!this.db) this.open();
//        return this.db.indexExists(index);
//    };
//
//    this.beginTransaction = function() {
//        if (!this.db) this.open();
//        this.db.beginTransaction();
//    };
//
//    this.commitTransaction = function() {
//        this.db.commitTransaction();
//    };
//
//    this.rollbackTransaction = function() {
//        this.db.rollbackTransaction();
//    };
//
//    return this;
//}
//
//function RcxDic(dic)
//{
//    this.name = dic.name;
//    this.version = dic.version;
//    this.id = dic.id;
//    this.hasType = dic.hasType;
//    this.isName = dic.isName;
//
//    this.open = function() {
//        try {
//            if (this.rdb) return;
//
//            this.rdb = new RcxDb(this.id + '|dict.sqlite');
//            this.rdb.open();
//            this.checkIndex('kanji');
//            this.checkIndex('kana');
//        }
//        catch (ex) {
//            this.close();
//            throw ex;
//        }
//    };
//
//    this.close = function() {
//        if (this.rdb) {
//            try {
//                this.rdb.close();
//            }
//            catch (ex) {
//            }
//            this.rdb = null;
//        }
//    };
//
//    this.checkIndex = function(name) {
//        var ix = 'ix_' + name;
//        if (this.rdb.indexExists(ix)) return;
//
//        if (!rcxData.indexCreateNotice) {
//            alert('A dictionary index needs to be created. This may take a while on some systems. Click OK to start.');
//            rcxData.indexCreateNotice = true;
//        }
//
//        this.rdb.exec('CREATE INDEX ' + ix + ' ON dict (' + name + ' ASC)');
//    };
//
//    this.find = function(query, arg1) {
//        if (!this.rdb) this.open();
//        var r = this.rdb.exec(query, arg1);
//        var entries = [];
//        for (var i = 0; i < r.length; ++i) {
//            var x = r[i];
//            if (!x.entry.length) continue;
//            // rcx currently expects an edict-like format
//            if (x.entry[x.entry.length - 1] == '/') entries.push(x.entry);
//                else entries.push((x.kanji ? (x.kanji + ' [' + x.kana + ']') : x.kana) + ' /' + x.entry + '/');
//        }
//        return entries;
//    };
//
//    this.findWord = function(word) {
    private static ResultSet findWord(Connection connection, String word) {
        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.

//        return this.find('SELECT * FROM dict WHERE kanji=?1 OR kana=?1 LIMIT 100', word);
            rs = statement.executeQuery(" SELECT * FROM dict WHERE kanji = '" + word + "' OR kana = '" + word
                    + "' LIMIT 100");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return rs;
    }

//    this.findText = function(text) {
//        return this.find('SELECT * FROM dict WHERE entry LIKE ?1 LIMIT 300', '%' + text + '%');
//    };
//
//    return this;
//};
}