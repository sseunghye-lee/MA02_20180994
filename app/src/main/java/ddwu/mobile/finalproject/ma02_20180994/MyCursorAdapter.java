package ddwu.mobile.finalproject.ma02_20180994;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    int layout;
    ViewHolder viewHolder;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        viewHolder = new ViewHolder();
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        viewHolder = (ViewHolder) view.getTag();

        if(viewHolder.title == null) {
            viewHolder.title = view.findViewById(R.id.title);
            viewHolder.actor = view.findViewById(R.id.actor);
            viewHolder.director = view.findViewById(R.id.director);
            viewHolder.theater = view.findViewById(R.id.tvTheater);
            viewHolder.review = view.findViewById(R.id.tvReview);
            viewHolder.imageView = view.findViewById(R.id.image);

        }

        new ImageAsyncTask().execute(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_IMAGE)));

        viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_TITLE)));
        viewHolder.director.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_DIRECTOR)));
        viewHolder.actor.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_ACTOR)));
        viewHolder.theater.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_THEATER)));
        viewHolder.review.setText(cursor.getString(cursor.getColumnIndex(MovieDBHelper.COL_REVIEW)));

    }

    class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                viewHolder.imageView.setImageBitmap(bitmap);
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

     class ViewHolder {

        public ViewHolder() {
            title = null;
            actor = null;
            director = null;
            imageView = null;
            theater = null;
            review = null;
        }
        TextView title;
        TextView actor;
        TextView director;
        ImageView imageView;
        TextView theater;
        TextView review;

    }

}
