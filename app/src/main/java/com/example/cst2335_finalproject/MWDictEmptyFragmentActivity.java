package com.example.cst2335_finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/** This class is a placeholder activity to display the fragment.
 * @author Minh Tran
 * @version 1.0
 * @since April 4, 2019
 */
public class MWDictEmptyFragmentActivity extends AppCompatActivity {

    /** This method initialises the activity's interface and displays the fragment
     * @param savedInstanceState Default argument for passing Bundles from other activities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mw_dict_empty_activity);

        /* Get the word's data from the Intent */
        Bundle wordDetails = getIntent().getExtras();

        MWDictWordDefinitionFragment wordDefinitionFragment = new MWDictWordDefinitionFragment();
        wordDefinitionFragment.setArguments(wordDetails);
        wordDefinitionFragment.setTablet(false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.MWDictFragmentLocation, wordDefinitionFragment)
                .addToBackStack("Back")
                .commit();
    }
}
