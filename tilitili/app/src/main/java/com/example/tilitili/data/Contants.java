package com.example.tilitili.data;

import java.util.HashMap;
import java.util.Map;

public class Contants {
    public static final String USER_JSON = "user_json";
    public static final String TOKEN = "token";

    public static class API {
        public static final String BASE_URL = "http://192.168.0.103:8000";
        public static final String LOGIN_URL = BASE_URL + "/login";
        public static final String CHANGE_PASSWORD = BASE_URL + "/user/%s/password"; // %s username

        public String getUrl(String url, Object... args) {
            return String.format(url, args);
        }
    }

    public enum PRIVILEGE {
        UserPrivilegeNormal,
        UserPrivilegeOrganizer,
        UserPrivilegeSuperuser
    }

    public static HashMap<Integer, PRIVILEGE> privilegeMap = new HashMap<Integer, PRIVILEGE>(){
        {
            put(0, PRIVILEGE.UserPrivilegeNormal);
            put(1, PRIVILEGE.UserPrivilegeOrganizer);
            put(2, PRIVILEGE.UserPrivilegeSuperuser);
        }
    };
}
