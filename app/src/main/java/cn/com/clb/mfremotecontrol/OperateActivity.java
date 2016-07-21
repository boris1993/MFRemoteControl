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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;

public class OperateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int FILE_SELECT_CODE = 0;
    boolean isInternal = false;

    Button btnKick;
    Button btnOther;

    FTPClient ftpClient = new FTPClient();

    String address;
    String username;
    String password;
    String localPath;

    class SubmitJCL extends AsyncTask<Intent, Integer, String> {
        String execStat = "FAILED";

        public SubmitJCL() {
            super();
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "SENDING...", Toast.LENGTH_SHORT).show();
        }

        /**
         * Process the login and return if it is successful
         *
         * @param args
         * @return connStat = {"SUCCESSFUL" , "FAILED"}
         * @description args[0] = data, args[1] = remotePath, args[2] = password
         */
        @Override
        protected String doInBackground(Intent... args) {
            String remotePath = "'" + username.toUpperCase() + ".JCL(" + username.toUpperCase() + "F)'";
            Uri uri = args[0].getData();
            try {
                localPath = getPath(OperateActivity.this, uri);
                ftpClient.connect(InetAddress.getByName(address));
                ftpClient.login(username, password);
                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    Toast.makeText(OperateActivity.this, getString(R.string.toast_conn_fail), Toast.LENGTH_SHORT).show();
                } else {
                    ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
                    FileInputStream fistream = new FileInputStream(new File(localPath));
                    boolean result = ftpClient.storeFile(remotePath, fistream);
                    ftpClient.sendSiteCommand("FILETYPE=JES");
                    // GET IBMUSER.JCL(IBMUSERF)
                    // Let's just save the output here
                    // Trust me, I'll pick somewhere better for it
                    // Well finally found a kinda suitable home for it...
                    File outFile = new File("/storage/sdcard0/JCL/jesout.txt");
                    OutputStream outputStream = new FileOutputStream(outFile);
                    // IBMUSER.JCL(IBMUSERF)
                    ftpClient.retrieveFile("'" + username.toUpperCase() + ".JCL(" + username.toUpperCase() + "F)'", outputStream);
                    fistream.close();
                    outputStream.close();
                    ftpClient.logout();
                    ftpClient.disconnect();
                    if (result) {
                        execStat = "JCL SUBMITTED";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return execStat;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (execStat.equals("FAILED")) {
                Toast.makeText(OperateActivity.this, execStat, Toast.LENGTH_SHORT).show();
            } else if (execStat.equals("JCL SUBMITTED")) {
                Toast.makeText(OperateActivity.this, "Check JCL/jesout.txt for the output", Toast.LENGTH_SHORT).show();
                if (isInternal) {
                    File file = new File(localPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate);

        btnOther = (Button) findViewById(R.id.btn_other);
        btnKick = (Button) findViewById(R.id.btnKick);
        if (btnKick != null) {
            btnKick.setOnClickListener(this);
        }
        if (btnOther != null) {
            btnOther.setOnClickListener(this);
        }

        Intent intent = getIntent();
        address = intent.getStringExtra("String.ipaddr");
        username = intent.getStringExtra("String.username");
        password = intent.getStringExtra("String.password");

    }

    /**
     * When back key is pressed, go to the AddServerActivity, and destroy ServerSelectActivity
     *
     * @param keyCode
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            // Description for the next expression:
            // If set, and the activity being launched is already running in the current task,
            // then instead of launching a new instance of that activity,
            // all of the other activities on top of it will be closed
            // and this Intent will be delivered to the (now on top) old activity as a new Intent.
            startActivity(new Intent(this, ServerSelectActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnKick:
                sendKickJCL();
                break;
            case R.id.btn_other:
                showFileChooser();
                break;
        }
    }

    /**
     * Generate a JCL to purge user session
     */
    private void sendKickJCL() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "JCL";
            StringBuilder sb = new StringBuilder();
            sb.append(username);
            sb.append("S");
            String jobName = sb.toString().toUpperCase();
            sb.append(".JCL");
            File path = new File(PATH);
            String fileName = PATH + File.separator + sb.toString();
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                FileWriter fw = new FileWriter(fileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                // Following code is to generate the JCL file
                bw.append("//" + jobName + " JOB CLASS=A,MSGCLASS=A,MSGLEVEL=(1,1),NOTIFY=&SYSUID,\n");
                bw.append("// TIME=(,01),USER=" + username.toUpperCase() + ",PASSWORD=" + password.toUpperCase() + "\n");
                bw.append("//RUN EXEC PGM=SDSF,DYNAMNBR=150,REGION=1024K,TIME=5\n");
                bw.append("//ISFOUT DD SYSOUT=*\n");
                bw.append("//ISFIN DD *\n");
                bw.append(" OWNER " + username.toUpperCase() + "\n");
                bw.append(" ST " + username.toUpperCase() + "\n");
                bw.append(" FIND '" + username.toUpperCase() + "'\n");
                bw.append(" ++P\n");
                bw.append(" ST\n");
                bw.append("/*\n");
                bw.append("//");
                // Write the file
                bw.close();
                fw.close();
                Intent intent = new Intent();
                Uri uri = Uri.parse("file://" + fileName);
                intent.setData(uri);
                isInternal = true;
                new SubmitJCL().execute(intent);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.toast_file_create_fail), Toast.LENGTH_SHORT).show();
            }
            // When this file becomes useless, delete it
//            if (isDone) {
//                File file = new File(fileName);
//                if (file.isFile() && file.exists()) {
//                    file.delete();
//                }
//            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.title_chooseJcl)),
                    FILE_SELECT_CODE
            );
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.toast_filemanNotFound), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            isInternal = false;
            new SubmitJCL().execute(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}
