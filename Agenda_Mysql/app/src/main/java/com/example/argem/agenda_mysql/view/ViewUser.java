package com.example.argem.agenda_mysql.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.argem.agenda_mysql.Conexion;
import com.example.argem.agenda_mysql.R;
import com.example.argem.agenda_mysql.controller.UserDB;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class ViewUser extends AppCompatActivity {

    // true realiza insercion
    // false realiza actualizacion
    boolean transaccion;

    UserDB userDB=new UserDB();

    EditText txtNombre;
    EditText txtApellido;
    EditText txtClave;
    EditText txtCorreo;
    TextView leyenda;
    Button btnAddOrUpdate;
    Button btnCancel;

    AsyncHttpClient client;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtNombre=findViewById(R.id.txtNombreU);
        txtApellido=findViewById(R.id.txtApellidoU);
        txtClave=findViewById(R.id.txtClaveU);
        txtCorreo=findViewById(R.id.txtCorreoU);

        leyenda=findViewById(R.id.textView6);
        btnAddOrUpdate=findViewById(R.id.btnAddUser);
        btnCancel = findViewById(R.id.btnCancelUser);

        bundle=getIntent().getExtras();
        if (bundle != null)
        {
            transaccion = bundle.getBoolean("t");
            if (transaccion==false) {
                leyenda.setText("Actualización Datos");
                btnAddOrUpdate.setText("Actualizar");
                cargarDatos();
            }
        }
    }

    public void onClickAddOrUpdate(View view)
    {
        if (transaccion)
        {
            UserDB u = new UserDB();
            u.getUsuario().setNombre(txtNombre.getText().toString());
            u.getUsuario().setApellido(txtApellido.getText().toString());
            u.getUsuario().setClave(txtClave.getText().toString());
            u.getUsuario().setCorreo(txtCorreo.getText().toString());

            client = new AsyncHttpClient();
            String dir= Conexion.url+"user/insertUser.php?nombre=" + u.getUsuario().getNombre().replace(" ", "%20")
                    + "&apellido=" + u.getUsuario().getApellido().replace(" ", "%20")
                    + "&clave=" + u.getUsuario().getClave()
                    + "&email=" +u.getUsuario().getCorreo();
            try {
                client.post(dir, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode==200) {
                            Toast.makeText(ViewUser.this, "El usuario a sido creado", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(ViewUser.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Toast.makeText(ViewUser.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            userDB.getUsuario().setNombre(txtNombre.getText().toString());
            userDB.getUsuario().setApellido(txtApellido.getText().toString());
            userDB.getUsuario().setClave(txtClave.getText().toString());
            userDB.getUsuario().setCorreo(txtCorreo.getText().toString());

            client = new AsyncHttpClient();
            try {
                String dir= Conexion.url+"user/updateUser.php?id=" + userDB.getUsuario().getId_usuario()
                        + "&nombre=" + userDB.getUsuario().getNombre().replace(" ", "%20")
                        + "&apellido=" + userDB.getUsuario().getApellido().replace(" ", "%20")
                        + "&clave="+ URLEncoder.encode(userDB.getUsuario().getClave(), "UTF-8")
                        + "&email=" + userDB.getUsuario().getCorreo();
                client.post(dir, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            bundle.putString("nombre", txtNombre.getText().toString());
                            bundle.putString("apellido", txtApellido.getText().toString());
                            bundle.putString("clave", txtClave.getText().toString());
                            bundle.putString("correo", txtCorreo.getText().toString());
                            Toast.makeText(ViewUser.this, "Datos de usuario actualizados", Toast.LENGTH_SHORT).show();
                            Intent i =new Intent(getApplicationContext(), ViewPrincipal.class);
                            i.putExtras(bundle);
                            startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(ViewUser.this, "No se pudo actualizar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Toast.makeText(ViewUser.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickCancel(View view)
    {
        finish();
    }

    public void cargarDatos()
    {
        userDB.getUsuario().setId_usuario(bundle.getInt("id"));
        userDB.getUsuario().setNombre(bundle.getString("nombre"));
        userDB.getUsuario().setApellido(bundle.getString("apellido"));
        userDB.getUsuario().setCorreo(bundle.getString("correo"));
        userDB.getUsuario().setClave(bundle.getString("clave"));

        txtNombre.setText(userDB.getUsuario().getNombre());
        txtApellido.setText(userDB.getUsuario().getApellido());
        txtClave.setText(userDB.getUsuario().getClave());
        txtCorreo.setText(userDB.getUsuario().getCorreo());
    }
}
