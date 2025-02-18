package ru.home.mtsapplication.ui.admin.processed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.home.mtsapplication.R;
import ru.home.mtsapplication.models.RequestModel;
import ru.home.mtsapplication.ui.requests.RequestAdapter;

public class ProcessedRequestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<RequestModel> processedRequests;
    private DatabaseReference requestsRef;

    public ProcessedRequestsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processed_requests, container, false);

        recyclerView = view.findViewById(R.id.recycler_processed_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        processedRequests = new ArrayList<>();
        adapter = new RequestAdapter(processedRequests);
        recyclerView.setAdapter(adapter);

        // Получаем все запросы (так как админ видит все)
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        loadProcessedRequests();

        return view;
    }

    private void loadProcessedRequests() {
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                processedRequests.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) { // Перебираем пользователей
                    for (DataSnapshot requestSnapshot : userSnapshot.getChildren()) { // Перебираем заявки пользователя
                        RequestModel request = requestSnapshot.getValue(RequestModel.class);
                        if (request != null && request.isFull()) { // Фильтруем только isFull = true
                            processedRequests.add(request);
                        }
                    }
                }
                Collections.sort(processedRequests, (r1, r2) -> Boolean.compare(r2.isImportant(), r1.isImportant()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Ошибка загрузки данных: " + databaseError.getMessage());
            }
        });
    }
}
