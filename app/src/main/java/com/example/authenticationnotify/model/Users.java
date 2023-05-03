package com.example.authenticationnotify.model;

public class Users {
    String id = "";

    String UserName;
    String UserImage;
    String Mobile;
    String Address;
    String Birthday;
    String Email;





    public Users(String id , String UserName, String UserImage, String Mobile
            , String Address, String Birthday  , String Email ) {
        this.id = id;
         this.UserName = UserName;
        this.UserImage = UserImage;
        this.Mobile = Mobile;
        this.Address = Address;
        this.Birthday = Birthday;
        this.Email = Email ;

     }



    public String getId() {
        return id;
    }

 
    public String getUserName() {
        return UserName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getBirthday() {
        return Birthday;
    }

    public String getAddress() {
        return Address;
    }



 }
