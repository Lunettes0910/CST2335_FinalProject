package com.example.cst2335_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/** This class implements the main interface for the MW Dictionary API. */
public class MWDictMainActivity extends AppCompatActivity {
    /** Default column name for the searched word */
    public static final String SELECTED_WORD = "WORD";

    /** Default column name for the searched word's syllables */
    public static final String SELECTED_SYLLABLES = "SYLLABLES";

    /** Default column name for the searched word's pronunciation */
    public static final String SELECTED_PRONUNCIATION = "PRONUNCIATION";

    /** Default column name for the searched word's type (i.e. noun, verb, etc.) */
    public static final String SELECTED_WORD_TYPE = "WORDTYPE";

    /** Default column name for the searched words' definition */
    public static final String SELECTED_DEFINITION = "DEFINITION";

    /** Default tag for the application */
    final static String APP_NAME = "M-W Dictionary API";

    /** First part of the API's URL to be parsed */
    final static String mwDictUrl = "https://www.dictionaryapi.com/api/v1/references/sd3/xml/";

    /** Second part of the API's URL to be parsed */
    final static String mwDictKey = "?key=4556541c-b8ed-4674-9620-b6cba447184f";

    /** History database opener to save the searched word */
    protected MWDictHistoryDatabaseOpenHelper dbOpener;

    /** The history database storing all searched words */
    protected SQLiteDatabase db;

    /** A toolbar containing info about the app */
    protected Toolbar myToolbar;

    /** A progressBar updating the search progress */
    protected ProgressBar loadingBar;

    /** A SharedPreference file storing the user's last search */
    protected SharedPreferences sharedPrefs;

    /** An EditText for search word input */
    protected EditText enterWord;

    /** The user's searched word */
    protected String searchWord;

    /** This method initialises the app's interface.
     * @param savedInstanceState Default argument for passing Bundles from other activities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mw_dict_main_activity);

        myToolbar = findViewById(R.id.mwDictToolbar);
        setSupportActionBar(myToolbar);

        enterWord = findViewById(R.id.mwDictWordInput);

        sharedPrefs = getSharedPreferences("usersLastWordInput", Context.MODE_PRIVATE);
        String typedField = sharedPrefs.getString("LastSearch", "");

        enterWord.setText(typedField);

        Button historyButton = findViewById(R.id.mwDictHistoryButton);
        historyButton.setOnClickListener(a -> {
            Intent startChatIntent = new Intent(this, MWDictHistoryActivity.class);
            startActivity(startChatIntent);
        });

        Button searchWordButton = findViewById(R.id.mwDictSearchButton);
        searchWordButton.setOnClickListener( b -> {
            searchWord = enterWord.getText().toString().toLowerCase();

            if (searchWord.equals("")) {
                Log.e(APP_NAME, "Empty word: ".concat(searchWord));
                onInvalidWord();

            } else {
                try {
                    MWDictQuery networkThread = new MWDictQuery();
                    String wordUrl = mwDictUrl.concat(URLEncoder.encode(searchWord, "UTF-8")).concat(mwDictKey);
                    networkThread.execute(wordUrl);

                    loadingBar = findViewById(R.id.mwDictConnectionBar);
                    loadingBar.setVisibility(View.VISIBLE);
                    // Get a database
                    MWDictHistoryDatabaseOpenHelper dbOpener = new MWDictHistoryDatabaseOpenHelper(this);
                    db = dbOpener.getWritableDatabase();

                } catch (UnsupportedEncodingException notEnglishWord) {
                    Log.e(APP_NAME, "Foreign word");
                    onInvalidWord();
                }
            }
        });
    }

    /** This method initialises the app's toolbar.
     * @param menu Default argument for passing Menu from the interface's layout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu items in ActionBar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mw_dict_menu_activity, menu);

        return true;
    }

    /** This method executes choices made on the app's toolbar
     * @param item Chosen item on the menu
     */
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

    /** This method saved the user's input into the SharedPreferences file */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor spEditor = sharedPrefs.edit();

        String typedField = enterWord.getText().toString();
        spEditor.putString("LastSearch", typedField);

