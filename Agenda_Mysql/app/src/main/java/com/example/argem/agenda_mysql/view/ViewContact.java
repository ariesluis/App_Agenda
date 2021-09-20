package com.example.argem.agenda_mysql.view;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.argem.agenda_mysql.Conexion;
import com.example.argem.agenda_mysql.R;
import com.example.argem.agenda_mysql.controller.ContactDB;
import com.example.argem.agenda_mysql.controller.ControllerContact;
import com.example.argem.agenda_mysql.controller.ControllerImagen;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

public class ViewContact extends AppCompatActivity {

    ContactDB cdb=new ContactDB();
    AsyncHttpClient client;
    Bundle bundle;
    private static final int PICK_IMAGE=100;
    Uri imageData;

    EditText nombre, telefono, direccion, alias, comentario;
    CheckBox amigos, companero, conocido, familia, trabajo, otro;
    Button btnSave, btnCancel, btnDelete;
    ImageButton image;
    ImageView imageView;

    String encoded_string;
    byte[] array;
    int id_usuario=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        nombre=findViewById(R.id.txtNombreC);
        telefono=findViewById(R.id.txtTelefonoC);
        direccion=findViewById(R.id.txtDireccionC);
        alias=findViewById(R.id.txtAliasC);
        amigos=findViewById(R.id.chkAmigo);
        companero=findViewById(R.id.chkCompanero);
        conocido=findViewById(R.id.chkConocido);
        familia=findViewById(R.id.chkFamilia);
        trabajo=findViewById(R.id.chkTrabajo);
        otro=findViewById(R.id.chkOtro);
        image=findViewById(R.id.btnImagen);
        imageView=findViewById(R.id.imgFoto);
        comentario=findViewById(R.id.txtComentarioC);
        btnSave=findViewById(R.id.btnGuardarC);
        btnCancel=findViewById(R.id.btnCancelarC);
        btnDelete=findViewById(R.id.btnBorrarC);

