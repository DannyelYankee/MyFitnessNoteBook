package com.example.myfitnessnotebook;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class phpRegistro extends Worker {


    public phpRegistro(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String user = getInputData().getString("user");
        String password = getInputData().getString("password");
        String server = "http://ec2-18-132-60-229.eu-west-2.compute.amazonaws.com/djuape001/WEB/registro.php";

        return null;
    }
}
