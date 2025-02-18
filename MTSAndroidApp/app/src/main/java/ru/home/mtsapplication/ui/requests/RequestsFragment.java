package ru.home.mtsapplication.ui.requests;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import ru.home.mtsapplication.R;
import ru.home.mtsapplication.activities.LoginActivity;
import ru.home.mtsapplication.activities.MainActivity;
import ru.home.mtsapplication.models.RequestModel;
import ru.home.mtsapplication.record.Record;
import ru.home.mtsapplication.static_info.RequestInfo;

public class RequestsFragment extends Fragment {
    public RequestsFragment() {}

    //default elements
    Button sendRequestBtn;
    Spinner textTopic;
    Spinner textDetails;
    EditText textComments;


    //for database
    private FirebaseDatabase database;
    DatabaseReference requestsRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        sendRequestBtn = view.findViewById(R.id.button_send);
        textTopic = view.findViewById(R.id.spinner_theme);
        textDetails = view.findViewById(R.id.spinner_details);
        textComments = view.findViewById(R.id.edit_comment);



        // кастомный spinner_item.xml
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, RequestInfo.topics);
        topicAdapter.setDropDownViewResource(R.layout.spinner_item); // Применяем кастомный стиль

        ArrayAdapter<String> detailsAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, RequestInfo.details);
        detailsAdapter.setDropDownViewResource(R.layout.spinner_item);

        textTopic.setAdapter(topicAdapter);
        textDetails.setAdapter(detailsAdapter);

        //спросить у пользователя доступ к микро иначе вылетает прога!!!

        sendRequestBtn.setOnClickListener(request -> {
            saveRequest(textTopic.getSelectedItem().toString(),
                    textDetails.getSelectedItem().toString(),
                    textComments.getText().toString(),
                    false,
                    true);
        });


        return view;
    }


    public void saveRequest(String topic, String details, String comments, boolean spam, boolean important) {
        database = FirebaseDatabase.getInstance("https://mtsapplication-ec85d-default-rtdb.firebaseio.com/");
        requestsRef = database.getReference("requests");

        // Получаем текущего пользователя
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Генерируем уникальный ID заявки
        String requestId = requestsRef.child(userId).push().getKey();

        // Создаём объект заявки
        RequestModel request = new RequestModel(userEmail, topic, details, comments, spam, important);
        request.setFull(true);

        // Сохраняем в Firebase
        if (requestId != null) {

            requestsRef.child(userId).child(requestId).setValue(request)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Запрос сохранен");

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);

                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Ошибка при добавлении: " + e.getMessage()));
        }
    }


}
