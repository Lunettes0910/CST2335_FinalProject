package com.example.cst2335_finalproject;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

public class MWDictMainActivity extends AppCompatActivity {
    protected Toolbar myToolbar;
    protected ProgressBar loadingBar;

    final static String APP_NAME = "M-W Dictionary API";
    final static String mwDictUrl = "https://www.dictionaryapi.com/api/v1/references/sd3/xml/";
    final static String mwDictKey = "?key=4556541c-b8ed-4674-9620-b6cba447184f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mw_dict_main_activity);

        myToolbar = findViewById(R.id.mwDictToolbar);
        setSupportActionBar(myToolbar);

        EditText enteredWord = findViewById(R.id.mwDictWordInput);

        Button searchWordButton = findViewById(R.id.mwDictSearchButton);
        searchWordButton.setOnClickListener( b -> {
            String searchWord = enteredWord.getText().toString().toLowerCase();

            if (searchWord.equals("")) {
                Log.e(APP_NAME, "Empty word: ".concat(searchWord));
                onInvalidWord();

            } else {
                try {
                    MWDictForecastQuery networkThread = new MWDictForecastQuery();
                    String wordUrl = mwDictUrl.concat(URLEncoder.encode(searchWord, "UTF-8")).concat(mwDictKey);
                    networkThread.execute(wordUrl);

                    loadingBar = findViewById(R.id.mwDictConnectionBar);
                    loadingBar.setVisibility(View.VISIBLE);
                } catch (UnsupportedEncodingException notEnglishWord) {
                    Log.e(APP_NAME, "Foreign word");
                    onInvalidWord();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu items in ActionBar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mw_dict_menu_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mw_dict_menu_about:
                // Create a layout for center-screen dialogs
                View infoDialog = getLayoutInflater().inflate(R.layout.mw_dict_menu_info_activity, null);

                // Create an alert dialog
                AlertDialog.Builder infoBuilder = new AlertDialog.Builder(this);
                infoBuilder.setPositiveButton("Ok, got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).setView(infoDialog);

                infoBuilder.create().show();
                break;

            case R.id.mw_dict_menu_help:
                // Create a layout for center-screen dialogs
                View helpDialog = getLayoutInflater().inflate(R.layout.mw_dict_menu_help_activity, null);

                // Create an alert dialog
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                helpBuilder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).setView(helpDialog);

                helpBuilder.create().show();
                break;

            case R.id.mw_dict_menu_back:
                Snackbar goBack = Snackbar.make(myToolbar, "Go back?", Snackbar.LENGTH_LONG)
                        .setAction("Return", e -> finish());
                goBack.show();
                break;
        }

        return true;
    }

    public void onInvalidWord() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mw_dict_toast_invalid_word,
                (ViewGroup) findViewById(R.id.mwDictToastInvalidWord));

        TextView text = layout.findViewById(R.id.mwDictToastInvalidMsg);
        Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
    }

    public void onConnectionIssue() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mw_dict_toast_connection_issue,
                (ViewGroup) findViewById(R.id.mwDictToastConnectionIssue));

        TextView text = layout.findViewById(R.id.mwDictToastConnectionMsg);
        Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
    }

    public void onErrors() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mw_dict_toast_error,
                (ViewGroup) findViewById(R.id.mwDictToastError));

        TextView text = layout.findViewById(R.id.mwDictToastErrorMsg);
        Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
    }

    protected class MWDictForecastQuery extends AsyncTask<String, Integer, String> {
        final static String ASYNC_CLASS_NAME = "MWDictQuery";

        String word, syllables, pronunciation, wordType;
        LinkedList<String> definitionList = new LinkedList<String>();
        int definitionCount = 0;

        @Override
        protected String doInBackground(String... strings) {
            try {
                // Attempt to establish connection to the API
                String mwDictAPI = strings[0];
                URL mwDictUrl = new URL(mwDictAPI);
                HttpURLConnection newConnection = (HttpURLConnection) mwDictUrl.openConnection();
                InputStream inputStream = newConnection.getInputStream();
                Log.d(ASYNC_CLASS_NAME, "Connection to Merriam-Webster Dictionary API established.");

                // Create a pull parser
                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput( inputStream  , "UTF-8");  //inStream comes from line 46
                Log.d(ASYNC_CLASS_NAME, "Start reading data from MWDict API.");

                while(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (xpp.getEventType() == XmlPullParser.START_TAG) {
                        String tagName = xpp.getName(); //get the name of the starting tag: <tagName>
                        if (tagName.equals("entry")) {
                            word = xpp.getAttributeValue(null, "id");
                            publishProgress(10);
                            Log.d(ASYNC_CLASS_NAME, "Found word: " + word);
                        } else if (tagName.equals("hw")) {
                            xpp.next();
                            syllables = xpp.getText();
                            publishProgress(25);
                            Log.d(ASYNC_CLASS_NAME, "Found syllables: " + syllables);
                        } else if (tagName.equals("pr")) {
                            xpp.next();
                            pronunciation = xpp.getText();
                            publishProgress(37);
                            Log.d(ASYNC_CLASS_NAME, "Found pronunciation: " + pronunciation);
                        } else if (tagName.equals("fl")) {
                            xpp.next();
                            wordType = xpp.getText();
                            publishProgress(50);
                            Log.d(ASYNC_CLASS_NAME, "Found word type: " + wordType);
                        } else if (tagName.equals("dt")) {
                            xpp.next();
                            definitionList.add(xpp.getText());
                            Log.d(ASYNC_CLASS_NAME, "Added definition");
                        }
                    }

                    xpp.next();
                }

                publishProgress(100);

            } catch (MalformedURLException invalidUrl) {
                Log.e(ASYNC_CLASS_NAME, "Invalid URL entered");
                return "Invalid word.";
            } catch (IOException connectionError) {
                Log.e(ASYNC_CLASS_NAME, "Cannot connect to the forecast's API");
                return "Cannot connect to the Internet.";
            } catch (XmlPullParserException pullParserInstanceError) {
                Log.e(ASYNC_CLASS_NAME, "Cannot create a XmlPullParser or XmlPullParserFactory instance");
                return "We are sorry, an error has occurred.";
            } catch (Exception ex) {
                Log.e(ASYNC_CLASS_NAME, "Crash!! - " + ex.getMessage());
                return "We are sorry, an error has occurred.";
            }

            return "Finished all tasks.";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Finished all tasks.")) {


                Log.i(ASYNC_CLASS_NAME, result);
                loadingBar.setVisibility(View.INVISIBLE);
            } else if (result.equals("Invalid word.")) {
                onInvalidWord();
            } else if (result.equals("Cannot connect to the Internet.")) {
                onConnectionIssue();
            } else
                onErrors();
        }
    }
}
