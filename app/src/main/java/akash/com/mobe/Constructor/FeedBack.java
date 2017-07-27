package akash.com.mobe.Constructor;

/**
 * Created by julfi on 27/07/2017.
 */

public class FeedBack {

    private String emo,fname,lname,date,email;

    public FeedBack(){

    }

    public FeedBack(String emo, String fname, String lname, String date, String email){
        this.emo = emo;
        this.fname = fname;
        this.lname = lname;
        this.date = date;
        this.email = email;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
