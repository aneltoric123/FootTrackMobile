package com.example.foottrackmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTekmaActivity extends AppCompatActivity {

    private RequestQueue queue;

    private Spinner spinnerDomaca, spinnerGostujoca;
    private EditText etGoliDomaci, etGoliGostujoca;

    // Maps team name -> id
    private Map<String, Integer> teamsMap = new HashMap<>();

    private final String URL = "https://foottrack-dabec2gyhffmarg3.switzerlandnorth-01.azurewebsites.net/api/tekme";
    private final String TEAMS_URL = "https://foottrack-dabec2gyhffmarg3.switzerlandnorth-01.azurewebsites.net/api/tekme/teams";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tekma);

        queue = Volley.newRequestQueue(this);

        spinnerDomaca = findViewById(R.id.spinnerDomaca);
        spinnerGostujoca = findViewById(R.id.spinnerGostujoca);
        etGoliDomaci = findViewById(R.id.etGoliDomaci);
        etGoliGostujoca = findViewById(R.id.etGoliGostujoca);

        findViewById(R.id.btnSave).setOnClickListener(v -> saveTekma());

        loadTeams();
    }

    private void loadTeams() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                TEAMS_URL,
                null,
                response -> {
                    ArrayList<String> teamNames = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String name = obj.getString("ime");
                            int id = obj.getInt("ekipaId");
                            teamNames.add(name);
                            teamsMap.put(name, id);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDomaca.setAdapter(adapter);
                    spinnerGostujoca.setAdapter(adapter);
                },
                error -> Log.e("Teams API", error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<>();
                headers.put("ApiKey", getString(R.string.api_key));
                return headers;
            }
        };

        queue.add(request);
    }

    private void saveTekma() {
        try {
            String domacaName = spinnerDomaca.getSelectedItem().toString();
            String gostName = spinnerGostujoca.getSelectedItem().toString();

            int domacaId = teamsMap.get(domacaName);
            int gostId = teamsMap.get(gostName);
            int goliDomaci = Integer.parseInt(etGoliDomaci.getText().toString());
            int goliGost = Integer.parseInt(etGoliGostujoca.getText().toString());

            JSONObject body = new JSONObject();
            body.put("datum", "2026-01-10T14:30:00");
            body.put("domacaEkipaId", domacaId);
            body.put("gostujocaEkipaId", gostId);
            body.put("stadionId", 1);
            body.put("krogId",2);
            body.put("domacaGol", goliDomaci);
            body.put("gostujocaGol", goliGost);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    body,
                    response -> {
                        Toast.makeText(this, "Tekma dodana ✅", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> {
                        Toast.makeText(this, "Napaka ❌", Toast.LENGTH_SHORT).show();
                        Log.e("POST", error.toString());
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> headers = new HashMap<>();
                    headers.put("ApiKey", getString(R.string.api_key));
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
