package com.suyash.dell.medicare;

/**
 * Created by DELL on 9/24/2017.
 */

public class Person {

    private String username,email,pass,phoneno;



    public Person(String user)
    {

    }

    public Person() {

    }

    public String getName() {

        return username;
    }

    public void setName(String username) {

        this.username = username;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email=email;
    }
    public String getPass()
    {
        return pass;
    }
    public void setPass(String pass)
    {
        this.pass = pass;
    }
    public String getNumber() {

        return phoneno;
    }

    public void setNumber(String phoneno) {

        this.phoneno = phoneno;
    }
}
