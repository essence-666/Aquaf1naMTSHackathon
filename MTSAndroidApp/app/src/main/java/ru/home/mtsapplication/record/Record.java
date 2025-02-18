package ru.home.mtsapplication.record;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import ru.home.mtsapplication.models.RequestModel;
import ru.home.mtsapplication.ui.requests.RequestCallback;

import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class Record {

    private String audioFilePath;
    private MediaRecorder recorder;
    private Context context;


    private Button recordVoiceBtn, sendVoiceBtn;

    public Record(Context context, Button recordVoiceBtn, Button sendVoiceBtn) {
        this.context = context;
        this.recordVoiceBtn = recordVoiceBtn;
        this.sendVoiceBtn = sendVoiceBtn;
    }

    public void startRecording() throws IOException {
        try {
            File audioFile = File.createTempFile("voice_", ".3gp", context.getCacheDir());
            audioFilePath = audioFile.getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(audioFilePath);
            recorder.prepare();
            recorder.start();

            Toast.makeText(context, "Запись началась...", Toast.LENGTH_SHORT).show();

            // Останавливаем запись через 5 секунд
            recordVoiceBtn.postDelayed(this::stopRecording, 10000);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }
    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            recorder.release();
            recorder = null;

            sendVoiceBtn.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Запись завершена", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendVoiceToAPI(RequestCallback callback) {
        try {
            FileInputStream fis = new FileInputStream(audioFilePath);
            byte[] audioBytes = new byte[(int) new File(audioFilePath).length()];
            fis.read(audioBytes);
            fis.close();

            String encodedAudio = Base64.encodeToString(audioBytes, Base64.NO_WRAP);

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", "1213123");
            jsonBody.put("data", encodedAudio);


            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://aquaf1na.fun:5000/ml";

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            String topic = response.getString("topic");
                            String details = response.getString("details");
                            String comments = response.getString("comments");
                            boolean spam = response.getBoolean("spam");
                            boolean important = response.getBoolean("important");
                            RequestModel request = new RequestModel(
                                    topic,
                                    details,
                                    comments,
                                    spam,
                                    important
                            );
                            if (details.isEmpty()) request.setFull(false);


                            callback.onSuccess(request);
                        } catch (JSONException e) {
                            callback.onError("Ошибка обработки JSON: " + e.getMessage());
                        }
                    },
                    error -> {
                        String errorMessage = "Ошибка API: ";

                        if (error.networkResponse != null) {
                            errorMessage += "Код ответа: " + error.networkResponse.statusCode + "\n";
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                errorMessage += "Ответ сервера: " + responseBody;
                            } catch (Exception e) {
                                errorMessage += "Ошибка при разборе ответа.";
                            }
                        } else {
                            errorMessage += "Ошибка сети: " + error.toString();
                        }

                        Log.e("API_ERROR", errorMessage);
                        callback.onError(errorMessage);
                    }


            );
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    40000, // 30 секунд ожидания
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            queue.add(jsonRequest);
        } catch (IOException | JSONException e) {
            callback.onError("Ошибка при обработке файла: " + e.getMessage());
        }
    }


}
