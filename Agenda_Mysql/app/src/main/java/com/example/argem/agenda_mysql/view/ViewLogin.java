package com.example.argem.agenda_mysql.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.argem.agenda_mysql.Conexion;
import com.example.argem.agenda_mysql.R;
import com.example.argem.agenda_mysql.controller.UserDB;
import com.example.argem.agenda_mysql.model.Usuario;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class ViewLogin extends AppCompatActivity {

    Button btnRegistro;
    Button btnLogin;
    EditText email;
    EditText clave;
    ProgressBar pb;

    JSONArray jsonArray;
    UserDB user=new UserDB();
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.txtUsuarioL);
        clave = findViewById(R.id.txtPasswordL);
        btnLogin=findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);

        client = new AsyncHttpClient();
    }

    public void onClickLogin(View view)
    {
        try {
            final String pass = clave.getText().toString();
            final String em = email.getText().toString();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    obtenerUsuario(user.geUser(em, pass));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (user.getUsuario() != null && user.getUsuario().getId_usuario() > 0) {
                                Intent i = new Intent(getApplicationContext(), ViewPrincipal.class);
                                Bundle b = new Bundle();
                                b.putInt("id", user.getUsuario().getId_usuario());
                                b.putString("nombre", user.getUsuario().getNombre());
                                b.putString("apellido", user.getUsuario().getApellido());
                                b.putString("clave", user.getUsuario().getClave());
                                b.putString("correo", user.getUsuario().getCorreo());
                                i.putExtras(b);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(ViewLogin.this, "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                                clave.setText("");
                            }
                        }
                    });
                }
            };
            thread.start();
        }
        catch (Exception ex)
        {
            Toast.makeText(ViewLogin.this, "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
            clave.setText("");
        }
    }

    public void onClickRegistro(View view)
    {
        Intent i = new Intent(this, ViewUser.class);
        Bundle b=new Bundle();
        b.putBoolean("t", true);
        i.putExtras(b);
        startActivity(i);
    }

    public void obtenerUsuario(String response)
    {
        try {
            jsonArray = new JSONArray(response);
            for (int i=0; i< jsonArray.length();i++)
            {
                user.getUsuario().setId_usuario(jsonArray.getJSONObject(i).getInt("id_usuario"));
                user.getUsuario().setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                user.getUsuario().setApellido(jsonArray.getJSONObject(i).getString("apellido"));
                user.getUsuario().setClave(jsonArray.getJSONObject(i).getString("clave"));
                user.getUsuario().setCorreo(jsonArray.getJSONObject(i).getString("correo"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
