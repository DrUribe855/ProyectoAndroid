package com.example.apppreguntas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.apppreguntas.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetalleCuestionario extends AppCompatActivity {

    Config config;
    LinearLayout linearResumen;
    LinearLayout linearPreguntas;
    String user_id;
    String cuestionario_id;
    TextView etq_nombres;
    Integer datos;
    TextView pregunta;
    TextView correctas;
    TextView incorrectas;
    Integer contador_correctas = 0;
    Integer contador_incorrectas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cuestionario);

        config = new Config(getApplicationContext());
        linearResumen = findViewById(R.id.linearResumen);
        linearPreguntas = findViewById(R.id.linearPreguntas);

        SharedPreferences archivo = getSharedPreferences("app-preguntas", MODE_PRIVATE);
        user_id = archivo.getString("id_usuario", null);

        Bundle datos = getIntent().getExtras();
        cuestionario_id = datos.getString("id");

        etq_nombres = findViewById(R.id.etq_nombres);
        etq_nombres.setText(archivo.getString("nombres", ""));

        Date fecha_actual = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String fechaFormateada = sdf.format(fecha_actual);

        TextView fecha_inicio = new TextView(getApplicationContext());
        fecha_inicio.setText("Fecha inicio: " + fechaFormateada);
        fecha_inicio.setTextColor(Color.parseColor("#000000"));
        linearResumen.addView(fecha_inicio);

        extraerDetalleCuestionario();
    }

    public void extraerDetalleCuestionario(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = config.getEndpoint("API-Preguntas/getDetalleCuestionario.php?id_cuestionario="+ cuestionario_id);

        JsonObjectRequest solicitud =  new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("El servidor responde OK");
                System.out.println(response.toString());
                System.out.println(response);
                getDetalleCuestionarios(response);
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

    public void getDetalleCuestionarios(JSONObject data){
        try {
            JSONArray array = data.getJSONArray("respuesta");
            for (int i = 0; i < array.length(); i++){
                JSONObject detalleCuestionario = array.getJSONObject(i);
                JSONObject preguntas = detalleCuestionario.getJSONObject("pregunta");
                JSONArray opciones = detalleCuestionario.getJSONArray("opciones");

                String id = preguntas.getString("id");
                String descripcion = preguntas.getString("descripcion");
                String status = preguntas.getString("estado");
                String respuesta = preguntas.getString("respuesta");

                CardView cardView = new CardView(getApplicationContext());
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(0,0,0,16);
                cardView.setLayoutParams(cardParams);
                cardView.setPadding(10,0,0,0);
                cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                cardView.setRadius(8);
                cardView.setCardElevation(20);

                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                layout.setPadding(16,16,16,16);

                datos = array.length();
                pregunta = new TextView(getApplicationContext());
                correctas = new TextView(getApplicationContext());
                incorrectas = new TextView(getApplicationContext());
                TextView pregunta_opciones = new TextView(getApplicationContext());

                pregunta.setTextColor(Color.rgb(0,0,0));
                pregunta_opciones.setTextColor(Color.rgb(0,0,0));
                pregunta_opciones.setTextSize(18);
                pregunta_opciones.setText("Pregunta: " + i + "\n" + descripcion + "\n");

                layout.addView(pregunta_opciones);

                for(int j = 0; j < opciones.length(); j++ ){
                    JSONObject objeto = opciones.getJSONObject(j);
                    String descrip = objeto.getString("descripcion");

                    TextView opcion = new TextView(getApplicationContext());
                    opcion.setTextColor(Color.rgb(0,0,0));
                    opcion.setText("- " + descrip);
                    opcion.setTextSize(15);
                    opcion.setTypeface(null, Typeface.ITALIC);
                    opcion.setPadding(0,0,0,0);

                    if(status.equalsIgnoreCase("OK") && descrip.equalsIgnoreCase(respuesta)){
                        opcion.setTextColor(Color.rgb(0,255,0));
                        correctas.setTextColor(Color.rgb(0,255,0));
                        contador_correctas += 1;
                    }else if(status.equalsIgnoreCase("ERROR") && descripcion.equalsIgnoreCase(respuesta)){
                        opcion.setTextColor(Color.rgb(255,0,0));
                        incorrectas.setTextColor(Color.rgb(255,0,0));
                        contador_incorrectas +=1;
                    }

                    layout.addView(opcion);
                }

                cardView.addView(layout);
                linearPreguntas.addView(cardView);
            }

            pregunta.append("Preguntas: " + datos);
            correctas.append("Correctas: " + contador_correctas);
            incorrectas.append("Incorrectas: " + contador_incorrectas);

            linearResumen.addView(pregunta);
            linearResumen.addView(correctas);
            linearResumen.addView(incorrectas);



        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }

    public void back(View view){
        Intent intention = new Intent(getApplicationContext(), Resumen.class);
        startActivity(intention);
        finish();
    }
}