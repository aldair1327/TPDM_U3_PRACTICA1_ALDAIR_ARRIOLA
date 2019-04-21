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

public class Main5Activity extends AppCompatActivity {
    public EditText ide,domi,tele,enca;
    public Button s, u;
    DatabaseReference DB = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        ide = findViewById(R.id.locid);
        domi = findViewById(R.id.dom);
        enca = findViewById(R.id.enc);
        tele = findViewById(R.id.tel);

        s = findViewById(R.id.locbuscar);
        u = findViewById(R.id.locactualizar);

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearch(ide.getText().toString());
            }
        });

        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final local emp = new local(ide.getText().toString(), domi.getText().toString(), tele.getText().toString(), enca.getText().toString());

                DB.child("Local").child(emp.idlocal).setValue(emp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Main5Activity.this, "El empleado fue actualizado exitosamnete", Toast.LENGTH_SHORT).show();
                                tele.setText("");
                                enca.setText("");
                                domi.setText("");
                                ide.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Main5Activity.this, "ERROR!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void toSearch(String i){
        FirebaseDatabase.getInstance().getReference().child("Local").child(i)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        local e = dataSnapshot.getValue(local.class);

                        if(e!=null) {
                            enca.setText(e.encargado);
                            tele.setText(e.telefono);
                            domi.setText(e.domicilio);
                        } else {
                            mensaje("ERROR","No se encontro ningun local con ese ID");
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