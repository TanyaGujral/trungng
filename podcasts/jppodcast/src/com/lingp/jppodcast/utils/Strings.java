/**
 * Sparse rss
 * 
 * Copyright (c) 2010-2012 Stefan Handschuh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.lingp.jppodcast.utils;

import com.lingp.jppodcast.provider.FeedData;

public final class Strings {
  //public static final String PACKAGE = "com.lingp.jppodcast";
  public static final String SEEK_CHANGE = "com.lingp.basepodcast.SEEK_CHANGE";
  public static final String PLAY_URL = "com.lingp.basepodcast.PLAY_URL";
  public static final String ACTION_TOGGLE_PLAYBACK = "com.lingp.basepodcast.musicplayer.action.TOGGLE_PLAYBACK";
  public static final String ACTION_PLAY = "com.lingp.basepodcast.musicplayer.action.PLAY";
  public static final String ACTION_PAUSE = "com.lingp.basepodcast.musicplayer.action.PAUSE";
  public static final String ACTION_STOP = "com.lingp.basepodcast.musicplayer.action.STOP";
  public static final String ACTION_SEEK = "com.lingp.basepodcast.musicplayer.action.SEEK";
  public static final String ACTION_REFRESHFEEDS = "com.lingp.basepodcast.REFRESH";
  public static final String ACTION_RESTART = "com.lingp.basepodcast.RESTART";

  public static final String APPLICATION_SHARED_PREFERENCES = "SHARED_PREFERENCES";
  public static final String INTENT_DATABASE_READY = "database.ready";
  public static final String SETTINGS_REFRESHINTERVAL = "refresh.interval";
  public static final String SETTINGS_NOTIFICATIONSENABLED = "notifications.enabled";
  public static final String SETTINGS_REFRESHENABLED = "refresh.enabled";
  public static final String SETTINGS_REFRESHONPENENABLED = "refreshonopen.enabled";
  public static final String SETTINGS_NOTIFICATIONSRINGTONE = "notifications.ringtone";
  public static final String SETTINGS_NOTIFICATIONSVIBRATE = "notifications.vibrate";
  public static final String SETTINGS_PRIORITIZE = "contentpresentation.prioritize";
  public static final String SETTINGS_FETCHPICTURES = "pictures.fetch";
  public static final String SETTINGS_KEEPTIME = "keeptime";
  public static final String SETTINGS_BLACKTHEME = "blacktheme";
  public static final String SETTINGS_FONTSIZE = "fontsize";

  public static final String SETTINGS_DISABLEPICTURES = "pictures.disable";
  public static final String SETTINGS_OVERRIDEWIFIONLY = "overridewifionly";
  public static final String SETTINGS_ENCLOSUREWARNINGSENABLED = "enclosurewarnings.enabled";

  public static final String PREFERENCE_CURRENT_URI = "pref.current_uri";
  public static final String PREFERENCE_DATABASE_READY = "pref.database_ready";

  public static final String FEEDID = "feedid";

  public static final String EXTRA_SHOWREAD = "show_read";
  public static final String EXTRA_SHOWFEEDINFO = "show_feedinfo";
  public static final String EXTRA_AUTORELOAD = "autoreload";

  public static final String DB_ISNULL = " IS NULL";
  public static final String DB_DESC = " DESC";
  public static final String DB_ARG = "=?";
  public static final String DB_AND = " AND ";

  public static final String DB_EXCUDEFAVORITE = new StringBuilder(FeedData.ItemColumns.FAVORITE)
      .append(Strings.DB_ISNULL).append(" OR ").append(FeedData.ItemColumns.FAVORITE).append("=0")
      .toString();

  public static final String EMPTY = "";

  public static final String HTTP = "http://";

  public static final String HTTPS = "https://";

  public static final String _HTTP = "http";

  public static final String _HTTPS = "https";

  public static final String PROTOCOL_SEPARATOR = "://";

  public static final String FILE_FAVICON = "/favicon.ico";

  public static final String SPACE = " ";

  public static final String TWOSPACE = "  ";

  public static final String HTML_TAG_REGEX = "<(.|\n)*?>";

  public static final String FILEURL = "file://";

  public static final String IMAGEFILE_IDSEPARATOR = "__";

  public static final String IMAGEID_REPLACEMENT = "##ID##";

  public static final String DEFAULTPROXYPORT = "8080";

  public static final String URL_SPACE = "%20";

  public static final String HTML_SPAN_REGEX = "<[/]?[ ]?span(.|\n)*?>";

  public static final String HTML_IMG_REGEX = "<[/]?[ ]?img(.|\n)*?>";

  public static final String ONE = "1";

  public static final Object THREENEWLINES = "\n\n\n";

  public static final String PREFERENCE_LICENSEACCEPTED = "license.accepted";

  public static final String HTML_LT = "&lt;";

  public static final String HTML_GT = "&gt;";

  public static final String LT = "<";

  public static final String GT = ">";

  public static final String TRUE = "true";

  public static final String FALSE = "false";

  public static final String READDATE_GREATERZERO = FeedData.ItemColumns.READDATE + ">0";

  public static final String COUNT = "count";

  public static final String ENCLOSURE_SEPARATOR = "[@]"; // exactly three
                                                          // characters!

  public static final String QUESTIONMARKS = "'??'";

  public static final String HTML_QUOT = "&quot;";

  public static final String QUOT = "\"";

  public static final String HTML_APOSTROPHE = "&#39;";

  public static final String APOSTROPHE = "'";

  public static final String AMP = "&";

  public static final String AMP_SG = "&amp;";

  public static final String SLASH = "/";
  public static final String LISTEN_START_TIME = "listen.start.time";
  public static final String LISTEN_TOTAL_ITEMS = "listen.total.items";
  public static final String LISTEN_TOTAL_MILLIS = "listen.total.millis";
  public static final String LISTEN_MONTH_ITEMS = "listen.month.items";
  public static final String LISTEN_MONTH_MILLIS = "listen.month.millis";
  public static final String LISTEN_WEEK_ITEMS = "listen.week.items";
  public static final String LISTEN_WEEK_MILLIS = "listen.week.millis";
  public static final String WEEK_RESETED = "listen.week.reseted";
  public static final String MONTH_RESETED = "listen.month.reseted";
  public static final String NO_CONTENT_MSG = "no.content.msg";

}
