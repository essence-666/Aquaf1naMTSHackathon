package ru.home.mtsapplication.ui.voice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.home.mtsapplication.R;
import ru.home.mtsapplication.activities.MainActivity;
import ru.home.mtsapplication.models.RequestModel;
import ru.home.mtsapplication.record.Record;
import ru.home.mtsapplication.ui.requests.RequestCallback;

public class VoiceFragment extends Fragment {

    private DatabaseReference requestsRef;
    private FirebaseDatabase database;
    private Button recordVoiceBtn, sendVoiceBtn;
    private TextView voiceText;
    private Record voiceRecorder;

    public VoiceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        recordVoiceBtn = view.findViewById(R.id.button_record_voice);
        sendVoiceBtn = view.findViewById(R.id.button_send_voice);
        voiceText = view.findViewById(R.id.text_voice_input);

        checkPermissions();

        voiceRecorder = new Record(requireContext(), recordVoiceBtn, sendVoiceBtn);

        recordVoiceBtn.setOnClickListener(r -> {
            try {
                voiceRecorder.startRecording();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка при записи", Toast.LENGTH_SHORT).show();
            }
        });

        sendVoiceBtn.setOnClickListener(r -> {
            voiceRecorder.sendVoiceToAPI(new RequestCallback() {
                @Override
                public void onSuccess(RequestModel request) {
                    Log.d("API_RESPONSE", "Заявка: " + request.getTopic());
                    saveVoiceRequest(request);
                }

                @Override
                public void onError(String error) {
                    Log.e("API_ERROR", error);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void saveVoiceRequest(RequestModel requestModel) {
        database = FirebaseDatabase.getInstance("https://mtsapplication-ec85d-default-rtdb.firebaseio.com/");
        requestsRef = database.getReference("requests");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String requestId = requestsRef.child(userId).push().getKey();

        requestModel.setUserEmail(userEmail);

        if (requestId != null) {
            requestsRef.child(userId).child(requestId).setValue(requestModel)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Запрос сохранен");
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Ошибка при сохранении: " + e.getMessage()));
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO}, 200);
        }
    }
}
