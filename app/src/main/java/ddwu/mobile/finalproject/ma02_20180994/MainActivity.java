package ddwu.mobile.finalproject.ma02_20180994;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import noman.googleplaces.PlaceType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTheater:
                Intent intent = new Intent(MainActivity.this, SearchTheater.class);
                startActivity(intent);
                break;
            case R.id.btnSearch:
                Intent intent2 = new Intent(MainActivity.this, SearchMovie.class);
                startActivity(intent2);
                break;
            case R.id.btnMovie:
                Intent intent3 = new Intent(MainActivity.this, MyMovie.class);
                startActivity(intent3);
                break;
        }
    }
}