package com.example.argem.agenda_mysql;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button btnGuardar;
    EditText nombre, telefono, email;
    AsyncHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = findViewById(R.id.txtNombreE);
        telefono=findViewById(R.id.txtTelefonoE);
        email=findViewById(R.id.txtEmailE);
        btnGuardar=findViewById(R.id.btnGuardarC);

        client =new AsyncHttpClient();
    }

    public void guardar()
    {
        if(nombre.getText().toString().isEmpty() || telefono.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Campos en blanco", Toast.LENGTH_SHORT).show();
        }
        else {
            Persona persona = new Persona();
            persona.setNombre(nombre.getText().toString().replace(" ","%20"));
            persona.setTelefono(telefono.getText().toString());
            persona.setEmail(email.getText().toString());

            String dir= Conexion.url+"insertPerson.php?nombre=" + persona.getNombre() + "&telefono=" + persona.getTelefono() + "&email=" + persona.getEmail();
            try {
                client.post(dir, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == 200)
                        {
                            Toast.makeText(MainActivity.this, "El contacto a sido guardado", Toast.LENGTH_SHORT).show();
                            nombre.setText("");
                            telefono.setText("");
                            email.setText("");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(MainActivity.this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickGuardar( View view)
    {
        guardar();
    }

    public void viewListado(View view)
    {
        Intent i = new Intent(getApplicationContext(), EditData.class);
        startActivity(i);
    }
}
