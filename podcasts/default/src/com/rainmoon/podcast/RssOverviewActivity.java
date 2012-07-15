package com.rainmoon.podcast;

import java.io.File;
import java.io.FilenameFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rainmoon.podcast.preference.PreferencesActivityCompatability;
import com.rainmoon.podcast.preference.PreferencesActivityV11;
import com.rainmoon.podcast.preference.PrefsFragment;
import com.rainmoon.podcast.provider.FeedData;
import com.rainmoon.podcast.provider.OPML;

public class RssOverviewActivity extends Activity {

  private static final int DIALOG_ERROR_FEEDIMPORT = 3;

  private static final int DIALOG_ERROR_FEEDEXPORT = 4;

  private static final int DIALOG_ERROR_INVALIDIMPORTFILE = 5;

  private static final int DIALOG_ERROR_EXTERNALSTORAGENOTAVAILABLE = 6;

  private static final int DIALOG_ABOUT = 7;

  private static final Uri CANGELOG_URI = Uri
      .parse("http://code.google.com/p/sparserss/wiki/Changelog");


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.feedoverview, menu);
    return true;
  }

  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menu_addfeed: {
      startActivity(new Intent(Intent.ACTION_INSERT)
          .setData(FeedData.FeedColumns.CONTENT_URI));
      break;
    }
    case R.id.menu_refresh: {
      new Thread() {
        public void run() {
          sendBroadcast(new Intent(Strings.ACTION_REFRESHFEEDS).putExtra(
              Strings.SETTINGS_OVERRIDEWIFIONLY,
              PreferenceManager.getDefaultSharedPreferences(RSSOverview.this)
                  .getBoolean(Strings.SETTINGS_OVERRIDEWIFIONLY, false)));
        }
      }.start();
      break;
    }

    case R.id.menu_settings: {
      if (Build.VERSION.SDK_INT < 11) {
        startActivity(new Intent(this, PreferencesActivityCompatability.class));
      } else {
        Intent intent = new Intent(this, PreferencesActivityV11.class);
        // do not show header because currently there is only 1
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
            PrefsFragment.class.getName());
        startActivity(intent);
      }

      break;
    }
    case R.id.menu_allread: {
      new Thread() {
        public void run() {
          if (getContentResolver().update(
              FeedData.EntryColumns.CONTENT_URI,
              getReadContentValues(),
              new StringBuilder(FeedData.EntryColumns.READDATE).append(
                  Strings.DB_ISNULL).toString(), null) > 0) {
            getContentResolver().notifyChange(FeedData.FeedColumns.CONTENT_URI,
                null);
          }
        }
      }.start();
      break;
    }
    case R.id.menu_about: {
      showDialog(DIALOG_ABOUT);
      break;
    }
    case R.id.menu_import: {
      if (Environment.getExternalStorageState().equals(
          Environment.MEDIA_MOUNTED)
          || Environment.getExternalStorageState().equals(
              Environment.MEDIA_MOUNTED_READ_ONLY)) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.select_file);

        try {
          final String[] fileNames = Environment.getExternalStorageDirectory()
              .list(new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                  return new File(dir, filename).isFile();
                }
              });
          builder.setItems(fileNames, new DialogInterface.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(DialogInterface dialog, int which) {
              try {
                OPML.importFromFile(
                    new StringBuilder(Environment.getExternalStorageDirectory()
                        .toString()).append(File.separator)
                        .append(fileNames[which]).toString(), RSSOverview.this);
              } catch (Exception e) {
                showDialog(DIALOG_ERROR_FEEDIMPORT);
              }
            }
          });
          builder.show();
        } catch (Exception e) {
          showDialog(DIALOG_ERROR_FEEDIMPORT);
        }
      } else {
        showDialog(DIALOG_ERROR_EXTERNALSTORAGENOTAVAILABLE);
      }

      break;
    }
    case R.id.menu_export: {
      if (Environment.getExternalStorageState().equals(
          Environment.MEDIA_MOUNTED)
          || Environment.getExternalStorageState().equals(
              Environment.MEDIA_MOUNTED_READ_ONLY)) {
        try {
          String filename = new StringBuilder(Environment
              .getExternalStorageDirectory().toString()).append("/sparse_rss_")
              .append(System.currentTimeMillis()).append(".opml").toString();

          OPML.exportToFile(filename, this);
          Toast.makeText(this,
              String.format(getString(R.string.message_exportedto), filename),
              Toast.LENGTH_LONG).show();
        } catch (Exception e) {
          showDialog(DIALOG_ERROR_FEEDEXPORT);
        }
      } else {
        showDialog(DIALOG_ERROR_EXTERNALSTORAGENOTAVAILABLE);
      }
      break;
    }
    case R.id.menu_deleteread: {
      FeedData.deletePicturesOfFeedAsync(this,
          FeedData.EntryColumns.CONTENT_URI, Strings.READDATE_GREATERZERO);
      getContentResolver().delete(FeedData.EntryColumns.CONTENT_URI,
          Strings.READDATE_GREATERZERO, null);
//      ((RSSOverviewListAdapter) getListAdapter()).notifyDataSetChanged();
      break;
    }
    case R.id.menu_deleteallentries: {
      showDeleteAllEntriesQuestion(this, FeedData.EntryColumns.CONTENT_URI);
      break;
    }
    }
    return true;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    Dialog dialog;

    switch (id) {
    case DIALOG_ERROR_FEEDIMPORT: {
      dialog = createErrorDialog(R.string.error_feedimport);
      break;
    }
    case DIALOG_ERROR_FEEDEXPORT: {
      dialog = createErrorDialog(R.string.error_feedexport);
      break;
    }
    case DIALOG_ERROR_INVALIDIMPORTFILE: {
      dialog = createErrorDialog(R.string.error_invalidimportfile);
      break;
    }
    case DIALOG_ERROR_EXTERNALSTORAGENOTAVAILABLE: {
      dialog = createErrorDialog(R.string.error_externalstoragenotavailable);
      break;
    }
    case DIALOG_ABOUT: {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setIcon(android.R.drawable.ic_dialog_info);
      builder.setTitle(R.string.menu_about);
      MainTabActivity.INSTANCE.setupLicenseText(builder);
      builder.setPositiveButton(android.R.string.ok,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
            }
          });
      builder.setNeutralButton(R.string.changelog,
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              startActivity(new Intent(Intent.ACTION_VIEW, CANGELOG_URI));
            }
          });
      return builder.create();
    }
    default:
      dialog = null;
    }
    return dialog;
  }

  private Dialog createErrorDialog(int messageId) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage(messageId);
    builder.setTitle(R.string.error);
    builder.setIcon(android.R.drawable.ic_dialog_alert);
    builder.setPositiveButton(android.R.string.ok, null);
    return builder.create();
  }

  private static void showDeleteAllEntriesQuestion(final Context context,
      final Uri uri) {
    Builder builder = new AlertDialog.Builder(context);

    builder.setIcon(android.R.drawable.ic_dialog_alert);
    builder.setTitle(R.string.contextmenu_deleteallentries);
    builder.setMessage(R.string.question_areyousure);
    builder.setPositiveButton(android.R.string.yes,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            new Thread() {
              public void run() {
                FeedData.deletePicturesOfFeed(context, uri,
                    Strings.DB_EXCUDEFAVORITE);
                if (context.getContentResolver().delete(uri,
                    Strings.DB_EXCUDEFAVORITE, null) > 0) {
                  context.getContentResolver().notifyChange(
                      FeedData.FeedColumns.CONTENT_URI, null);
                }
              }
            }.start();
          }
        });
    builder.setNegativeButton(android.R.string.no, null);
    builder.show();
  }
  
}
