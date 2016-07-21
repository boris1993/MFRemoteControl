/*
 *      Copyright 2016 Boris Zhao
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package cn.com.clb.mfremotecontrol;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;

public class AddServerActivity extends AppCompatActivity implements View.OnClickListener {

    //region FTP Utilities
    DbHelper dbHelper;
    SQLiteDatabase db;
    //endregion

    //region Widgets
    EditText txtAddress;
    EditText txtUsername;
    EditText txtPassword;
    EditText txtName;
    EditText txtPort;
    Button btnSubmit;
    Button btnReset;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPort = (EditText) findViewById(R.id.txtPort);
        txtName = (EditText) findViewById(R.id.txtName);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnReset = (Button) findViewById(R.id.btnReset);

        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Watch for the button click events
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit: {
                dbHelper = new DbHelper(AddServerActivity.this);
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                try {
                    values.clear();
                    values.put(DbHelper.C_NAME, txtName.getText().toString());
                    values.put(DbHelper.C_ADDRESS, txtAddress.getText().toString());
                    values.put(DbHelper.C_PORT, txtPort.getText().toString());
                    values.put(DbHelper.C_USERNAME, txtUsername.getText().toString());
                    values.put(DbHelper.C_PASSWORD, txtPassword.getText().toString());
                    db.insert(DbHelper.TABLE, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (db.isOpen()) { db.close(); }
                }
                this.startActivity( new Intent(this, ServerSelectActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) );
                break;
            }
            case R.id.btnReset: {
                txtAddress.setText("");
                txtUsername.setText("");
                txtPassword.setText("");
                txtPort.setText("");
                break;
            }
            default:
                break;
        }
    }


}
