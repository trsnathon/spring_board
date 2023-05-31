package homepage.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;


public class Board {

    private int postNo;
    private String lock;
    private int wish;
    private String wishCheck;

    public String getWishCheck() {
        return wishCheck;
    }

    public void setWishCheck(String wishCheck) {
        this.wishCheck = wishCheck;
    }

    @NotBlank
    private String title;
    @NotBlank
    private String body;

    public int getWish() {
        return wish;
    }

    public void setWish(int wish) {
        this.wish = wish;
    }

    @NotBlank
    private String memberId;

    private Date postDate;

    @NotNull
    private int view;



    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }


    public int getPostNo() {
        return postNo;
    }

    public void setPostNo(int postNo) {
        this.postNo = postNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public Board() {
    }

    public Board(String title, String body, String memberId, Date postDate, int view) {
        this.title = title;
        this.body = body;
        this.memberId = memberId;
        this.postDate = postDate;
        this.view = view;
    }
}
