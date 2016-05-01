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
                db.execSQL("UPDATE " + DbHelper.TABLE
                        + " SET "
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
