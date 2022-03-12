package ddwu.mobile.finalproject.ma02_20180994;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SearchMovie extends AppCompatActivity {

    EditText etGenre;
    ListView lvList;
    String apiAddress;

    String query;

    ArrayList<MovieDTO> resultList;
    MovieXmlParser parser;

    MovieAdapter movieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movie);

        etGenre = findViewById(R.id.etGenre);
        lvList = findViewById(R.id.lvList);

        resultList = new ArrayList();

        movieAdapter = new MovieAdapter(this, R.layout.activity_movie_list, resultList);
        lvList.setAdapter(movieAdapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDTO movie = resultList.get(position);
                Intent getIntent = getIntent();
                String tname = getIntent.getStringExtra("name");
                Intent intent = new Intent(SearchMovie.this, MyReview.class);
                intent.putExtra("id", String.valueOf(id));
                intent.putExtra("_id", resultList.get(position).get_id());
                intent.putExtra("title", resultList.get(position).getTitle());
                intent.putExtra("actor", resultList.get(position).getActor());
                intent.putExtra("director", resultList.get(position).getDirector());
                intent.putExtra("imageString", resultList.get(position).getImage());
                intent.putExtra("ratingBar", resultList.get(position).getUserRating());
                intent.putExtra("name", tname);
                startActivity(intent);
            }
        });

        apiAddress = getResources().getString(R.string.api_url);
        parser = new MovieXmlParser();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
                if (!isOnline()) {
                    Toast.makeText(SearchMovie.this, "네트워크를 사용 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                query = etGenre.getText().toString();
                new NaverAsyncTask().execute(apiAddress, query);
                break;
        }
    }

    class MovieAdapter extends BaseAdapter {

        int count;
        private Context context;
        private int layout;
        private ArrayList<MovieDTO> movieList;
        private LayoutInflater inflater;
        ViewHolder viewHolder;

        public MovieAdapter(Context context, int layout, ArrayList<MovieDTO> movieList) {
            this.context = context;
            this.layout = layout;
            this.movieList = movieList;
            count=0;

            inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return movieList.size();
        }

        @Override
        public Object getItem(int position) {
            return movieList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return movieList.get(position).get_id();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;

            if (convertView == null) {
                convertView = inflater.inflate(layout, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.director = (TextView) convertView.findViewById(R.id.director);
                viewHolder.actor = (TextView) convertView.findViewById(R.id.actor);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.image);
                viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(movieList.get(pos).getTitle());
            viewHolder.director.setText(movieList.get(pos).getDirector());
            viewHolder.actor.setText(movieList.get(pos).getActor());
            viewHolder.ratingBar.setRating(Float.parseFloat(movieList.get(pos).getUserRating())/2);

            new ImageAsyncTask().execute(movieList.get(pos).getImage());

            return convertView;
        }

        class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null) {
                    viewHolder.img.setImageBitmap(bitmap);
                } else {
                    viewHolder.img.setImageResource(R.mipmap.ddwuicon);
                }
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap image = downloadImage(strings[0]);
                return image;
            }
        }

        class ViewHolder {
            TextView title;
            TextView director;
            TextView actor;
            ImageView img;
            RatingBar ratingBar;
        }
    }

    class NaverAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchMovie.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String query = strings[1];

            String apiURL = null;
            try {
                apiURL = address + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String result = downloadNaverContents(apiURL);
            return result;
        }


        @Override
        protected void onPostExecute(String result) {

            progressDlg.dismiss();

            ArrayList<MovieDTO> parserdList = parser.parse(result);     // 오픈API 결과의 파싱 수행

            if (parserdList.size() == 0) {
                Toast.makeText(SearchMovie.this, "No data!", Toast.LENGTH_SHORT).show();
            } else {
                resultList.clear();
                resultList.addAll(parserdList);
//                adapter.notifyDataSetChanged();
                movieAdapter.notifyDataSetChanged();

            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadNaverContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
        String clientId = getResources().getString(R.string.client_id);
        String clientSecret = getResources().getString(R.string.client_secret);

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            /* 네이버 사용 시 설정 필요 */
            conn.setRequestProperty("X-Naver-Client-Id", clientId);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
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