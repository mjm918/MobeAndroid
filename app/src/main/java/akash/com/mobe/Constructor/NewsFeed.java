package akash.com.mobe.Constructor;

/**
 * Created by julfi on 28/07/2017.
 */

public class NewsFeed {

    private String fname,lname,email,emo,day,time;

    public NewsFeed(){

    }

    public NewsFeed(String fname, String lname, String email, String emo, String day, String time){
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.emo = emo;
        this.day = day;
        this.time = time;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmo() {
        return emo;
    }

    public void setEmo(String emo) {
        this.emo = emo;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
