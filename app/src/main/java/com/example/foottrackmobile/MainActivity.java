package com.example.foottrackmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    RecyclerView recyclerView;
    TekmaAdapter adapter;
    ArrayList<Tekma> tekmeList = new ArrayList<>();
    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat input =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            SimpleDateFormat output =
                    new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            Date date = input.parse(isoDate);
            return output.format(date);

        } catch (ParseException e) {
            return isoDate;
        }
    }
    private String url ="https://foottrack-dabec2gyhffmarg3.switzerlandnorth-01.azurewebsites.net/api/tekme";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TekmaAdapter(tekmeList);
        recyclerView.setAdapter(adapter);
    }
    private Response.Listener<JSONArray> jsonArrayListener =response ->{
            tekmeList.clear();
            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object = response.getJSONObject(i);
                    Tekma t = new Tekma();
                    t.tekmaId = object.getInt("tekmaId");
                    t.datum = formatDate(object.getString("datum"));
                    t.domacaGol = object.getInt("domacaGol");
                    t.gostujocaGol = object.getInt("gostujocaGol");
                    t.domacaIme = object.getString("domacaEkipaIme");
                    t.gostIme = object.getString("gostujocaEkipaIme");
                    t.stadionIme = object.getString("stadionIme");
                    tekmeList.add(t);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            adapter.notifyDataSetChanged();
    };


    public  void prikaziTekme(View view){
        if (view != null){
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,url, null,jsonArrayListener, errorListener)
            {
                @Override
                public Map<String,String> getHeaders()
                {
                    Map<String, String> params = new HashMap<>();
                    params.put("ApiKey", getString(R.string.api_key));
                    return params;
                }
            };
            requestQueue.add(request);
        }
    }
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.toString());
        }
    };
    public void openAddTekma(View view) {
        startActivity(new Intent(this, AddTekmaActivity.class));
    }
}
