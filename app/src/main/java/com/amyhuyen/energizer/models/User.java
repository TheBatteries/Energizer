package com.amyhuyen.energizer.models;


//@org.parceler.Parcel
public class User {

    //user fields
    String age;
    String email;
    String name;
    String phone;
    String userID;
    String userType;

    public User () {

    }

    public User (String age, String email, String name, String phone, String userID, String userType) {
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.userID = userID;
        this.userType = userType;
    }
}

////////////Body of User when DB structure was User --> UserType -->User ID --> User fields

//var
//    private FirebaseAuth firebaseAuth;
//    private FirebaseUser currentFirebaseUser;
//    private DatabaseReference mDBUserRef;
//    private String userType;
//
//    public User () {}
//
//    public User(FirebaseAuth firebaseAuth, DatabaseReference mDBUserRef, String userType) {
//        this.firebaseAuth = firebaseAuth;
//        this.mDBUserRef = mDBUserRef;
//        this.userType = userType;
//        currentFirebaseUser = firebaseAuth.getCurrentUser();
//    }
//
//    ////May not need this if I can getUserType using getValue()
//    public String getUserType() {
//        return userType;
//    }
//
//    public String getName() {
//        return currentFirebaseUser.getDisplayName();
//    }
//
//    public String getEmail() {
//        return currentFirebaseUser.getEmail();
//    }
//
//    //////TODO - maybe test this
////    public String getUserType () {
////        String userType = "";
////        mDBUserRef.child("NPO").addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                User user = dataSnapshot.getValue(User.class);
////                userType = user.getUserType();
////            }
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Log.d("LoginActivity", "Failed to get user type.");
////            }
////        });
////        return userType;
////    }