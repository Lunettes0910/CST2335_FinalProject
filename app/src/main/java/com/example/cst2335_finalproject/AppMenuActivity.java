package com.example.cst2335_finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppMenuActivity extends AppCompatActivity {
    final static int APP_COUNT = 4;
    final static String APP_1_NAME = "Merriam-Webster Dictionary";
    final static String APP_2_NAME = "News Feed";
    final static String APP_3_NAME = "Flight Status Tracker";
    final static String APP_4_NAME = "New York Times Article Search";

    final static String APP_1_AUTHOR = "Minh Tran";
    final static String APP_2_AUTHOR = "???";
    final static String APP_3_AUTHOR = "Weihao Liao";
    final static String APP_4_AUTHOR = "???";

    protected Toolbar appToolbar;
    protected AppListAdapter appListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_menu_main_activity);

        appToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        ListView appList = findViewById(R.id.appListView);
        appListAdapter = new AppListAdapter();
        appList.setAdapter(appListAdapter);
        appListAdapter.notifyDataSetChanged();

        appList.setOnItemClickListener(( parent,  view,  position,  id) -> {
            Intent testingApp;

            switch (position) {
                case 0:
                    testingApp = new Intent(this, MWDictMainActivity.class);
                    startActivity(testingApp);
                    break;

                case 1:
                case 2:
                case 3:
                    Toast.makeText(this, "This application is not available yet.", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu items in ActionBar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent testingApp;
        switch(item.getItemId()) {
            case R.id.app_menu_app1:
                testingApp = new Intent(this, MWDictMainActivity.class);
                startActivity(testingApp);
                break;

            case R.id.app_menu_app2:
            case R.id.app_menu_app3:
            case R.id.app_menu_app4:
                Toast.makeText(this, "This application is not available yet.", Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }

    protected class AppListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return APP_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            View newView = inflater.inflate(android.R.layout.two_line_list_item, parent, false);

            TextView appText = newView.findViewById(android.R.id.text1);
            TextView appSubtitle = newView.findViewById(android.R.id.text2);
            String appName = "???";
            String appAuthor = "???";

            switch (position) {
                case 0:
                    appName = APP_1_NAME;
                    appAuthor = APP_1_AUTHOR;
                    break;
                case 1:
                    appName = APP_2_NAME;
                    appAuthor = APP_2_AUTHOR;
                    break;
                case 2:
                    appName = APP_3_NAME;
                    appAuthor = APP_3_AUTHOR;
                    break;
                case 3:
                    appName = APP_4_NAME;
                    appAuthor = APP_4_AUTHOR;
                    break;
            }

            appText.setText(appName);
            appSubtitle.setText(appAuthor);

            //return the row:
            return newView;
        }
    }
}
