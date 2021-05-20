package com.example.linhasdeonibuscwb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoBus extends AppCompatActivity {

    /*EditText nomeBus;
    TextView txtcod, txtnomebus, txtcartao, txtcateg, txtcor;*/
    ProgressDialog progressDialog;
    
    TextInputEditText codOUname;
    TextInputLayout codBus, nomeBus, cardS, categ, corBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bus);

        codOUname = findViewById(R.id.codOUname);
        codBus = findViewById(R.id.codBus);
        nomeBus = findViewById(R.id.nomeBus);
        cardS = findViewById(R.id.cardS);
        categ = findViewById(R.id.categ);
        corBus = findViewById(R.id.corBus);
       // textViewJson = findViewById(R.id.textViewJson);
    } // fim onCreate

    public void pesquisa (View v){

        String codeval = codOUname.getText().toString();

        if (codeval.length() > 0 && !codeval.equals("")){
            consultLinhasCWB consulta = new consultLinhasCWB();
            String onibus = "https://urbs-api.vercel.app/api/v1/routes/"+ codeval;
            consulta.execute(onibus);
            //
            }
        else{
            Toast.makeText(this, "Código invalido!", Toast.LENGTH_SHORT).show();
        }

    }

    // CLASSE ASYNCTASK REALIZAR CHAMADA WEB SERVICES
    protected class consultLinhasCWB extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer out = new StringBuffer();
                while ((line = br.readLine()) != null){
                    out.append(line + "\n");
                }
                is.close();

                return
                        out.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }//FIM DOINGBACKGROUND

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(InfoBus.this);
            progressDialog.setMessage("Aguarde...carregando dados");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String dados) {
            try {
                //textViewJson.setText(dados);
                parseJSON(dados);
                progressDialog.hide();

            }
            catch (Exception e) {
                Toast.makeText(InfoBus.this, "Falha na consulta", Toast.LENGTH_SHORT).show();
                progressDialog.hide();
               // e.printStackTrace();
            }
        }

        private void parseJSON(String data){
            try{
                if(data.contains("erro")){
                    Toast.makeText(InfoBus.this, "Falha na consulta", Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject jsonObject = new JSONObject(data);//JSONObject recebe o JSON da consulta
                    //Depois de pegar o conteúdo no JSON, preenche os textviews

                    codBus.getEditText().setText(jsonObject.getString("COD"));
                    nomeBus.getEditText().setText(jsonObject.getString("NOME"));
                    cardS.getEditText().setText(jsonObject.getString("SOMENTE_CARTAO"));
                    categ.getEditText().setText(jsonObject.getString("CATEGORIA_SERVICO"));
                    corBus.getEditText().setText(jsonObject.getString("NOME_COR"));
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }


    }//FIM ASYNCTASK
}

