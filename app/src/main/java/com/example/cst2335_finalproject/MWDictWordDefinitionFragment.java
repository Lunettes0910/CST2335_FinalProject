package com.example.cst2335_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/** This class implements the display and deletion of a word shown from the history database
 * @author Minh Tran
 * @version 1.2
 * @since April 17, 2019
 */
public class MWDictWordDefinitionFragment extends Fragment {
    /** Default request number for word deletion */
    private static final int DELETE_WORD_REQUEST = 335;

    /** A boolean checking if the device is a tablet */
    private boolean isTablet;

    /** Bundle containing the word's details passed from a parent activity */
    private Bundle dataFromActivity;

    /** The word to display details about */
    private String word;

    /** This method receives the info whether the device is a tablet and setup isTablet accordingly.
     * @param tablet Whether the device is a tablet
     */
    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        word = dataFromActivity.getString(MWDictHistoryActivity.SELECTED_WORD);

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.mw_dict_word_definition_fragment, container, false);

        TextView wordDisplay = result.findViewById(R.id.mw_dict_frag_word);
        wordDisplay.setText(word);

        TextView syllablesDisplay = (TextView) result.findViewById(R.id.mw_dict_frag_syllables);
        syllablesDisplay.setText(dataFromActivity.getString(MWDictHistoryActivity.SELECTED_SYLLABLES));

        TextView pronunDisplay = (TextView) result.findViewById(R.id.mw_dict_frag_pronunciation);
        pronunDisplay.setText(dataFromActivity.getString(MWDictHistoryActivity.SELECTED_PRONUNCIATION));

        TextView typeDisplay = (TextView) result.findViewById(R.id.mw_dict_frag_word_type);
        typeDisplay.setText(dataFromActivity.getString(MWDictHistoryActivity.SELECTED_WORD_TYPE));

        TextView definitionDisplay = (TextView) result.findViewById(R.id.mw_dict_frag_word_definition);
        definitionDisplay.setText(dataFromActivity.getString(MWDictHistoryActivity.SELECTED_DEFINITION));

        int position = dataFromActivity.getInt(MWDictHistoryActivity.SELECTED_POSITION);

        /* Word deletion */
        Button deleteButton = (Button) result.findViewById(R.id.MWDictDeleteWordButton);
        deleteButton.setOnClickListener( clk -> {

            if(isTablet) {
                MWDictHistoryActivity parentActivity = (MWDictHistoryActivity) getActivity();
                parentActivity.deleteWord(position);
                parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            else
            {
                MWDictEmptyFragmentActivity parentActivity = (MWDictEmptyFragmentActivity) getActivity();
                Intent backToChatRoomActivity = new Intent();
                backToChatRoomActivity.putExtra(MWDictHistoryActivity.SELECTED_POSITION, dataFromActivity.getInt(MWDictHistoryActivity.SELECTED_POSITION));

                parentActivity.setResult(DELETE_WORD_REQUEST, backToChatRoomActivity);
                parentActivity.finish();
            }
        });
        return result;
    }
}
