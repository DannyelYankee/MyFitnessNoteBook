package com.example.myfitnessnotebook;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class phpInsertEjercicio extends Worker {
    public phpInsertEjercicio(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String user = getInputData().getString("user");
        String rutina = getInputData().getString("rutina");
        String nombre = getInputData().getString("nombre");
        String numSeries = getInputData().getString("numSeries");
        String numRepes = getInputData().getString("numRepes");
        String peso = getInputData().getString("peso");


        String server = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/djuape001/WEB/addEjercicio.php";
        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(server);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("user", user).appendQueryParameter("rutina", rutina).appendQueryParameter("nombre", nombre).appendQueryParameter("numSeries", numSeries).appendQueryParameter("numRepes", numRepes).appendQueryParameter("peso", peso);
            String parametros = builder.build().getEncodedQuery();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                JSONArray jsonArray = new JSONArray(result);
                String resultado = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    resultado = jsonArray.getJSONObject(i).getString("resultado");
                }

                Data datos;
                if (resultado.equals("false")) {
                    datos = new Data.Builder().putBoolean("exito", false).build();
                } else {
                    datos = new Data.Builder().putBoolean("exito", true).build();
                }
                return Result.success(datos);
            } else {
                return Result.retry();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }
}