        bundle=getIntent().getExtras();
        id_usuario=bundle.getInt("id_usuario");
        if (!bundle.getBoolean("tipo"))
        {
            btnSave.setText("Actualizar");
            btnDelete.setEnabled(true);
            cargarDatos();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void obtenerImagen()
    {
        try {

            Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap imagenFinal = Bitmap.createScaledBitmap(bm,120,150,false);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenFinal.compress(Bitmap.CompressFormat.JPEG, 1, stream);
            imagenFinal.recycle();
            array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            stream.close();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void onClickAddUpdate(View view)
    {
        obtenerImagen();
        if (bundle.getBoolean("tipo")) {
            guardarContacto();
            //Intent i = new Intent(getApplicationContext(), ViewPrincipal.class);
            //i.putExtras(bundle);
            //startActivity(i);
            finish();
        }
        else
        {
            actualizarContacto();
            //Intent i = new Intent(getApplicationContext(), ViewPrincipal.class);
            //i.putExtras(bundle);
            //startActivity(i);
            finish();
        }
    }

    private void guardarContacto()
    {
        try {
            JSONObject datos1 = new JSONObject();
            JSONObject datos2 = new JSONObject();
            //contacto
            datos1.put("nombre", nombre.getText().toString());
            datos1.put("telefono", telefono.getText().toString());
            datos1.put("direccion", direccion.getText().toString());
            datos1.put("alias", alias.getText().toString());
            datos1.put("id_usuario", bundle.getInt("id"));
            //imagen
            datos1.put("imagen", array);
            datos1.put("comentario", comentario.getText().toString());
            //contato-grupo
            if (amigos.isChecked()) datos2.put("Amigos", "Amigos");
            if (companero.isChecked()) datos2.put("Compañeros","Compañeros");
            if (conocido.isChecked()) datos2.put("Conocidos", "Conocidos");
            if (familia.isChecked()) datos2.put("Familia", "Familia");
            if (trabajo.isChecked()) datos2.put("Trabajo", "Trabajo");
            if (amigos.isChecked()) datos2.put("Otros", "Otros");

            client=new AsyncHttpClient();
            String url= Conexion.url+"contact/insertContact.php?jsonContacto="+ URLEncoder.encode(datos1.toString(),"UTF-8")+"&jsonGrupo="+ URLEncoder.encode(datos2.toString(),"UTF-8")+"&codigo_imagen="+ URLEncoder.encode(encoded_string, "UTF-8");
            client.post(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode==200) {
                        Toast.makeText(ViewContact.this, "Contacto creado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(ViewContact.this, "Error al crear el contacto", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void onClickImage(View view)
    {
        openGallery();
    }
    private void openGallery()
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode==PICK_IMAGE)
        {
            imageData=data.getData();
            imageView.setImageURI(imageData);
        }
    }

    private void actualizarContacto()
    {
        try
        {
            JSONObject datosContacto=new JSONObject();
            datosContacto.put("id_contacto", cdb.getContacto().getId_contacto());
            datosContacto.put("nombre", nombre.getText().toString());
            datosContacto.put("telefono", telefono.getText().toString());
            datosContacto.put("direccion", direccion.getText().toString());
            datosContacto.put("alias", alias.getText().toString());
            JSONObject datosImagen=new JSONObject();
            datosImagen.put("imagen", array);
            datosImagen.put("comentario", comentario.getText().toString());
            JSONObject datosGrupo=new JSONObject();
            if (amigos.isChecked()) datosGrupo.put("Amigos", "Amigos");
            if (companero.isChecked()) datosGrupo.put("Compañeros","Compañeros");
            if (conocido.isChecked()) datosGrupo.put("Conocidos", "Conocidos");
            if (familia.isChecked()) datosGrupo.put("Familia", "Familia");
            if (trabajo.isChecked()) datosGrupo.put("Trabajo", "Trabajo");
            if (otro.isChecked()) datosGrupo.put("Otros", "Otros");
            client=new AsyncHttpClient();
            String url= Conexion.url+"contact/updateContact.php?jsonContacto="+ URLEncoder.encode(datosContacto.toString())+"&jsonImagen="+URLEncoder.encode(datosImagen.toString())+"&jsonGrupo="+ URLEncoder.encode(datosGrupo.toString())+"&codigo_imagen="+URLEncoder.encode(encoded_string, "UTF-8");
            client.post(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode==200) {
                        Toast.makeText(ViewContact.this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(ViewContact.this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void cargarDatos()
    {
        try
        {
            JSONArray jcontacto= new JSONArray(bundle.getString("contacto"));
            JSONArray jimagen= new JSONArray(bundle.getString("imagen"));
            JSONArray jgrupos= new JSONArray(bundle.getString("grupo"));
            for (int i=0; i<jcontacto.length(); i++)
            {
                cdb.getContacto().setId_contacto(jcontacto.getJSONObject(i).getInt("id_contacto"));
                cdb.getContacto().setNombre(jcontacto.getJSONObject(i).getString("nombre"));
                cdb.getContacto().setTelefono(jcontacto.getJSONObject(i).getString("telefono"));
                cdb.getContacto().setDireccion(jcontacto.getJSONObject(i).getString("direccion"));
                cdb.getContacto().setAlias(jcontacto.getJSONObject(i).getString("alias"));
            }
            for (int i=0; i<jimagen.length(); i++)
            {
                cdb.getImagen().setId_imagen(jimagen.getJSONObject(i).getInt("id_imagen"));
                cdb.getImagen().setImagen(jimagen.getJSONObject(i).getString("imagen").getBytes());
                cdb.getImagen().setComentario(jimagen.getJSONObject(i).getString("comentario"));
            }
            ContactDB contactDB;
            for (int i=0; i<jgrupos.length(); i++)
            {
                contactDB = new ContactDB();
                contactDB.getGrupo().setId_grupo(jgrupos.getJSONObject(i).getInt("id_grupo"));
                cdb.getLstGrupo().add(contactDB.getGrupo());
            }
            //load data components
            nombre.setText(cdb.getContacto().getNombre());
            telefono.setText(cdb.getContacto().getTelefono());
            direccion.setText(cdb.getContacto().getDireccion());
            alias.setText(cdb.getContacto().getAlias());
            comentario.setText(cdb.getImagen().getComentario());
            ControllerImagen controllerImagen=new ControllerImagen(cdb.getContacto().getId_contacto());
            Bitmap bitmap =controllerImagen.execute().get();
            imageView.setImageBitmap(bitmap);
            for (int i=0; i<cdb.getLstGrupo().size(); i++)
            {
                if(cdb.getLstGrupo().get(i).getId_grupo()==1)
                    amigos.setChecked(true);
                if(cdb.getLstGrupo().get(i).getId_grupo()==2)
                    companero.setChecked(true);
                if(cdb.getLstGrupo().get(i).getId_grupo()==3)
                    conocido.setChecked(true);
                if(cdb.getLstGrupo().get(i).getId_grupo()==4)
                    familia.setChecked(true);
                if(cdb.getLstGrupo().get(i).getId_grupo()==5)
                    otro.setChecked(true);
                if(cdb.getLstGrupo().get(i).getId_grupo()==6)
                    trabajo.setChecked(true);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void borrarContacto(View view)
    {
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Eliminar Contacto")
                .setMessage("¿Desea eliminar el contacto?")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ControllerContact controllerContact = new ControllerContact(ViewContact.this);
                        try {
                            controllerContact.execute("d", ""+cdb.getContacto().getId_contacto()).get();
                            Toast.makeText(ViewContact.this, "Contacto Eliminado", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
