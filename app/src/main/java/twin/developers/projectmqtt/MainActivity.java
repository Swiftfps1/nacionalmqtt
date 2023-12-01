package twin.developers.projectmqtt;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Mqtt mqttManager;
    private double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databaseReference = FirebaseDatabase.getInstance().getReference("restaurant");




        ImageButton Taco = findViewById(R.id.taco);
        ImageButton Sopa = findViewById(R.id.sopa);
        ImageButton Hamburguesa = findViewById(R.id.hamburguesa);
        ImageButton Ensalada = findViewById(R.id.ensalada);
        final TextView tvTotal = findViewById(R.id.total);
        mqttManager = new Mqtt(getApplicationContext());
        mqttManager.connectToMqttBroker();
        Taco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarYRegistrarPrecioEnFirebase("taco", 3000, tvTotal);
                String taquito = "taco";
                mqttManager.publishMessage("restaurant/cheff", taquito);
            }
        });

        Sopa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarYRegistrarPrecioEnFirebase("sopa", 3000, tvTotal);
                String sopita = "sopa";
                mqttManager.publishMessage("restaurant/cheff", sopita);
            }
        });

        Hamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarYRegistrarPrecioEnFirebase("hamburguesa", 5000, tvTotal);

                String hamburguesita = "hamburguesa";
                mqttManager.publishMessage("restaurant/cheff", hamburguesita);
            }
        });

        Ensalada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarYRegistrarPrecioEnFirebase("ensalada", 4000, tvTotal);

                String fitnes = "ensalada";
                mqttManager.publishMessage("restaurant/cheff", fitnes);
            }
        });

        DatabaseReference totalReference = databaseReference.child("total");
        totalReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    total = dataSnapshot.getValue(Double.class);
                    tvTotal.setText("Total: " + total);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error al obtener el total: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sumarYRegistrarPrecioEnFirebase(final String producto, final double precio, final TextView tvTotal) {
        final DatabaseReference precioReference = databaseReference.child("productos").child(producto).child("precio");

        precioReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Double precioActual = task.getResult().getValue(Double.class);

                if (precioActual == null) {
                    precioActual = 0.0;
                }

                precioActual += precio;

                precioReference.setValue(precioActual);

                total += precio;

                DatabaseReference totalReference = databaseReference.child("total");
                totalReference.setValue(total);

                tvTotal.setText("Total: " + total);
            } else {
                Toast.makeText(getApplicationContext(), "Error al obtener el precio: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}