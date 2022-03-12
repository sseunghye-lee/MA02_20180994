package ddwu.mobile.finalproject.ma02_20180994;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyMovie extends AppCompatActivity {

    MovieDBHelper helper;
    Cursor cursor;
    ListView lvMovies = null;
    MyCursorAdapter adapter;
    ArrayList<MovieDTO> resultList;
    final static int REQ_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_movie);

        lvMovies = (ListView)findViewById(R.id.movieList);

        helper = new MovieDBHelper(this);
        resultList = new ArrayList();

        adapter = new MyCursorAdapter(this, R.layout.my_review, null);
        lvMovies.setAdapter(adapter);

        lvMovies.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent2 = new Intent(MyMovie.this, UpdateMyMovie.class);

            intent2.putExtra("id", String.valueOf(cursor.getLong(cursor.getColumnIndex(MovieDBHelper.COL_ID))));
            startActivity(intent2);

        });

        lvMovies.setOnItemLongClickListener((parent, view, position, id) -> {
            cursor.moveToPosition(position);
            Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
            Sharing_intent.setType("text/plain");
            String Text_Message = "영화 제목: " + cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_TITLE))
                    + "\n 영화 감독: " + cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_DIRECTOR))
                    + "\n 배우: " + cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_ACTOR))
                    + "\n 리뷰: " + cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_REVIEW))
                    + "\n 영화관: " + cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_THEATER));

            Sharing_intent.putExtra(Intent.EXTRA_TEXT, Text_Message);

            Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
            startActivity(Sharing);

            return true;
        });

    }

    protected void onResume() {
        super.onResume();
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + MovieDBHelper.TABLE_NAME, null);

            adapter.changeCursor(cursor);
            helper.close();
    }

    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnClose:
                Intent intent = new Intent(MyMovie.this, MainActivity.class);
                startActivity(intent);
                break;
        }

    }



}