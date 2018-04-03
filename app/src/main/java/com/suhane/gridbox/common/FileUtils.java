package com.suhane.gridbox.common;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by shashanksuhane on 03/04/18.
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean isFileExist(Context context, String fileName) {
        try {
            context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean writeToFile(Context context, String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "Fail to write file : " + e.toString());
            return false;
        }
        return true;
    }

    public static String readFromFile(Context context, String fileName) {
        String data = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                data = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Fail to read file: " + e.toString());
        }

        return data;
    }
}
