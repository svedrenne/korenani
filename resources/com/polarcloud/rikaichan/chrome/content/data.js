/*

	Rikaichan
	Copyright (C) 2005-2012 Jonathan Zarate
	http://www.polarcloud.com/

	---

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

	---

	Please do not change or remove any of the copyrights or links to web pages
	when modifying any of the files.

*/

var rcxData = {
	ready: false,
	kanjiPos: 0,
	dicList: [],

	loadConfig: function() {
		let reinit = false;

		if (this.ready) {
			this.done();
			reinit = true;
		}

		if (typeof(rcxDicList) == 'undefined') {
			rcxDicList = {};
			this.missing = true;
		}
		if (rcxDicList['kanji@local'] == null) {
			rcxDicList['kanji@local'] = {
				name: 'Kanji',
				id: 'kanji@local',
				isKanji: true
			};
		}

		//	rcxMain.global().rcxDicList = rcxDicList;

		let prefs = new rcxPrefs();
		let order = prefs.getString('dpriority');
		if (order == '') order = 'rikaichan-jpen@polarcloud.com#|rikaichan-jpde@polarcloud.com#|rikaichan-jpfr@polarcloud.com#|rikaichan-jpru@polarcloud.com#|rikaichan-jpnames@polarcloud.com#|kanji@local#';

		this.dicList = [];
		this.kanjiPos = 0;

		let done = {};

		// arrange dicList based on user setting
		let oa = order.split('|');
		for (let i = 0; i < oa.length; ++i) {
			if (oa[i].match(/^(.+?)#/)) {
				let dic = rcxDicList[RegExp.$1];
				if (dic) {
					this.dicList.push(dic);
					done[dic.id] = true;
				}
			}
		}

		// anything new is added at the end
		let addedNew = false;
		for (let id in rcxDicList) {
			if (!done[id]) {
				this.dicList.push(rcxDicList[id]);
				addedNew = true;
			}
		}

		let ids = [];

		// rebuild dpriority string which is also used by Options
		let order2 = [];
		for (let i = 0; i < this.dicList.length; ++i) {
			let dic = this.dicList[i];
			let s = dic.id + '#' + dic.name;
			if (dic.version) s += ' v' + dic.version;
			order2.push(s)

			if (dic.isKanji) this.kanjiPos = i;	// keep track of position
				else ids.push(dic.id);
		}
		order2 = this.missing ? '' : order2.join('|');
		if (order != order2) prefs.setString('dpriority', order2);

		if (addedNew) {
			// show dictionary tab if we have a new dictionary
			window.openDialog('chrome://rikaichan/content/options.xul', '', 'chrome,centerscreen', 'dic');
		}

		// FF 3.7a workaround; @@ revisit later
		if (!rcxData.dicPath) {
			rcxData.dicPath = { ready: false };
			try {
				Components.utils.import('resource://gre/modules/AddonManager.jsm');
				// ! asynchronous
				AddonManager.getAddonsByIDs(ids, function(addons) {
					for (let i = 0; i < addons.length; ++i) {
						let a = addons[i];
						// URL->URI changed in 3.7a6?
						rcxData.dicPath[a.id] = a.getResourceURI('install.rdf')
								.QueryInterface(Components.interfaces.nsIFileURL)
								.file.parent.path;
						//	alert(a.id + ': path=' + rcxData.dicPath[a.id]);
					}
					rcxData.dicPath.ready = true;
					rcxMain.rcxObs.notifyState('dready');
				});
				return;
			}
			catch (ex) { }
			rcxData.dicPath.ready = true;
		}

		if (reinit) this.init();
	},

	init: function() {
		if (this.ready) return;

		this.kanjiShown = {};
		let a = rcxConfig.kindex.split(',');
		for (let i = a.length - 1; i >= 0; --i) {
			this.kanjiShown[a[i]] = 1;
		}

		for (let i = this.dicList.length - 1; i >= 0; --i) {
			let dic = this.dicList[i];
			if (dic.isKanji) continue;
			if ((!dic.findWord) || (!dic.findText)) this.dicList[i] = dic = new RcxDic(dic);
			if (dic.open) dic.open();
		}

		this.ready = true;
	},

	done: function() {
		this.ready = false;
		this.kanjiData = null;
		this.kanjiShown = null;
		this.radData = null;
		this.deinflect.done();

		for (let i = this.dicList.length - 1; i >= 0; --i) {
			try {
				let dic = this.dicList[i];
				if (dic.close) dic.close();
			}
			catch (ex) { }
		}
	},


	selected: 0,

	selectNext: function() {
		this.selected = (this.selected + this.searchSkipped + 1) % this.dicList.length;
		this.searchSkipped = 0;
	},

	select: function(n) {
		if ((n < 0) || (n >= this.dicList.length)) return;
		this.selected = n;
		this.searchSkipped = 0;
	},

	deinflect: {
		init: function() {
			this.reasons = [];
			this.rules = [];

			var buffer = rcxFile.readArray('chrome://rikaichan/content/deinflect.dat');
			var ruleGroup = [];
			ruleGroup.fromLen = -1;

			// i = 1: skip header
			for (var i = 1; i < buffer.length; ++i) {
				var f = buffer[i].split('\t');

				if (f.length == 1) {
					this.reasons.push(f[0]);
				}
				else if (f.length == 4) {
					var r = { from: f[0], to: f[1], type: f[2], reason: f[3] };
					if (ruleGroup.fromLen != r.from.length) {
						ruleGroup = [];
						ruleGroup.fromLen = r.from.length;
						this.rules.push(ruleGroup);
					}
					ruleGroup.push(r);
				}
			}
			this.ready = true;
		},

		done: function() {
			this.reasons = null;
			this.rules = null;
			this.ready = false;
		},

		go: function(word) {
			if (!this.ready) this.init();

			var have = [];
			have[word] = 0;

			var r = [{ word: word, type: 0xFF, reason: '' }];
			var i = 0;
			do {
				word = r[i].word;
				var wordLen = word.length;
				var type = r[i].type;

				for (var j = 0; j < this.rules.length; ++j) {
					var ruleGroup = this.rules[j];
					if (ruleGroup.fromLen <= wordLen) {
						var end = word.substr(-ruleGroup.fromLen);
						for (var k = 0; k < ruleGroup.length; ++k) {
							var rule = ruleGroup[k];
							if ((type & rule.type) && (end == rule.from)) {
								var newWord = word.substr(0, word.length - rule.from.length) + rule.to;
								if (newWord.length <= 1) continue;
								var o = {};
								if (have[newWord] != undefined) {
									o = r[have[newWord]];
									o.type |= (rule.type >> 8);
									continue;
								}
								have[newWord] = r.length;
								if (r[i].reason.length) o.reason = this.reasons[rule.reason] + ' &lt; ' + r[i].reason;
									else o.reason = this.reasons[rule.reason];
								o.type = rule.type >> 8;
								o.word = newWord;
								r.push(o);
							}
						}
					}
				}
			} while (++i < r.length);

			return r;
		}
	},


	// katakana -> hiragana conversion tables
	ch:[0x3092,0x3041,0x3043,0x3045,0x3047,0x3049,0x3083,0x3085,0x3087,0x3063,0x30FC,0x3042,0x3044,0x3046,
		0x3048,0x304A,0x304B,0x304D,0x304F,0x3051,0x3053,0x3055,0x3057,0x3059,0x305B,0x305D,0x305F,0x3061,
		0x3064,0x3066,0x3068,0x306A,0x306B,0x306C,0x306D,0x306E,0x306F,0x3072,0x3075,0x3078,0x307B,0x307E,
		0x307F,0x3080,0x3081,0x3082,0x3084,0x3086,0x3088,0x3089,0x308A,0x308B,0x308C,0x308D,0x308F,0x3093],
	cv:[0x30F4,0xFF74,0xFF75,0x304C,0x304E,0x3050,0x3052,0x3054,0x3056,0x3058,0x305A,0x305C,0x305E,0x3060,
		0x3062,0x3065,0x3067,0x3069,0xFF85,0xFF86,0xFF87,0xFF88,0xFF89,0x3070,0x3073,0x3076,0x3079,0x307C],
	cs:[0x3071,0x3074,0x3077,0x307A,0x307D],

	_wordSearch: function(word, dic, max) {
		if (!this.ready) this.init();

		// half & full-width katakana to hiragana conversion
		// note: katakana vu is never converted to hiragana

		var trueLen = [0];
		var p = 0;
		var r = '';
		for (let i = 0; i < word.length; ++i) {
			let u = word.charCodeAt(i);
			let v = u;

			if (u <= 0x3000) break;

			// full-width katakana to hiragana
			if ((u >= 0x30A1) && (u <= 0x30F3)) {
				u -= 0x60;
			}
			// half-width katakana to hiragana
			else if ((u >= 0xFF66) && (u <= 0xFF9D)) {
				u = this.ch[u - 0xFF66];
			}
			// voiced (used in half-width katakana) to hiragana
			else if (u == 0xFF9E) {
				if ((p >= 0xFF73) && (p <= 0xFF8E)) {
					r = r.substr(0, r.length - 1);
					u = this.cv[p - 0xFF73];
				}
			}
			// semi-voiced (used in half-width katakana) to hiragana
			else if (u == 0xFF9F) {
				if ((p >= 0xFF8A) && (p <= 0xFF8E)) {
					r = r.substr(0, r.length - 1);
					u = this.cs[p - 0xFF8A];
				}
			}
			// ignore J~
			else if (u == 0xFF5E) {
				p = 0;
				continue;
			}

			r += String.fromCharCode(u);
			trueLen[r.length] = i + 1;	// need to keep real length because of the half-width semi/voiced conversion
			p = v;
		}
		word = r;


		var result = { data: [] };
		var maxTrim;

		if (dic.isName) {
			maxTrim = rcxConfig.namax;
			result.names = 1;
		}
		else {
			maxTrim = rcxConfig.wmax;
		}


		if (max != null) maxTrim = max;

		var have = [];
		var count = 0;
		var maxLen = 0;

		while (word.length > 0) {
			var showInf = (count != 0);
			var variants = dic.isName ? [{word: word, type: 0xFF, reason: null}] : this.deinflect.go(word);
			for (var i = 0; i < variants.length; i++) {
				var v = variants[i];
				var entries = dic.findWord(v.word);
				for (var j = 0; j < entries.length; ++j) {
					var dentry = entries[j];
					if (have[dentry]) continue;

					var ok = true;
					if ((dic.hasType) && (i > 0)) {
						// i > 0 a de-inflected word

						var gloss = dentry.split(/[,()]/);
						var y = v.type;
						var z;
						for (z = gloss.length - 1; z >= 0; --z) {
							var g = gloss[z];
							if ((y & 1) && (g == 'v1')) break;
							if ((y & 4) && (g == 'adj-i')) break;
							if ((y & 2) && (g.substr(0, 2) == 'v5')) break;
							if ((y & 16) && (g.substr(0, 3) == 'vs-')) break;
							if ((y & 8) && (g == 'vk')) break;
						}
						ok = (z != -1);
					}
					if ((ok) && (dic.hasType) && (rcxConfig.hidex)) {
						if (dentry.match(/\/\([^\)]*\bX\b.*?\)/)) ok = false;
					}
					if (ok) {
						if (count >= maxTrim) {
							result.more = 1;
							break;
						}

						have[dentry] = 1;
						++count;
						if (maxLen == 0) maxLen = trueLen[word.length];

						var r = null;
						if (v.reason) {
							if (showInf) r = '&lt; ' + v.reason + ' &lt; ' + word;
								else r = '&lt; ' + v.reason;
						}
						result.data.push([dentry, r]);
					}
				}	// for j < entries.length
				if (count >= maxTrim) break;
			}	// for i < variants.length
			if (count >= maxTrim) break;
			word = word.substr(0, word.length - 1);
		}	// while word.length > 0

		if (result.data.length == 0) return null;

		result.matchLen = maxLen;
		return result;
	},

	wordSearch: function(word, noKanji) {
		this.searchSkipped = 0;
		let ds = this.selected;
		do {
			let dic = this.dicList[ds];
			if ((!noKanji) || (!dic.isKanji)) {
				let e;
				if (dic.isKanji) e = this.kanjiSearch(word.charAt(0));
					else e = this._wordSearch(word, dic, null);
				if (e) {
					if (ds != 0) e.title = dic.name;
					return e;
				}
			}
			this.searchSkipped++;
			ds = (ds + 1) % this.dicList.length;
		} while (ds != this.selected);
		return null;
	},

	translate: function(text) {
		var result = { data: [], textLen: text.length };
		while (text.length > 0) {
			var e = null;
			var ds = this.selected;
			do {
				if (!this.dicList[ds].isKanji) {
					e = this._wordSearch(text, this.dicList[ds], 1);
					if (e != null) break;
				}
				ds = (ds + 1) % this.dicList.length;
			} while (ds != this.selected);

			if (e != null) {
				if (result.data.length >= rcxConfig.wmax) {
					result.more = 1;
					break;
				}
				result.data.push(e.data[0]);
				text = text.substr(e.matchLen);
			}
			else {
				text = text.substr(1);
			}
		}
		this.searchSkipped = (this.selected == this.kanjiPos) ? 1 : 0;
		if (result.data.length == 0) return null;
		result.textLen -= text.length;
		return result;
	},

	textSearch: function(text) {
		this.searchSkipped = 0;
		if (!this.ready) this.init();
		text = text.toLowerCase();
		let ds = this.selected;
		do {
			let dic = this.dicList[ds];
			if (!dic.isKanji) {
				let result = { data: [], reason: [], kanji: 0, more: 0, names: dic.isName };

				let r = dic.findText(text);

				// try priorizing
				let list = [];
				let sW = /[\sW]/;
				let slashText = '/' + text + '/';
				for (let i = 0; i < r.length; ++i) {
					let t = r[i].replace(/\(.+?\)/g, '').toLowerCase();

					// closer to the beginning = better
					let d = t.indexOf(text);
					if (d >= 0) {
						// the exact text within an entry = best
						if (t.replace(/\s+/g, '').indexOf(slashText) != -1) {
							d -= 100;
						}
						// a word within an entry = better
						else if (((d == 0) || (sW.test(t.substr(d - 1, 1)))) &&
								(((d + text.length) >= t.length) || (sW.test(t.substr(d + text.length, 1))))) {
							d -= 50;
						}
					}
					else d = 9999;
					list.push({ rank: d, text: r[i] });
				}

				let max = dic.isName ? rcxConfig.namax : rcxConfig.wmax;
				list.sort(function(a, b) { return a.rank - b.rank });
				for (let i = 0; i < list.length; ++i) {
					if (result.data.length >= max) {
						result.more = 1;
						break;
					}
					//	result.data.push([list[i].text + '[' + list[i].rank + ']/', null]);
					result.data.push([list[i].text, null]);
				}

				/*
				let j = (list.length > 100) ? 100 : list.length;
				for (let i = 0; i < j; ++i) {
					rcxDebug.echo(i + ': [' + list[i].rank + '] ' + list[i].text);
				}*/

				/*
				for (let i = 0; i < r.length; ++i) {
					if (result.data.length >= max) {
						result.more = 1;
						break;
					}
					result.data.push([r[i], null]);
				}
				*/

				if (result.data.length) {
					if (ds != 0) result.title = dic.name;
					return result;
				}
			}
			this.searchSkipped++;
			ds = (ds + 1) % this.dicList.length;
		} while (ds != this.selected);
		return null;
	},

	// @@@ todo later...
	kanjiSearch: function(kanji) {
		const hex = '0123456789ABCDEF';
		var kde;
		var result;
		var a, b;
		var i;

		i = kanji.charCodeAt(0);
		if (i < 0x3000) return null;

		if (!this.kanjiData) {
			this.kanjiData = rcxFile.read((typeof(rcxKanjiURI) == 'string') ? rcxKanjiURI : 'chrome://rikaichan/content/kanji.dat');
		}

		kde = this.find(this.kanjiData, kanji);
		if (!kde) return null;

		a = kde.split('|');
		if (a.length != 6) return null;

		result = { };
		result.kanji = a[0];

		result.misc = {};
		result.misc['U'] = hex[(i >>> 12) & 15] + hex[(i >>> 8) & 15] + hex[(i >>> 4) & 15] + hex[i & 15];

		b = a[1].split(' ');
		for (i = 0; i < b.length; ++i) {
			if (b[i].match(/^([A-Z]+)(.*)/)) {
				if (!result.misc[RegExp.$1]) result.misc[RegExp.$1] = RegExp.$2;
					else result.misc[RegExp.$1] += ' ' + RegExp.$2;
			}
		}

		result.onkun = a[2].replace(/\s+/g, '\u3001 ');
		result.nanori = a[3].replace(/\s+/g, '\u3001 ');
		result.bushumei = a[4].replace(/\s+/g, '\u3001 ');
		result.eigo = a[5];

		return result;
	},

	// ---

	numList: [
/*
		'C', 	'Classical Radical',
		'DR',	'Father Joseph De Roo Index',
		'DO',	'P.G. O\'Neill Index',
		'O', 	'P.G. O\'Neill Japanese Names Index',
		'Q', 	'Four Corner Code',
		'MN',	'Morohashi Daikanwajiten Index',
		'MP',	'Morohashi Daikanwajiten Volume/Page',
		'K',	'Gakken Kanji Dictionary Index',
		'W',	'Korean Reading',
*/
		'H',	'Halpern',
		'L',	'Heisig',
		'E',	'Henshall',
		'DK',	'Kanji Learners Dictionary',
		'N',	'Nelson',
		'V',	'New Nelson',
		'Y',	'PinYin',
		'P',	'Skip Pattern',
		'IN',	'Tuttle Kanji &amp; Kana',
		'I',	'Tuttle Kanji Dictionary',
		'U',	'Unicode'
	],

	makeHtml: function(entry) {
		var e;
		var b;
		var c, s, t;
		var i, j, n;

		if (entry == null) return '';

		if (!this.ready) this.init();
		if (!this.radData) this.radData = rcxFile.readArray('chrome://rikaichan/content/radicals.dat');

		b = [];

		if (entry.kanji) {
			var yomi;
			var box;
			var bn;
			var k;
			var nums;

			yomi = entry.onkun.replace(/\.([^\u3001]+)/g, '<span class="k-yomi-hi">$1</span>');
			if (entry.nanori.length) {
				yomi += '<br/><span class="k-yomi-ti">\u540D\u4E57\u308A</span> ' + entry.nanori;
			}
			if (entry.bushumei.length) {
				yomi += '<br/><span class="k-yomi-ti">\u90E8\u9996\u540D</span> ' + entry.bushumei;
			}

			bn = entry.misc['B'] - 1;
			k = entry.misc['G'];
			switch (k) {
			case 8:
				k = 'general<br/>use';
				break;
			case 9:
				k = 'name<br/>use';
				break;
			default:
				k = isNaN(k) ? '-' : ('grade<br/>' + k);
				break;
			}
			box = '<table class="k-abox-tb"><tr>' +
				'<td class="k-abox-r">radical<br/>' + this.radData[bn].charAt(0) + ' ' + (bn + 1) + '</td>' +
				'<td class="k-abox-g">' + k + '</td>' +
				'</tr><tr>' +
				'<td class="k-abox-f">freq<br/>' + (entry.misc['F'] ? entry.misc['F'] : '-') + '</td>' +
				'<td class="k-abox-s">strokes<br/>' + entry.misc['S'] + '</td>' +
				'</tr></table>';
			if (this.kanjiShown['COMP']) {
				k = this.radData[bn].split('\t');
				box += '<table class="k-bbox-tb">' +
						'<tr><td class="k-bbox-1a">' + k[0] + '</td>' +
						'<td class="k-bbox-1b">' + k[2] + '</td>' +
						'<td class="k-bbox-1b">' + k[3] + '</td></tr>';
				j = 1;
				for (i = 0; i < this.radData.length; ++i) {
					s = this.radData[i];
					if ((bn != i) && (s.indexOf(entry.kanji) != -1)) {
						k = s.split('\t');
						c = ' class="k-bbox-' + (j ^= 1);
						box += '<tr><td' + c + 'a">' + k[0] + '</td>' +
								'<td' + c + 'b">' + k[2] + '</td>' +
								'<td' + c + 'b">' + k[3] + '</td></tr>';
					}
				}
				box += '</table>';
			}

			nums = '';
			j = 0;

			for (i = 0; i < this.numList.length; i += 2) {
				c = this.numList[i];
				if (this.kanjiShown[c]) {
					s = entry.misc[c];
					c = ' class="k-mix-td' + (j ^= 1) + '"';
					nums += '<tr><td' + c + '>' + this.numList[i + 1] + '</td><td' + c + '>' + (s ? s : '-') + '</td></tr>';
				}
			}
			if (nums.length) nums = '<table class="k-mix-tb">' + nums + '</table>';

			b.push('<table class="k-main-tb"><tr><td valign="top">');
			b.push(box);
			b.push('<span class="k-kanji">' + entry.kanji + '</span><br/>');
			if (!rcxConfig.hidedef) b.push('<div class="k-eigo">' + entry.eigo + '</div>');
			b.push('<div class="k-yomi">' + yomi + '</div>');
			b.push('</td></tr><tr><td>' + nums + '</td></tr></table>');
			return b.join('');
		}

		s = t = '';

		if (entry.names) {
			c = [];

			b.push('<div class="w-title">Names Dictionary</div><table class="w-na-tb"><tr><td>');
			for (i = 0; i < entry.data.length; ++i) {
				e = entry.data[i][0].match(/^(.+?)\s+(?:\[(.*?)\])?\s*\/([\S\s]+)\//);
				if (!e) continue;

				if (s != e[3]) {
					c.push(t);
					t = '';
				}

				if (e[2]) c.push('<span class="w-kanji">' + e[1] + '</span> &#32; <span class="w-kana">' + e[2] + '</span><br/> ');
					else c.push('<span class="w-kana">' + e[1] + '</span><br/> ');

				s = e[3];
				if (rcxConfig.hidedef) t = '';
					else t = '<span class="w-def">' + s.replace(/\//g, '; ').replace(/\n/g, '<br/>') + '</span><br/>';
			}
			c.push(t);
			if (c.length > 4) {
				n = (c.length >> 1) + 1;
				b.push(c.slice(0, n + 1).join(''));

				t = c[n];
				c = c.slice(n, c.length);
				for (i = 0; i < c.length; ++i) {
					if (c[i].indexOf('w-def') != -1) {
						if (t != c[i]) b.push(c[i]);
						if (i == 0) c.shift();
						break;
					}
				}

				b.push('</td><td>');
				b.push(c.join(''));
			}
			else {
				b.push(c.join(''));
			}
			if (entry.more) b.push('...<br/>');
			b.push('</td></tr></table>');
		}
		else {
			if (entry.title) {
				b.push('<div class="w-title">' + entry.title + '</div>');
			}

			var pK = '';
			var k;

			for (i = 0; i < entry.data.length; ++i) {
				e = entry.data[i][0].match(/^(.+?)\s+(?:\[(.*?)\])?\s*\/([\S\s]+)\//);
				if (!e) continue;

				/*
					e[1] = kanji/kana
					e[2] = kana
					e[3] = definition
				*/
				if (s != e[3]) {
					b.push(t);
					pK = k = '';
				}
				else {
					k = t.length ? '<br/>' : '';
				}

				if (e[2]) {
					if (pK == e[1]) k = '\u3001 <span class="w-kana">' + e[2] + '</span>';
						else k += '<span class="w-kanji">' + e[1] + '</span> &#32; <span class="w-kana">' + e[2] + '</span>';
					pK = e[1];
				}
				else {
					k += '<span class="w-kana">' + e[1] + '</span>';
					pK = '';
				}
				b.push(k);

				if (entry.data[i][1]) b.push(' <span class="w-conj">(' + entry.data[i][1] + ')</span>');

				s = e[3];
				if (rcxConfig.hidedef) {
					t = '<br/>';
				}
				else {
					t = s.replace(/\//g, '; ');
					if (!rcxConfig.wpos) t = t.replace(/^\([^)]+\)\s*/, '');
					if (!rcxConfig.wpop) t = t.replace('; (P)', '');
					t = t.replace(/\n/g, '<br/>');
					t = '<br/><span class="w-def">' + t + '</span><br/>';
				}
			}
			b.push(t);
			if (entry.more) b.push('...<br/>');
		}

		return b.join('');
	},

	makeText: function(entry, max) {
		var e;
		var b;
		var i, j;
		var t;

		if (entry == null) return '';
		if (!this.ready) this.init();

		b = [];

		if (entry.kanji) {
			b.push(entry.kanji + '\n');
			b.push((entry.eigo.length ? entry.eigo : '-') + '\n');

			b.push(entry.onkun.replace(/\.([^\u3001]+)/g, '\uFF08$1\uFF09') + '\n');
			if (entry.nanori.length) {
				b.push('\u540D\u4E57\u308A\t' + entry.nanori + '\n');
			}
			if (entry.bushumei.length) {
				b.push('\u90E8\u9996\u540D\t' + entry.bushumei + '\n');
			}

			for (i = 0; i < this.numList.length; i += 2) {
				e = this.numList[i];
				if (this.kanjiShown[e]) {
					j = entry.misc[e];
					b.push(this.numList[i + 1].replace('&amp;', '&') + '\t' + (j ? j : '-') + '\n');
				}
			}
		}
		else {
			if (max > entry.data.length) max = entry.data.length;
			for (i = 0; i < max; ++i) {
				e = entry.data[i][0].match(/^(.+?)\s+(?:\[(.*?)\])?\s*\/(.+)\//);
				if (!e) continue;

				if (e[2]) {
					b.push(e[1] + '\t' + e[2]);
				}
				else {
					b.push(e[1]);
				}

				t = e[3].replace(/\//g, '; ');
				if (!rcxConfig.wpos) t = t.replace(/^\([^)]+\)\s*/, '');
				if (!rcxConfig.wpop) t = t.replace('; (P)', '');
				b.push('\t' + t + '\n');
			}
		}
		return b.join('');
	},

	// ---

	find: function(data, text) {
		const tlen = text.length;
		var beg = 0;
		var end = data.length - 1;
		var i;
		var mi;
		var mis;

		while (beg < end) {
			mi = (beg + end) >> 1;
			i = data.lastIndexOf('\n', mi) + 1;

			mis = data.substr(i, tlen);
			if (text < mis) end = i - 1;
				else if (text > mis) beg = data.indexOf('\n', mi + 1) + 1;
					else return data.substring(i, data.indexOf('\n', mi + 1));
		}
		return null;
	}
};

var rcxFile = {
	read: function(uri) {
		var inp = Components.classes['@mozilla.org/network/io-service;1']
				.getService(Components.interfaces.nsIIOService)
				.newChannel(uri, null, null)
				.open();

		var is = Components.classes['@mozilla.org/intl/converter-input-stream;1']
					.createInstance(Components.interfaces.nsIConverterInputStream);
		is.init(inp, 'UTF-8', 4 * 1024 * 1024,
			Components.interfaces.nsIConverterInputStream.DEFAULT_REPLACEMENT_CHARACTER);

		var buffer = '';
		var s = {};
		while (is.readString(-1, s) > 0) {
			buffer += s.value;
		}
		is.close();

		return buffer;
	},

	readArray: function(name) {
		var a = this.read(name).split('\n');
		while ((a.length > 0) && (a[a.length - 1].length == 0)) a.pop();
		return a;
	}
};

function RcxDb(name)
{
	this.open = function() {
		var f;

		if (name.match(/(.+)\|(.+)/)) {
			let id = RegExp.$1;
			let nm = RegExp.$2;
			try {
				f = Components.classes['@mozilla.org/extensions/manager;1']
					.getService(Components.interfaces.nsIExtensionManager)
					.getInstallLocation(id).getItemFile(id, nm);
			}
			catch (ex) {
				if ((rcxData.dicPath) && (rcxData.dicPath[id])) {
					f = Components.classes['@mozilla.org/file/local;1']
						.createInstance(Components.interfaces.nsILocalFile);
					f.initWithPath(rcxData.dicPath[id]);
					f.append(nm);
				}

				if (!f) throw 'Could not find or open ' + id + '/' + nm;
			}
		}
		else {
			f = Components.classes['@mozilla.org/file/local;1']
				.createInstance(Components.interfaces.nsILocalFile);
			f.initWithPath(name);
		}

		// The files may get installed as read-only, breaking
		// index creation. Try changing the file permission.
		if (!f.isWritable()) f.permissions |= 0x180;	// 0x180=0600 strict mode doesn't like octals

		this.db = Components.classes['@mozilla.org/storage/service;1']
			.getService(Components.interfaces.mozIStorageService)
			.openDatabase(f);
	};

	this.close = function() {
		if (this.db) {
			try {
				this.db.close();
			}
			catch (ex) {
			}
			this.db = null;
		}
	};

	this.exec = function(stm) {
		var rows = [];
		if (!this.db) this.open();
		var st = this.db.createStatement(stm);
		for (var i = arguments.length - 1; i > 0; --i) {
			if (arguments[i] != null) st.bindUTF8StringParameter(i - 1, arguments[i]);
		}
		while (st.executeStep()) {
			var r = [];
			for (var i = st.columnCount - 1; i >= 0; --i) {
				r[st.getColumnName(i)] = st.getUTF8String(i);
			}
			rows.push(r);
		}
		return rows;
	};

	this.indexExists = function(index) {
		if (!this.db) this.open();
		return this.db.indexExists(index);
	};

	this.beginTransaction = function() {
		if (!this.db) this.open();
		this.db.beginTransaction();
	};

	this.commitTransaction = function() {
		this.db.commitTransaction();
	};

	this.rollbackTransaction = function() {
		this.db.rollbackTransaction();
	};

	return this;
}

function RcxDic(dic)
{
	this.name = dic.name;
	this.version = dic.version;
	this.id = dic.id;
	this.hasType = dic.hasType;
	this.isName = dic.isName;

	this.open = function() {
		try {
			if (this.rdb) return;

			this.rdb = new RcxDb(this.id + '|dict.sqlite');
			this.rdb.open();
			this.checkIndex('kanji');
			this.checkIndex('kana');
		}
		catch (ex) {
			this.close();
			throw ex;
		}
	};

	this.close = function() {
		if (this.rdb) {
			try {
				this.rdb.close();
			}
			catch (ex) {
			}
			this.rdb = null;
		}
	};

	this.checkIndex = function(name) {
		var ix = 'ix_' + name;
		if (this.rdb.indexExists(ix)) return;

		if (!rcxData.indexCreateNotice) {
			alert('A dictionary index needs to be created. This may take a while on some systems. Click OK to start.');
			rcxData.indexCreateNotice = true;
		}

		this.rdb.exec('CREATE INDEX ' + ix + ' ON dict (' + name + ' ASC)');
	};

	this.find = function(query, arg1) {
		if (!this.rdb) this.open();
		var r = this.rdb.exec(query, arg1);
		var entries = [];
		for (var i = 0; i < r.length; ++i) {
			var x = r[i];
			if (!x.entry.length) continue;
			// rcx currently expects an edict-like format
			if (x.entry[x.entry.length - 1] == '/') entries.push(x.entry);
				else entries.push((x.kanji ? (x.kanji + ' [' + x.kana + ']') : x.kana) + ' /' + x.entry + '/');
		}
		return entries;
	};

	this.findWord = function(word) {
		return this.find('SELECT * FROM dict WHERE kanji=?1 OR kana=?1 LIMIT 100', word);
	};

	this.findText = function(text) {
		return this.find('SELECT * FROM dict WHERE entry LIKE ?1 LIMIT 300', '%' + text + '%');
	};

	return this;
};
