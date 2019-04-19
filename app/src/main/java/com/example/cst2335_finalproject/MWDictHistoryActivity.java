package com.example.cst2335_finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/** This class implements the interface for the app's history database */
public class MWDictHistoryActivity extends AppCompatActivity {
    /** Default tag for this class */
    public static final String CLASS_NAME = "MW_DICT_HISTORY";

    /** Default column name for the searched words */
    public static final String SELECTED_WORD = "WORD";

    /** Default column name for the searched words's syllables */
    public static final String SELECTED_SYLLABLES = "SYLLABLES";

    /** Default column name for the searched words' pronunciation */
    public static final String SELECTED_PRONUNCIATION = "PRONUNCIATION";

    /** Default column name for the searched words' type */
    public static final String SELECTED_WORD_TYPE = "WORDTYPE";

    /** Default column name for the searched words' definition */
    public static final String SELECTED_DEFINITION = "DEFINITION";

    /** Default column name for the searched words' position in the database / id */
    public static final String SELECTED_POSITION = "POSITION";

    /** Default request number for an empty placeholder activity */
    public static final int EMPTY_ACTIVITY_REQUEST = 345;

    /** Default request number for database deletion */
    public static final int DELETE_WORD_REQUEST = 335;

    /** History database opener to save the searched word */
    MWDictHistoryDatabaseOpenHelper dbOpener;

    /** The history database storing all searched words */
    SQLiteDatabase db;

    /** A placeholder ArrayList to display and manage all words */
    protected ArrayList<MWDictWord> wordList = new ArrayList<>(1);

    /** A ListView adapter to manage changes on the word list */
    WordListAdapter wordListAdapter;

    /** This method initialises the interface displaying the history database
     * @param savedInstanceState Default argument for passing Bundles from other activities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mw_dict_word_list_activity);

        /** A boolean check if the device is a tablet by looking for the fragment's view */
        boolean isTablet = findViewById(R.id.MWDictWordDefinitionFragment) != null;

        /* Open the database */
        dbOpener = new MWDictHistoryDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();

        /* Setup queries */
        String[] columns = {MWDictHistoryDatabaseOpenHelper.COL_WORD, MWDictHistoryDatabaseOpenHelper.COL_SYLLABLES, MWDictHistoryDatabaseOpenHelper.COL_PRONUNCIATION,
                MWDictHistoryDatabaseOpenHelper.COL_WORD_TYPE, MWDictHistoryDatabaseOpenHelper.COL_DEFINITION};
        Cursor results = db.query(false, MWDictHistoryDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        /* Display the database */

        int wordIndex = results.getColumnIndex(MWDictHistoryDatabaseOpenHelper.COL_WORD);
        int syllablesIndex = results.getColumnIndex(MWDictHistoryDatabaseOpenHelper.COL_SYLLABLES);
        int pronunIndex = results.getColumnIndex(MWDictHistoryDatabaseOpenHelper.COL_PRONUNCIATION);
        int wordTypeIndex = results.getColumnIndex(MWDictHistoryDatabaseOpenHelper.COL_WORD_TYPE);
        int definitionIndex = results.getColumnIndex(MWDictHistoryDatabaseOpenHelper.COL_DEFINITION);

        String pastWord, pastSyllables, pastPronun, pastWordType, pastDefinitionList;

        while (results.moveToNext()) {
            pastWord = results.getString(wordIndex);
            pastSyllables = results.getString(syllablesIndex);
            pastPronun = results.getString(pronunIndex);
            pastWordType = results.getString(wordTypeIndex);
            pastDefinitionList = results.getString(definitionIndex);

            wordList.add(new MWDictWord(pastWord, pastSyllables, pastPronun, pastWordType, pastDefinitionList));
        }

        /* Print database info to Log window for debugging purposes */
        printCursor(results);

        /* Get ListView and use an Adapter for ListView */

        ListView chatLog = findViewById(R.id.MWDictWordListView);
        wordListAdapter = new WordListAdapter();
        chatLog.setAdapter(wordListAdapter);
        chatLog.setOnItemClickListener( (list, item, position, id) -> {
            Bundle wordDetails = new Bundle();
            MWDictWord chosenWord = wordList.get(position);

            wordDetails.putString(SELECTED_WORD, chosenWord.getWord());
            wordDetails.putString(SELECTED_SYLLABLES, chosenWord.getSyllables());
            wordDetails.putString(SELECTED_PRONUNCIATION, chosenWord.getPronunciation());
            wordDetails.putString(SELECTED_WORD_TYPE, chosenWord.getWordType());
            wordDetails.putString(SELECTED_DEFINITION, chosenWord.getDefinitionList());
            wordDetails.putInt(SELECTED_POSITION, position);

            if(isTablet)
            {
                MWDictWordDefinitionFragment wordDefinitionFragment = new MWDictWordDefinitionFragment();
                wordDefinitionFragment.setArguments(wordDetails);
                wordDefinitionFragment.setTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MWDictWordDefinitionFragment, wordDefinitionFragment)
                        .addToBackStack("Back")
                        .commit();
            }
            else
            {
                Intent nextActivity = new Intent(this, MWDictEmptyFragmentActivity.class);
                nextActivity.putExtras(wordDetails);
                startActivityForResult(nextActivity, EMPTY_ACTIVITY_REQUEST);
            }
        });
    }

    /** This method prints the database's info to the Log window.
     * For debugging purposes
     * @param c A cursor reading through the database
     */
    protected void printCursor(Cursor c) {
        Log.d(MWDictHistoryDatabaseOpenHelper.DATABASE_NAME, "Version number: " + MWDictHistoryDatabaseOpenHelper.VERSION_NUM);
        Log.d(MWDictHistoryDatabaseOpenHelper.TABLE_NAME, "Containing " + c.getColumnCount() + " column(s)");
        Log.d(MWDictHistoryDatabaseOpenHelper.TABLE_NAME, "Found " + c.getCount());
    }

    /* A specific Adapter used to show message stream in chat room */
    protected class WordListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return wordList.size();
        }

        @Override
        public Object getItem(int position) {
            return wordList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            View newView = null;
            TextView showMessage;

            newView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
            showMessage = newView.findViewById(android.R.id.text1);
            showMessage.setText(wordList.get(position).getWord());
            showMessage = newView.findViewById(android.R.id.text2);
            showMessage.setText(wordList.get(position).getWordType());

            return newView;
        }

        public long getItemId (int position) {
            return position;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EMPTY_ACTIVITY_REQUEST)
        {
            if(resultCode == DELETE_WORD_REQUEST)
            {
                int position = data.getIntExtra(SELECTED_POSITION, 0);
                deleteWord(position);
            }
        }
    }

    public void deleteWord(int position)
    {
        db.delete(MWDictHistoryDatabaseOpenHelper.TABLE_NAME, "_word=?", new String[] { wordList.get(position).getWord() });
        Log.d(CLASS_NAME, "Delete " + wordList.get(position).getWord() + " from position #" + position);
        wordList.remove(position);
        wordListAdapter.notifyDataSetChanged();
    }
}
