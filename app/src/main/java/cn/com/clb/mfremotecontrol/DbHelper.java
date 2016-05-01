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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    static final String TAG = "DbHelper";
    static final String DB_NAME = "ftpservers.db";
    static final int DB_VERSION = 1;
    static final String TABLE = "servers";
    static final String C_ID = BaseColumns._ID;
    static final String C_ADDRESS = "address";
    static final String C_PORT = "port";
    static final String C_USERNAME = "username";
    static final String C_PASSWORD = "password";
    static final String C_NAME = "name";
    Context context;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE + "("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_ADDRESS + " TEXT, "
                + C_PORT + " TEXT, "
                + C_USERNAME + " TEXT, "
                + C_PASSWORD + " TEXT, "
                + C_NAME + " TEXT)";
        db.execSQL(sql);
        Log.d(TAG, "TABLE CREATED WITH SQL" + sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE);
        Log.d(TAG, "TABLE DROPPED FOR RENEW");
        onCreate(db);
    }
}
