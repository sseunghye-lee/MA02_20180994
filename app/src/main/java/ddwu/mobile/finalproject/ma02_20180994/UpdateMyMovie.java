package ddwu.mobile.finalproject.ma02_20180994;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UpdateMyMovie extends AppCompatActivity {

    ImageView image;
    TextView title;
    TextView director;
    TextView actor;
    TextView tvTheater;
    EditText etReview;

    MovieDBHelper helper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_my_movie);

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        director = findViewById(R.id.director);
        actor = findViewById(R.id.actor);
        tvTheater = findViewById(R.id.tvTheater);
        etReview = findViewById(R.id.etReview);


        helper = new MovieDBHelper(this);

        Intent intent = getIntent();
        String data = intent.getStringExtra("id");

        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + MovieDBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(helper.COL_ID));
            if(data.equals(String.valueOf(id))) {
                title.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_TITLE)));
                actor.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_ACTOR)));
                director.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_DIRECTOR)));
                tvTheater.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_THEATER)));
                etReview.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_REVIEW)));
                new ImageAsyncTask().execute(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_IMAGE)));
            }
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdate:
//                DB 데이터 업데이트 작업 수행
                SQLiteDatabase db = helper.getWritableDatabase();
                Intent intent = getIntent();
                String data = intent.getStringExtra("id");

                ContentValues row = new ContentValues();
                row.put(helper.COL_REVIEW, etReview.getText().toString());
                String whereClause = "_id=?";
                String[] whereArgs = new String[] {data};

                db.update(helper.TABLE_NAME, row, whereClause, whereArgs);

                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                image.setImageBitmap(bitmap);
            }
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap image = downloadImage(strings[0]);
            return image;
        }
    }

    protected Bitmap downloadImage(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        Bitmap result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToBitmap(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }


    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }


    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
    protected Bitmap readStreamToBitmap(InputStream stream) {
        return BitmapFactory.decodeStream(stream);
    }
}