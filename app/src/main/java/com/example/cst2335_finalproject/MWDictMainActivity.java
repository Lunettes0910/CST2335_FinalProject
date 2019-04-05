package com.example.cst2335_finalproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MWDictMainActivity extends AppCompatActivity {
    protected Toolbar myToolbar;
    protected String menuToastMessage = "This is the initial message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mw_dict_main_activity);

        myToolbar = findViewById(R.id.mwDictToolbar);
        setSupportActionBar(myToolbar);

        EditText enteredWord = findViewById(R.id.mwDictWordInput);
        String word = enteredWord.getText().toString();

        Button searchWord = findViewById(R.id.mwDictSearchButton);
        searchWord.setOnClickListener( b -> {
            if (word.equals("")) {
                Toast.makeText(this, "Please input a word.", Toast.LENGTH_LONG).show();
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
}
