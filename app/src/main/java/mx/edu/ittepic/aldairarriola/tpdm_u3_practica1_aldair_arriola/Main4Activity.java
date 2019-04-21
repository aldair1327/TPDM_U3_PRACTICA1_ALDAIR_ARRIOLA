package mx.edu.ittepic.aldairarriola.tpdm_u3_practica1_aldair_arriola;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main4Activity extends AppCompatActivity {
    public EditText ide,name,rfc,age;
    public Button s, u;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        ide = findViewById(R.id.actid);
        name = findViewById(R.id.actname);
        rfc = findViewById(R.id.actrfc);
        age = findViewById(R.id.actedad);

        s = findViewById(R.id.actbuscar);
        u = findViewById(R.id.actactualizar);

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearch(ide.getText().toString());
            }
        });

        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final empleado emp = new empleado(ide.getText().toString(), name.getText().toString(), rfc.getText().toString(), age.getText().toString());

                DB.child("Empleado").child(emp.idempleado).setValue(emp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Main4Activity.this, "El empleado fue actualizado exitosamnete", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                rfc.setText("");
                                age.setText("");
                                ide.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main4Activity.this, "ERROR!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void toSearch(String i){
        FirebaseDatabase.getInstance().getReference().child("Empleado").child(i)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        empleado e = dataSnapshot.getValue(empleado.class);

                        if(e!=null) {
                            name.setText(e.nombre);
                            age.setText(e.edad);
                            rfc.setText(e.rfc);
                        } else {
                            mensaje("ERROR","No se encontro ningun empleado con ese ID");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void mensaje(String t, String m){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        alerta.setTitle(t).setMessage(m).setPositiveButton("OK",null).show();
    }
}
