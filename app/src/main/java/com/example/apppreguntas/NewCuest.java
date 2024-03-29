package com.example.apppreguntas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apppreguntas.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewCuest extends AppCompatActivity {
    Config config;
    TextView fecha_inicio;
    TextView names;
    String user_id;
    String name;
    String save_id;
    String fechaFormateada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cuest);

        config = new Config(getApplicationContext());
        fecha_inicio = findViewById(R.id.fecha_inicio);
        names = findViewById(R.id.names);

        SharedPreferences archivo = getSharedPreferences("app-preguntas", MODE_PRIVATE);
        user_id = archivo.getString("id_usuario", null);
        name = archivo.getString("nombres", null);

        save_id = user_id;

        Date fecha_actual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        fechaFormateada = sdf.format(fecha_actual);

        fecha_inicio.setText(fechaFormateada);
        names.setText(name);

        System.out.println("id: " + user_id);
        System.out.println("Fecha: " + fechaFormateada);


    }

    public void insertCuestionario(View view){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = config.getEndpoint("API-Preguntas/createCuestionario.php");
        Integer cant_preguntas = 0;
        Integer cant_ok = 0;
        Integer cant_error = 0;
        String fecha_fin = "0000-00-00 00:00:00";

        StringRequest solicitud =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("El servidor POST responde OK");
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println(response);
                    Intent intention = new Intent(getApplicationContext(), Pregunta.class);
                    intention.putExtra("id_user", save_id);
                    intention.putExtra("name", name);

                    startActivity(intention);

                } catch (JSONException e) {
                    System.out.println("El servidor POST responde con un error:");
                    System.out.println(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("El servidor POST responde con un error:");
                System.out.println(error.getMessage());
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_usuario", user_id);
                params.put("cant_preguntas", cant_preguntas.toString());
                params.put("cant_ok", cant_ok.toString());
                params.put("cant_error", cant_error.toString());
                params.put("fecha_fin", fecha_fin.toString());

                return params;
            }
        };

        queue.add(solicitud);
    }
}