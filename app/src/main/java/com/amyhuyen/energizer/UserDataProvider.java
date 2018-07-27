package com.amyhuyen.energizer;

import com.amyhuyen.energizer.models.Nonprofit;
import com.amyhuyen.energizer.models.Volunteer;

public class UserDataProvider {
    private Volunteer mVolunteer;
    private Nonprofit mNonprofit;
    private String mUserType;
    private String mUserName;
    private String mUserEmail;

    private static UserDataProvider sInstance;

    private UserDataProvider() {

    }

    public static UserDataProvider getInstance() {
        if (sInstance == null) {
            sInstance = new UserDataProvider();
        }
        return sInstance;
    }

    public void setCurrentVolunteer(Volunteer volunteer) {
        mVolunteer = volunteer;
    }

    public Volunteer getCurrentVolunteer() {
        return mVolunteer;
    }

    public void setCurrentNPO(Nonprofit nonprofit) {
        mNonprofit = nonprofit;
    }

    public Nonprofit getCurrentNPO() {
        return mNonprofit;
    }

    public void setCurrentUserType(String UserType) {
        mUserType = UserType;
    }

    public String getCurrentUserType() {
        return mUserType;
    }

    public String getCurrentUserName(){
        String UserName = null;
        if (mUserType == "Volunteer"){
            UserName = mVolunteer.getName();
        } else {
            UserName = mNonprofit.getName();
        }
        return UserName;
    }

    public String getCurrentUserEmail(){
        String email = null;
        if (mUserType == "Volunteer"){
            email = mVolunteer.getEmail();
        } else {
            email = mNonprofit.getEmail();
        }
        return email;
    }
}
