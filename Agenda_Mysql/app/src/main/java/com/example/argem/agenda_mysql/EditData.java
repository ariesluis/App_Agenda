package com.example.argem.agenda_mysql;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONArray;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EditData extends AppCompatActivity {

    AsyncHttpClient client;
    ListView lst;
    EditText nombre, telefono, email;
    Button btnActualizar, btnDelete;

    Persona p;
    ArrayList<Persona> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        nombre=findViewById(R.id.txtNombreE);
        telefono=findViewById(R.id.txtTelefonoE);
        email=findViewById(R.id.txtEmailE);
        lst=findViewById(R.id.lsvPersonas);
        client =new AsyncHttpClient();
        btnActualizar=findViewById(R.id.btnUpdate);
        btnDelete=findViewById(R.id.btnDelete);

        listPerson();
    }

    public void listPerson()
    {
        lst.setFocusable(true);

        nombre.setText("");
        telefono.setText("");
        email.setText("");
        nombre.setEnabled(false);
        telefono.setEnabled(false);
        email.setEnabled(false);
        btnActualizar.setEnabled(false);
        btnDelete.setEnabled(false);

        String dir= Conexion.url+"listPerson.php";
        try {
            client.post(dir, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == 200)
                    {
                        listarDatos(new String(responseBody));
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(EditData.this, "Error al listar los contactos", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error al listar los contactos", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPerson(String response)
    {
        try
        {
            JSONArray jsonArray=new JSONArray(response);
            for (int i=0; i < jsonArray.length();i++)
            {
                p=new Persona();
                p.setId(jsonArray.getJSONObject(i).getInt("id"));
                p.setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                p.setTelefono(jsonArray.getJSONObject(i).getString("telefono"));
                p.setEmail(jsonArray.getJSONObject(i).getString("email"));
            }
            nombre.setText(p.getNombre());
            telefono.setText(p.getTelefono());
            email.setText(p.getEmail());
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error buscar el contacto", Toast.LENGTH_SHORT).show();
        }
    }

    public void listarDatos(String response)
    {
        arrayList = new ArrayList<Persona>();
        try
        {
            JSONArray jsonArray=new JSONArray(response);
            for (int i=0; i < jsonArray.length();i++)
            {
                Persona p=new Persona();
                p.setId(jsonArray.getJSONObject(i).getInt("id"));
                p.setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                p.setTelefono(jsonArray.getJSONObject(i).getString("telefono"));
                p.setEmail(jsonArray.getJSONObject(i).getString("email"));
                arrayList.add(p);
            }
            ArrayAdapter<Persona> a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
            lst.setAdapter(a);

            lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Persona p = arrayList.get(position);
                    String dir = Conexion.url+"getPerson.php?id="+p.getId();
                    client.post(dir, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200) {
                                getPerson(new String(responseBody));
                                nombre.setEnabled(true);
                                telefono.setEnabled(true);
                                email.setEnabled(true);
                                btnActualizar.setEnabled(true);
                                btnDelete.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(EditData.this, "Error al cargar el contacto", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void deletePerson(View view)
    {
        try
        {
            String dir= Conexion.url + "deletePerson.php?id="+p.getId();
            client.post(dir, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == 200)
                    {
                        Toast.makeText(EditData.this, "El contacto a sido eliminado", Toast.LENGTH_SHORT).show();
                        nombre.setText("");
                        telefono.setText("");
                        email.setText("");
                        listPerson();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(EditData.this, "Error al eliminar el contacto.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(EditData.this, "Error al eliminar el contacto.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePerson(View view)
    {
        if(nombre.getText().toString().isEmpty() || telefono.getText().toString().isEmpty())
        {
            Toast.makeText( EditData.this, "Campos en blanco", Toast.LENGTH_SHORT).show();
        }
        else {
            try
            {
                p.setNombre(nombre.getText().toString().replace(" ","%20"));
                p.setTelefono(telefono.getText().toString());
                p.setEmail(email.getText().toString());

                String dir= Conexion.url + "updatePerson.php?id="+p.getId()+"&nombre="+p.getNombre()+"&telefono="+p.getTelefono()+"&email="+p.getEmail();
                client.post(dir, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == 200)
                        {
                            Toast.makeText(EditData.this, "El contacto a sido actualizado", Toast.LENGTH_SHORT).show();
                            nombre.setText("");
                            telefono.setText("");
                            email.setText("");
                            listPerson();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(EditData.this, "Error al actualizar el contacto.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception ex)
            {
                Toast.makeText(EditData.this, "Error al actualizar el contacto.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
