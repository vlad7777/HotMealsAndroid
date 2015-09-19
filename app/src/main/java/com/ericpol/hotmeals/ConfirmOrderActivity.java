package com.ericpol.hotmeals;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ConfirmOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        EditText addressView = (EditText) this.findViewById(R.id.textbox_address);
        EditText specialCommentsView = (EditText) this.findViewById(R.id.textbox_special_comments);

        SharedPreferences pref = this.getSharedPreferences(this.getResources().getString(R.string.pref_name), Context.MODE_PRIVATE);
        String address = pref.getString(this.getResources().getString(R.string.last_address_pref), "");
        String comment = pref.getString(this.getResources().getString(R.string.last_comment_pref), "");

        addressView.setText(address);
        specialCommentsView.setText(comment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
