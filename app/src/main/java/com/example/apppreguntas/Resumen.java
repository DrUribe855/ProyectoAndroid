package com.example.apppreguntas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apppreguntas.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Resumen extends AppCompatActivity {

    String user_id, name;
    TextView user_name;

    LinearLayout principalLinear;
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        user_name = findViewById(R.id.nombre_usuario);
        config = new Config(getApplicationContext());
        SharedPreferences archivo = getSharedPreferences("app-preguntas", MODE_PRIVATE);
        user_id = archivo.getString("id_usuario", null);
        name = archivo.getString("nombres", null);
        user_name.append(name);
        principalLinear = findViewById(R.id.principalLinear);

        this.getCuestionarios();
    }

    public void getCuestionarios(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = config.getEndpoint("API-Preguntas/getCuestionarios.php?id_usuario=" + this.user_id);

        JsonObjectRequest solicitud =  new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("El servidor responde OK");
                System.out.println(response.toString());
                System.out.println(response);
                printCuestionarios(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("El servidor responde con un error:");
                System.out.println(error.getMessage());
            }
        });

        queue.add(solicitud);
    }

    public void printCuestionarios(JSONObject data){
        try {
            JSONArray array = data.getJSONArray("cuestionarios");
            for (int i = 0; i < array.length(); i++){
                JSONObject cuestionario = array.getJSONObject(i);
                String id = cuestionario.getString("id");
                String fecha_inicio = cuestionario.getString("fecha_inicio");
                String cantidad_preguntas = cuestionario.getString("cant_preguntas");
                String preguntas_ok = cuestionario.getString("cant_ok");
                String preguntas_error = cuestionario.getString("cant_error");

                //Creacion de la carta (cardview)

                CardView cardView = new CardView(getApplicationContext());
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0,0,0,32);
                cardView.setLayoutParams(cardParams);
                cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                cardView.setRadius(8); // Esquinas
                cardView.setCardElevation(20); // Elevacion de la carta

                LinearLayout cardLayout = new LinearLayout(getApplicationContext());
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                cardLayout.setPadding(16,16,16,16);
                TextView card = new TextView(getApplicationContext());
                card.setTextColor(Color.rgb(0,0,0));
                card.setPadding(16,16,16,16);
                card.setText("Número: " + id + "\n" + "Fecha inicio: " + fecha_inicio + "\n" + "N° Preguntas: " + cantidad_preguntas + "\n" + "N° OK: " + preguntas_ok + "\n" + "N° error: " + preguntas_error );

                Button detail = new Button(getApplicationContext());
                detail.setText("Detalles");
                detail.setBackgroundColor(getColor(R.color.inputs));
                LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                detailParams.gravity = Gravity.CENTER;
                detail.setLayoutParams(detailParams);

                detail.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intention = new Intent(getApplicationContext(), DetalleCuestionario.class);
                        intention.putExtra("id", id);
                        startActivity(intention);
                    }
                });

                cardLayout.addView(card);
                cardLayout.addView(detail);

                cardView.addView(cardLayout);

                principalLinear.addView(cardView);


            }
        }catch(JSONException e){
            throw new RuntimeException();
        }
    }

    public void logout(View view){
        SharedPreferences archivo = getSharedPreferences("app-preguntas", MODE_PRIVATE);
        SharedPreferences.Editor editor = archivo.edit();

        editor.clear();
        editor.clear();

        editor.commit();
        Intent intention = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intention);
        finish();
    }

    public void newCuestionario(View view){
        SharedPreferences archivo = getSharedPreferences("app-preguntas", MODE_PRIVATE);
        SharedPreferences.Editor editor = archivo.edit();

        editor.putString("id_usuario", user_id);
        editor.putString("nombres", name);

        editor.commit();

        Intent intention = new Intent(getApplicationContext(), NewCuest.class );
        startActivity(intention);
        finish();
    }
}