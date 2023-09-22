package com.cocosorority.cocobackend.utils;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.SneakyThrows;

@Service
public class GoogleSheetService {

    @SneakyThrows
    public ValueRange getRangeValue(String sheetId, String range) {
        Sheets service = getService();
        return service.spreadsheets().values().get(sheetId, range).execute();
    }

    @SneakyThrows
    public Sheets getService() {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(
                Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("coco")
                .build();
    }
}
