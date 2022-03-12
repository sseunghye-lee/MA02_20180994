package ddwu.mobile.finalproject.ma02_20180994;

import android.text.Html;
import android.text.Spanned;

public class MovieDTO {
    private long _id;
    private String title;
    private String image;
    private String director;
    private String actor;
    private String userRating;
    private String review;
    private String theater;

    public String getTheater() {
        return theater;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public long get_id() {
        return _id;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        Spanned spanned = Html.fromHtml(title);     // 문자열에 HTML 태그가 포함되어 있을 경우 제거 후 일반 문자열로 변환
        return spanned.toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
