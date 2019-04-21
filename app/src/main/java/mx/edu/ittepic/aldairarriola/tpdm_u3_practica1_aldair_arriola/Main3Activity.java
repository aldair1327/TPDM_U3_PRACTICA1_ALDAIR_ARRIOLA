package mx.edu.ittepic.aldairarriola.tpdm_u3_practica1_aldair_arriola;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    public EditText ID,domicilio,telefono,encargado;
    public Button insertar, consultar, eliminar, actualizar;
    public DatabaseReference DB;
    public List<local> datos;
    public ListView lista;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ID = findViewById(R.id.idlocal);
        domicilio = findViewById(R.id.domiciliolocal);
        telefono = findViewById(R.id.domitel);
        encargado = findViewById(R.id.domienc);

        insertar = findViewById(R.id.ins);
        consultar = findViewById(R.id.con);
        eliminar = findViewById(R.id.eli);
        actualizar = findViewById(R.id.act);

        lista = findViewById(R.id.listalocal);

        DB = FirebaseDatabase.getInstance().getReference();
        DB.child("Local").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datos = new ArrayList<>();

                if (dataSnapshot.getChildrenCount() <= 0) {
                    Toast.makeText(Main3Activity.this, "ERROR!!! NO HAY DATOS!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (final DataSnapshot snap : dataSnapshot.getChildren()) {
                    DB.child("Local").child(snap.getKey()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    local e = dataSnapshot.getValue(local.class);

                                    if (e != null) {
                                        datos.add(e);
                                    }
                                    cargarSelect();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final local loc = new local(ID.getText().toString(), domicilio.getText().toString(), telefono.getText().toString(), encargado.getText().toString());

                DB.child("Local").child(loc.idlocal).setValue(loc)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Main3Activity.this, "El local fue registrado exitosamnete", Toast.LENGTH_SHORT).show();
                                domicilio.setText("");
                                encargado.setText("");
                                telefono.setText("");
                                ID.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main3Activity.this, "ERROR!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDelete();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearch();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUpdate();
            }
        });

    }

    private void toUpdate() {
        startActivity(new Intent(Main3Activity.this,Main5Activity.class));
    }

    private void toSearch() {
        final EditText id = new EditText(this);
        id.setHint("ID del local");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("ID A BUSCAR:").setView(id).setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrarempleado(id.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();
    }
    private void mostrarempleado(String i){
        FirebaseDatabase.getInstance().getReference().child("Local").child(i)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        local e = dataSnapshot.getValue(local.class);

                        if(e!=null) {
                            encargado.setText(e.encargado);
                            telefono.setText(e.telefono);
                            domicilio.setText(e.domicilio);
                            ID.setText(e.idlocal);
                        } else {
                            mensaje("ERROR","No se encontro ningun local con ese ID");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void toDelete() {
        final EditText id = new EditText(this);
        id.setHint("ID del local a eliminar");
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("ID A BUSCAR:").setView(id).setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminar(id.getText().toString());
            }
        }).setNegativeButton("Cancelar", null).show();

    }
    private void eliminar(String id){
        DB.child("Local").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this, "SE ELIMINO CORRECTAMENTE AL LOCAL", Toast.LENGTH_SHORT).show();
                        ID.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarSelect(){
        if (datos.size()==0) return;
        String nombres[] = new String[datos.size()];

        for(int i = 0; i<nombres.length; i++){
            local e = datos.get(i);
            nombres[i] = e.idlocal;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);
    }

    private void mensaje(String t, String m){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        alerta.setTitle(t).setMessage(m).setPositiveButton("OK",null).show();



    }
}
