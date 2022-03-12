package ddwu.mobile.finalproject.ma02_20180994;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TheaterLocation extends AppCompatActivity {

    EditText etName;
    EditText etPhone;
    EditText etAddress;
    String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_location);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        if(name == null) {
            name = "정보 없음";
        }
        String phone = intent.getStringExtra("phone");
        if(phone == null) {
            phone = "정보 없음";
        }
        String address = intent.getStringExtra("address");
        if(address == null) {
            address = "정보 없음";
        }

        etName.setText(name);
        etPhone.setText(phone);
        etAddress.setText(address);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnMovie:
                Intent intent = new Intent(TheaterLocation.this, SearchMovie.class);
                intent.putExtra("name", name);
                startActivity(intent);
        }
    }
}