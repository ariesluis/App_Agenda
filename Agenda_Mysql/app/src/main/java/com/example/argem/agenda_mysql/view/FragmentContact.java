package com.example.argem.agenda_mysql.view;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.argem.agenda_mysql.R;
import com.example.argem.agenda_mysql.controller.ContactDB;
import com.example.argem.agenda_mysql.controller.ControllerContact;
import com.example.argem.agenda_mysql.model.Contacto;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FragmentContact extends Fragment {

    ListView lst;
    ContactDB contactDB = new ContactDB();
    ArrayList<Contacto> arrayList = new ArrayList<>();
    JSONArray jsonContacto;
    JSONArray jsonContactoSelect;
    JSONArray jsonImagen;
    JSONArray jsonGrupo;
    int id_usuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_list_contact, container, false);
        lst = rootView.findViewById(R.id.lstContact);
        Bundle bundle = getArguments();
        id_usuario =bundle.getInt("id");
        listarContactos();
        return rootView;
    }

    public void listarContactos()
    {
        arrayList=new ArrayList<>();
        try {
            ControllerContact controllerContact = new ControllerContact(getContext());
            jsonContacto=new JSONArray(controllerContact.execute("c", ""+id_usuario ).get());
            for (int i=0; i < jsonContacto.length();i++)
            {
                //datos contacto
                Contacto c = new Contacto();
                c.setId_contacto(jsonContacto.getJSONObject(i).getInt("id_contacto"));
                c.setNombre(jsonContacto.getJSONObject(i).getString("nombre"));
                c.setTelefono(jsonContacto.getJSONObject(i).getString("telefono"));
                c.setDireccion(jsonContacto.getJSONObject(i).getString("direccion"));
                c.setAlias(jsonContacto.getJSONObject(i).getString("alias"));
                //datos imagen
                arrayList.add(c);
            }
            ArrayAdapter<Contacto> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);
            lst.setAdapter(arrayAdapter);

            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        ControllerContact controllerImage = new ControllerContact(getContext());
                        ControllerContact controllerGrupo = new ControllerContact(getContext());
                        jsonContactoSelect=new JSONArray();
                        jsonContactoSelect.put(jsonContacto.getJSONObject(position));
                        jsonImagen=new JSONArray(controllerImage.execute("i",""+arrayList.get(position).getId_contacto()).get());
                        jsonGrupo = new JSONArray(controllerGrupo.execute("g", ""+arrayList.get(position).getId_contacto()).get());
                        Bundle b=new Bundle();
                        b.putBoolean("tipo", false);
                        b.putInt("id_usuario",id_usuario);
                        b.putString("contacto",jsonContactoSelect.toString());
                        b.putString("imagen",jsonImagen.toString());
                        b.putString("grupo",jsonGrupo.toString());
                        Intent i = new Intent(getContext(), ViewContact.class);
                        i.putExtras(b);
                        startActivity(i);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
