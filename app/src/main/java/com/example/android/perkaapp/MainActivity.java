package com.example.android.perkaapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText mFirstName, mLastName, mEMail, mPosition, mExplanation, mProject, mSource, mResume;
    Button mSubmit;
    String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirstName = (EditText) findViewById(R.id.firstName);
        mLastName = (EditText) findViewById(R.id.lastName);
        mEMail = (EditText) findViewById(R.id.eMail);
        mPosition = (EditText) findViewById(R.id.position);
        mExplanation = (EditText) findViewById(R.id.explanation);
        mProject = (EditText) findViewById(R.id.projects);
        mSource = (EditText) findViewById(R.id.source);
        mResume = (EditText) findViewById(R.id.resume);
        mSubmit = (Button) findViewById(R.id.submitButton);

//        mUrl = "https://api.perka.com/1/communication/job/apply";
mUrl = "http://requestb.in/1n0vgdy1";
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 302);
            }
        });


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();

            }
        });


    }

    public void getData() {
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String eMail = mEMail.getText().toString();
        String position = mPosition.getText().toString();
        String project = mProject.getText().toString();
        String source = mSource.getText().toString();
        String resume = mResume.getText().toString();


        String explanation = mExplanation.getText().toString();

        JSONObject data = new JSONObject();

        try {
            data.put("first_name", firstName);
            data.put("last_name", lastName);
            data.put("email", eMail);
            data.put("position_id", position);
            data.put("explanation", explanation);
            data.put("projects", project);
            data.put("source", source);
            data.put("resume", resume);

            Log.d("JSon", data.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (data.length() > 0) {
            new sendData().execute(String.valueOf(data));
            //call to async class
        }

    }

    class sendData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(mUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(params[0]);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }



            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error closing stream", "e");
                    }
                }
            }
            return null;
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Uri selectedFileUri = data.getData();

            Log.d("hmm", selectedFileUri.getPath());
//            File file=new File(selectedFileUri.getPath());
//            try {
//                encodeFileToBase64Binary(file);
//                Log.d("hmm2", "worked");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d("hmm2", "didn't work");
//            }

            convertFileToString(selectedFileUri.getPath());
//            convertFileToString("/storage/emulated/0/Download/ResumeGA2.pdf");

        }

    }

    public String convertFileToString(String pathOnSdCard){
        Log.d("test", "why wont you work1?");

        String strFile=null;
        File file=new File(pathOnSdCard);
        try {
            byte[] data = FileUtils.readFileToByteArray(file);//Convert any file, image or video into byte array
            strFile = Base64.encodeToString(data, Base64.NO_WRAP);//Convert byte array into string
            Log.d("test", "why wont you work2?");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test", "why wont you work3?");
        }
        return strFile;
    }

    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(fileName);
        byte[] encoded = Base64.encode(bytes,-1);

        String encodedString = new String(encoded);
        return encodedString;
    }

}