        spEditor.commit();
    }

    /** This method initialises a Toast informing users of their invalid input. */
    public void onInvalidWord() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mw_dict_toast_invalid_word,
                (ViewGroup) findViewById(R.id.mwDictToastInvalidWord));

        TextView text = layout.findViewById(R.id.mwDictToastInvalidMsg);
        Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
    }

    /** This method initialises a Toast informing users of connection issues. */
    public void onConnectionIssue() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mw_dict_toast_connection_issue,
                (ViewGroup) findViewById(R.id.mwDictToastConnectionIssue));

        TextView text = layout.findViewById(R.id.mwDictToastConnectionMsg);
        Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
    }

    /** This method initialises a Toast informing users of the app's internal errors. */
    public void onErrors() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mw_dict_toast_error,
                (ViewGroup) findViewById(R.id.mwDictToastError));

        TextView text = layout.findViewById(R.id.mwDictToastErrorMsg);
        Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
    }

    /** This class implements an Asynchronous Task to connect to the Dictionary's API and search the word.
     * @author Minh Tran
     * @version 1.0
     * @since April 4, 2019
     */
    protected class MWDictQuery extends AsyncTask<String, Integer, String> {
        /** Default tag for the class */
        final static String ASYNC_CLASS_NAME = "MWDictQuery";

        /** Default word for the task as entered by the user */
        String word = searchWord;

        /** Word's syllables to find */
        String syllables;

        /** Word's pronunciation to find */
        String pronunciation;

        /** Word's type to find */
        String wordType;

        /** Word's definition */
        String definitionList = "";

        /** This method runs an Internet connection in the background to search for the word.
         * @param strings The queried URL
         * @return The search's result (i.e. found word, connection issues, or crash)
         */
        @Override
        protected String doInBackground(String... strings) {
            try {
                /* Attempt to establish connection to the API */
                String mwDictAPI = strings[0];
                URL mwDictUrl = new URL(mwDictAPI);
                HttpURLConnection newConnection = (HttpURLConnection) mwDictUrl.openConnection();
                InputStream inputStream = newConnection.getInputStream();
                Log.d(ASYNC_CLASS_NAME, "Connection to Merriam-Webster Dictionary API established.");

                /* Create a pull parser */
                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput( inputStream  , "UTF-8");
                Log.d(ASYNC_CLASS_NAME, "Start reading data from MWDict API.");

                while(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (xpp.getEventType() == XmlPullParser.START_TAG) {
                        String tagName = xpp.getName();
                        if (tagName.equals("hw")) {
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
                        }else if (tagName.equals("sn")) {
                            xpp.next();
                            definitionList = definitionList.concat(xpp.getText());
                            Log.d(ASYNC_CLASS_NAME, "Found new definition #" + xpp.getText());
                        } else if (tagName.equals("dt")) {
                            xpp.next();
                            definitionList = definitionList.concat(xpp.getText()).concat("\n");
                            Log.d(ASYNC_CLASS_NAME, "Added definition: xpp.getText()");
                        } else if (tagName.equals("/entry"))
                            break;
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

        /** This method update the search progress onto the interface's progressBar
         * @param values Completed percentage of the search
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.setProgress(values[0]);
        }

        /** This method automatically saves the searched word into the history database or issues notices otherwise.
         * @param result The search process's result
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Finished all tasks.")) {
                Log.i(ASYNC_CLASS_NAME, result);
                loadingBar.setVisibility(View.INVISIBLE);

                TextView wordDisplay = findViewById(R.id.mw_dict_word);
                wordDisplay.setText(word);
                wordDisplay.setVisibility(View.VISIBLE);

                TextView syllablesDisplay = (TextView) findViewById(R.id.mw_dict_syllables);
                syllablesDisplay.setText(syllables);
                syllablesDisplay.setVisibility(View.VISIBLE);

                TextView pronunDisplay = (TextView) findViewById(R.id.mw_dict_pronunciation);
                pronunDisplay.setText(pronunciation);
                pronunDisplay.setVisibility(View.VISIBLE);

                TextView typeDisplay = (TextView) findViewById(R.id.mw_dict_word_type);
                typeDisplay.setText(wordType);
                typeDisplay.setVisibility(View.VISIBLE);

                TextView definitionDisplay = (TextView) findViewById(R.id.mw_dict_word_definition);
                definitionDisplay.setText(definitionList);
                definitionDisplay.setVisibility(View.VISIBLE);

                ContentValues newRowValues = new ContentValues();
                MWDictWord newWord = new MWDictWord(word, syllables, pronunciation, wordType, definitionList);

                newRowValues.put(SELECTED_WORD, newWord.getWord());
                newRowValues.put(SELECTED_SYLLABLES, newWord.getSyllables());
                newRowValues.put(SELECTED_PRONUNCIATION, newWord.getPronunciation());
                newRowValues.put(SELECTED_WORD_TYPE, newWord.getWordType());
                newRowValues.put(SELECTED_DEFINITION, newWord.getDefinitionList());

                /* Get ID for the new word */
                long newId = db.insert(MWDictHistoryDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

            } else if (result.equals("Invalid word.")) {
                onInvalidWord();
            } else if (result.equals("Cannot connect to the Internet.")) {
                onConnectionIssue();
            } else
                onErrors();
        }
    }
}
