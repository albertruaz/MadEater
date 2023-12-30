package com.example.tab_layout;

import android.content.Context;
import android.os.ParcelFileDescriptor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;

public class FileHelper {

    // JSON 파일에서 데이터 읽어오기
    public static String readJsonFromFile(Context context, int resourceId) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = context.getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    // JSON 파일로 데이터 쓰기
    public static void writeJsonToFile(Context context, int resourceId, String jsonData) throws IOException {
        try {
            ParcelFileDescriptor pfd = context.getResources().openRawResourceFd(resourceId).getParcelFileDescriptor();
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
            writer.write(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
