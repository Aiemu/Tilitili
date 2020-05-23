package com.example.tilitili.data;

import static com.example.tilitili.data.Contants.privilegeMap;

public class User {
    private int userId;
    private String username;
    private String nickname;
    private int privilege;

    public User(int id, String username, int privilege, String nickname){
        this.userId = id;
        this.username = username;
        this.privilege = privilege;
        this.nickname = nickname;
    }
}
