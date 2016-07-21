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

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditServerActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtServerName;
    EditText txtAddr;
    EditText txtUsername;
    EditText txtPassword;
    EditText txtPort;

    Button btnSubmit;
    Button btnCancel;

    String id;

    DbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_server);

        txtServerName = (EditText) findViewById(R.id.txtServerName);
        txtAddr = (EditText) findViewById(R.id.txtAddress);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPort = (EditText) findViewById(R.id.txtPort);

        btnSubmit = (Button) findViewById(R.id.btn_edit_update);
        btnCancel = (Button) findViewById(R.id.btn_edit_cancel);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("String.id");
        txtServerName.setText(intent.getStringExtra("String.name"));
        txtAddr.setText(intent.getStringExtra("String.addr"));
        txtPort.setText(intent.getStringExtra("String.port"));
        txtUsername.setText(intent.getStringExtra("String.username"));
        txtPassword.setText(intent.getStringExtra("String.password"));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_edit_update:
                dbHelper = new DbHelper(this);
                db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE " + DbHelper.TABLE + " SET "
                        + DbHelper.C_NAME + "='" + txtServerName.getText().toString() + "',"
                        + DbHelper.C_ADDRESS + "='" + txtAddr.getText().toString() + "',"
                        + DbHelper.C_PORT + "='" + txtPort.getText().toString() + "',"
                        + DbHelper.C_USERNAME + "='" + txtUsername.getText().toString() + "',"
                        + DbHelper.C_PASSWORD + "='" + txtPassword.getText().toString() + "'"
                        + " WHERE " + DbHelper.C_ID + "=" + id);
                intent = new Intent(this, ServerSelectActivity.class);
                // Description for the next expression:
                // If set, and the activity being launched is already running in the current task,
                // then instead of launching a new instance of that activity,
                // all of the other activities on top of it will be closed
                // and this Intent will be delivered to the (now on top) old activity as a new Intent.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_edit_cancel:
                intent = new Intent(this, ServerSelectActivity.class);
                // Description for the next expression:
                // If set, and the activity being launched is already running in the current task,
                // then instead of launching a new instance of that activity,
                // all of the other activities on top of it will be closed
                // and this Intent will be delivered to the (now on top) old activity as a new Intent.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

    }
}
