/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright 2016 Boris Zhao @ CLB
 *
 *
 * MainframeRemoteControl is a free software. you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * the Apache Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * MainframeRemoteControl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MainframeRemoteControl.  If not, see <http://commons.apache.org/proper/commons-daemon/license.html>.
 *
 * MainframeRemoteControl是一个自由软件，您可以自由分发、修改其中的源代码或者重新发布它，
 * 新的任何修改后的重新发布版必须同样在遵守Apache License 2.0或更后续的版本协议下发布.
 * 关于Apache License协议的细则请参考LICENSE文件，
 * 您可以在MainframeRemoteControl的相关目录中获得Apache License协议的副本，
 * 如果没有找到，请连接到 http://commons.apache.org/proper/commons-daemon/license.html 查看。
 *
 * - Author: Boris Zhao (赵迎东)
 * - Contact: boris1993@126.com
 * - License: Apache License 2.0
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
