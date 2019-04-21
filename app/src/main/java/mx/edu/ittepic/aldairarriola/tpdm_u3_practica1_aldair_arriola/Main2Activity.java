package mx.edu.ittepic.aldairarriola.tpdm_u3_practica1_aldair_arriola;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class Main2Activity extends AppCompatActivity {
    public EditText ID,nombre,rfc,edad;
    public Button insertar, consultar, eliminar, actualizar;
    public DatabaseReference DB;
    public List<empleado> datos;
    public ListView lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ID = findViewById(R.id.empleadoid);
        nombre = findViewById(R.id.empleadonombre);
        rfc = findViewById(R.id.empleadorfc);
        edad = findViewById(R.id.empleadoedad);

        insertar = findViewById(R.id.empinsertar);
        consultar = findViewById(R.id.empconsultar);
        eliminar = findViewById(R.id.empeliminar);
        actualizar = findViewById(R.id.empactualizar);

        lista = findViewById(R.id.listaemp);

        DB = FirebaseDatabase.getInstance().getReference();

        DB.child("Empleado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datos = new ArrayList<>();

                if (dataSnapshot.getChildrenCount() <= 0) {
                    Toast.makeText(Main2Activity.this, "ERROR!!! NO HAY DATOS!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (final DataSnapshot snap : dataSnapshot.getChildren()) {
                    DB.child("Empleado").child(snap.getKey()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    empleado e = dataSnapshot.getValue(empleado.class);

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
                final empleado emp = new empleado(ID.getText().toString(), nombre.getText().toString(), rfc.getText().toString(), edad.getText().toString());

                DB.child("Empleado").child(emp.idempleado).setValue(emp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Main2Activity.this, "El empleado fue registrado exitosamnete", Toast.LENGTH_SHORT).show();
                                nombre.setText("");
                                rfc.setText("");
                                edad.setText("");
                                ID.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main2Activity.this, "ERROR!!!", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(Main2Activity.this,Main4Activity.class));
    }

    private void toSearch() {
            final EditText id = new EditText(this);
            id.setHint("ID del empleado");
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("ATENCION").setMessage("ID A BUSCAR:").setView(id).setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mostrarempleado(id.getText().toString());
                }
            }).setNegativeButton("Cancelar", null).show();
        }
        private void mostrarempleado(String i){
            FirebaseDatabase.getInstance().getReference().child("Empleado").child(i)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            empleado e = dataSnapshot.getValue(empleado.class);

                            if(e!=null) {
                                nombre.setText(e.nombre);
                                edad.setText(e.edad);
                                rfc.setText(e.rfc);
                                ID.setText(e.idempleado);
                            } else {
                                mensaje("ERROR","No se encontro ningun empleado con ese ID");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }


        private void toDelete() {
            final EditText id = new EditText(this);
            id.setHint("ID del empleado a eliminar");
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("ATENCION").setMessage("ID A BUSCAR:").setView(id).setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    eliminar(id.getText().toString());
                }
            }).setNegativeButton("Cancelar", null).show();

        }
        private void eliminar(String id){
            DB.child("Empleado").child(id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Main2Activity.this, "SE ELIMINO CORRECTAMENTE AL EMPLEADO", Toast.LENGTH_SHORT).show();
                            ID.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Main2Activity.this, "ERROR AL ELIMINAR", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        ///otras funciones
        private void cargarSelect(){
            if (datos.size()==0) return;
            String nombres[] = new String[datos.size()];

            for(int i = 0; i<nombres.length; i++){
                empleado e = datos.get(i);
                nombres[i] = e.idempleado;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
            lista.setAdapter(adapter);
        }

        private void mensaje(String t, String m){
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);

            alerta.setTitle(t).setMessage(m).setPositiveButton("OK",null).show();
        }
    }




