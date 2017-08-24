package com.example.ezclassapp.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victorlee95 on 8/24/2017.
 */

public class User {
    String name;
    int age;
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public List<User> getListUsers() {
        ArrayList<User> list = new ArrayList<>();
        list.add(new User("victor",1));
        list.add(new User("abi",2));
        list.add(new User("gg",3));
        list.add(new User("hehe",4));
        list.add(new User("heyhey",5));
        return list;
    }
}
