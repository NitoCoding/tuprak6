package com.example.tuprak6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView nameTextView, emailTextView;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Inisialisasi view
        profileImageView = findViewById(R.id.iv_profile);
        nameTextView = findViewById(R.id.tv_name);
        emailTextView = findViewById(R.id.tv_email);
        progressBar = findViewById(R.id.progressBar);

        // Inisialisasi API service
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Ambil userId dari intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userId")) {
            int userId = intent.getIntExtra("userId", -1);
            if (userId != -1) {
                // Panggil metode fetchUserData dengan userId
                progressBar.setVisibility(View.VISIBLE);
                profileImageView.setVisibility(View.GONE);
                nameTextView.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
                fetchUserData(userId);
            }
        }
    }

    private void fetchUserData(int userId) {
        // Buat executor untuk thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Jalankan permintaan API di thread latar belakang
        executor.execute(() -> {
            Call<DetailUserResponse> call = apiService.getUser(userId);
            call.enqueue(new Callback<DetailUserResponse>() {
                @Override
                public void onResponse(@NonNull Call<DetailUserResponse> call, @NonNull Response<DetailUserResponse> response) {
                    // Jalankan kode pada thread utama

                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        profileImageView.setVisibility(View.VISIBLE);
                        nameTextView.setVisibility(View.VISIBLE);
                        emailTextView.setVisibility(View.VISIBLE);
                        if (response.isSuccessful() && response.body() != null) {
                            // Dapatkan data user dari respons
                            User userData = response.body().getData();

                            // Perbarui UI dengan data yang diterima
                            if (userData != null) {
                                Picasso.get().load(userData.getAvatar()).into(profileImageView);
                                nameTextView.setText(userData.getFirst_name() + " " + userData.getLast_name());
                                emailTextView.setText(userData.getEmail());
                            }
                        } else {
                            // Tangani kesalahan respons
                            Log.e("DetailActivity", "Error fetching user data: " + response.errorBody());
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<DetailUserResponse> call, @NonNull Throwable t) {
                    // Tangani kegagalan permintaan API
                    // Jalankan kode pada thread utama
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Log.e("DetailActivity", "Network error fetching user data: " + t.getMessage());
                    });
                }
            });
        });
    }
}
