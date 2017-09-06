package com.successive.mupdfdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private AppCompatButton openFileManagerBtn;
    private Toolbar toolbar;
    private RelativeLayout pdfViewRL;
    private TextView title, msgTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Marshmallow Runtime Permissions
        loadPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        openFileManagerBtn = (AppCompatButton) findViewById(R.id.open_btn);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        msgTxt = (TextView) findViewById(R.id.msgTxt);
        title.setVisibility(View.GONE);
        pdfViewRL = (RelativeLayout) findViewById(R.id.pdfViewRL);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        openFileManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFileOnClick(view);
            }
        });
    }

    private void displayPDF(String path) throws Exception {

        /*Reading file from URI*/
        byte buffer[] = null;
        Uri uri = Uri.parse(path);
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            int len = is.available();
            buffer = new byte[len];
            is.read(buffer, 0, len);
            is.close();
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory during buffer reading");
        } catch (Exception e) {
            System.out.println("Exception reading from stream: " + e);
            try {
                Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                if (cursor.moveToFirst()) {
                    String str = cursor.getString(0);
                    if (str == null) {
                    } else {
                        uri = Uri.parse(str);
                    }
                }
            } catch (Exception e2) {
                System.out.println("Exception in Transformer Prime file manager code: " + e2);
            }
        }

        MuPDFCore core = new MuPDFCore(getApplicationContext(), buffer, null);
        MuPDFReaderView mDocView = new MuPDFReaderView(getApplicationContext());
        mDocView.setAdapter(new MuPDFPageAdapter(getApplicationContext(), null, core));
        pdfViewRL.addView(mDocView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));


        /*Use this if you want to open PDF outside app using MuPDF's own Activity*/

        /*Intent intent = new Intent(getApplicationContext(), MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(path));
        startActivity(intent);*/


    }

    @Override
    public void onActivityResult(int reqCode, int result, Intent intent) {
        if (reqCode == 1) {
            if (result == RESULT_OK) {
                Uri data = intent.getData();
                Log.i("Main", "File path: " + data.getPath());
                Log.i("Main", "File name: " + data.getLastPathSegment());
                if (data.getLastPathSegment() != null) {
                    title.setVisibility(View.VISIBLE);
                    title.setText(data.getLastPathSegment());
                    msgTxt.setVisibility(View.GONE);
                }
                try {


                    displayPDF(data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pickFileOnClick(View v) {
        //Method to open pdf files
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1);
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    // Code Added
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("STATUS", "Permission Granted!");

                } else {
                    // not granted

                }
                return;
            }

        }
    }
}