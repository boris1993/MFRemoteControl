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
