package com.example.galicricket.RegisterUserDetails;

public class ReadWriteUserDetails {

    String Name,teamName, mobileNumber, email;

    public ReadWriteUserDetails(){

    }

    public ReadWriteUserDetails(String name, String team, String mobile, String email){
        Name = name;
        teamName = team;
        mobileNumber = mobile;
        this.email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
