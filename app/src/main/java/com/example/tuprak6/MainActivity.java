package com.example.tuprak6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<User> users;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private Button loadMoreButton;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list of users
        users = new ArrayList<>(); // Make sure to initialize users
        userAdapter = new UserAdapter(this,users);
        recyclerView.setAdapter(userAdapter);

        loadMoreButton = findViewById(R.id.more);

        // Load the initial page
        loadUsers();

        loadMoreButton.setOnClickListener(v -> {
            // Increment page number and load next page of users
            page += 1;
            loadUsers();
        });
    }

    private void loadUsers() {
        Call<UserResponse> call = apiService.getUsers(page);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    // Get the list of users from the response
                    List<User> newUsers = response.body().getData();

                    // Add the new users to the existing list
                    users.addAll(newUsers);

                    // Notify the adapter that data has changed
                    userAdapter.notifyDataSetChanged();
                } else {
                    // Handle error case

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Handle failure case

            }
        });
    }
}