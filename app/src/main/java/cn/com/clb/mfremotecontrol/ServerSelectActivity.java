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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.commons.net.ftp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerSelectActivity extends AppCompatActivity {
    private long exitTime = 0;

    String address;
    String username;
    String password;

    DbHelper dbHelper;
    SQLiteDatabase db;

    FTPClient ftpClient = new FTPClient();

    boolean isLast = true;

    // ListView related
    ListView listView;
    SimpleAdapter adapter;
    ArrayList<HashMap<String, Object>> listData;

    /**
     * A thread for handling the FTP login process
     */
    class LoginToServer extends AsyncTask<String, Integer, String> {
        String connStat = "FAILED";

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "CONNECTING...", Toast.LENGTH_SHORT).show();
        }

        /**
         * Perform the login and return if it is successful
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {
            try {
                ftpClient.connect(InetAddress.getByName(args[0]));
                ftpClient.login(args[1], args[2]);
                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    connStat = "FAILED";
                    ftpClient.disconnect();
                } else {
                    connStat = "SUCCESSFUL";
                    ftpClient.disconnect();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return connStat;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ServerSelectActivity.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("SUCCESSFUL")) {
                Intent intent = new Intent(ServerSelectActivity.this, OperateActivity.class);
                intent.putExtra("String.ipaddr", address);
                intent.putExtra("String.username", username);
                intent.putExtra("String.password", password);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_select);
        listView = (ListView) findViewById(R.id.serverList);

        // Make an ArrayList with data in it
        listData = fillList();
        // Create an adapter
        adapter = fillAdapter(listData);
        // Sets the data behind this ListView.
        listView.setAdapter(adapter);

        //region Click to quick login
        /**
         * Set a listener to watch for the menu item clicking
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                String addr = map.get("address");
                dbHelper = new DbHelper(ServerSelectActivity.this);
                db = dbHelper.getReadableDatabase();
                try {
                    Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE + " WHERE " + DbHelper.C_ADDRESS + "= '" + addr + "'", null);
                    cursor.moveToFirst();
                    address = cursor.getString(cursor.getColumnIndex(DbHelper.C_ADDRESS));
                    username = cursor.getString(cursor.getColumnIndex(DbHelper.C_USERNAME));
                    password = cursor.getString(cursor.getColumnIndex(DbHelper.C_PASSWORD));
                    String[] loginParms = {address, username, password};
                    new LoginToServer().execute(loginParms);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(db.isOpen()) { db.close(); }
                }
            }
        });
        //endregion

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, getString(R.string.menu_delete));
                menu.add(0, 1, 0, getString(R.string.menu_edit));
            }
        });
    }

    //region Add items to the list

    /**
     * Create an ArrayList with servers in it which will be used in the adapter later
     * @return ArrayList
     */
    public ArrayList<HashMap<String, Object>> fillList() {
        // Create a temp ArrayList to store items
        // Later this will be returned
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        // Create a DbHelper object, and make the database readable
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE, null);
            isLast = cursor.moveToFirst();
            // When reached the end, the value of isLast will be false
            while (isLast) {
                // Get items one by one until the last one reached
                String address = cursor.getString(cursor.getColumnIndex(DbHelper.C_ADDRESS));
                String name = cursor.getString(cursor.getColumnIndex(DbHelper.C_NAME));
                // Create a temp HashMap for the single record
                // like a table
                // --------------------------
                // | name   | address       |
                // |--------|---------------|
                // |X200    | 10.0.0.220    |
                // --------------------------
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("name", name);
                map.put("address", address);
                // Add this map to the ArrayList
                dataList.add(map);
                // See if it reaches the last one
                isLast = cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen()) { db.close(); }
        }
        return dataList;
    }

    /**
     * An adapter for the ListView
     * "An easy adapter to map static data to views defined in an XML file.",
     * said the Android Developer website.
     * @param listData
     * @return SimpleAdapter adapter
     */
    public SimpleAdapter fillAdapter(ArrayList<HashMap<String, Object>> listData) {
        /**
         * @param1 The context where the View associated with this SimpleAdapter is running,
         * @param2 A List of Maps. Each entry in the List corresponds to one row in the list,
         *          The Maps contain the data for each row,
         *          and should include all the entries specified in "from" which is the 4th param.
         * @param3 Layout which defines how data is displayed in the ListView,
         *          The layout file should include at least those named views defined in "to",
         *          which is the 5th param
         * @param4 A list of column names that will be added to the Map associated with each item.
         * @param5 The views that should display column in the "from" parameter.
         *          These should all be TextViews.
         *          The first N views in this list are given the values of the first N columns in the from parameter.
         */
        return adapter = new SimpleAdapter(this, listData,
                R.layout.list_item,
                new String[] {"name", "address"},
                new int[] {R.id.item_name, R.id.item_address});
    }
    //endregion

    /**
     * Delete an item in the server list
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent;
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        HashMap<String, String> map;
        String addr;
        switch (item.getItemId()) {
            case 0:
                // Get selected item and save into a HashMap
                map = (HashMap<String, String>) listView.getItemAtPosition(menuInfo.position);
                // Fetch the IP address as the condition when deleting
                addr = map.get("address");
                // Make the database writable
                dbHelper = new DbHelper(ServerSelectActivity.this);
                db = dbHelper.getWritableDatabase();
                // Delete this record in the database
                // TODO: Delete server by ID
                db.execSQL("DELETE FROM " + DbHelper.TABLE + " WHERE address = '" + addr + "'");
                if (db.isOpen()) {
                    db.close();
                }
                // Recreate the list
                listData.clear();
                listData = fillList();
                // and refresh the ListView
                adapter = fillAdapter(listData);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                break;
            case 1:
                map = (HashMap<String, String>) listView.getItemAtPosition(menuInfo.position);
                addr = map.get("address");
                db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE + " WHERE address = '" + addr + "'", null);
                cursor.moveToFirst();
                intent = new Intent(this, EditServerActivity.class);
                intent.putExtra("String.id", cursor.getString(cursor.getColumnIndex(DbHelper.C_ID)));
                intent.putExtra("String.name", cursor.getString(cursor.getColumnIndex(DbHelper.C_NAME)));
                intent.putExtra("String.addr", cursor.getString(cursor.getColumnIndex(DbHelper.C_ADDRESS)));
                intent.putExtra("String.port", cursor.getString(cursor.getColumnIndex(DbHelper.C_PORT)));
                intent.putExtra("String.username", cursor.getString(cursor.getColumnIndex(DbHelper.C_USERNAME)));
                intent.putExtra("String.password", cursor.getString(cursor.getColumnIndex(DbHelper.C_PASSWORD)));
                if(db.isOpen()) { db.close(); }
                startActivity(intent);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Watch for the key press events
     * @param keycode <== Which key is touched
     * @param keyEvent <== What operation performed to the key
     * @return
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent keyEvent) {
        if (keycode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), this.getString(R.string.exit_notify), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keycode, keyEvent);
    }

    //region menu handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                finish();
                break;
            case R.id.menu_addServer:
                startActivity(new Intent(this, AddServerActivity.class));
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion


}
