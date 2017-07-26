package akash.com.mobe.Constructor;

/**
 * Created by julfi on 26/07/2017.
 */

public class UserInfo {

    private String name,dob,approve,department,email,emo;

    public UserInfo(){

    }

    public UserInfo(String name,String dob,String approve,String department,String email,String emo){

        super();

        this.name = name;
        this.dob = dob;
        this.approve = approve;
        this.department = department;
        this.email = email;
        this.emo = emo;

    }

    public String getEmo() {
        return emo;
    }

    public void setEmo(String emo) {
        this.emo = emo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
