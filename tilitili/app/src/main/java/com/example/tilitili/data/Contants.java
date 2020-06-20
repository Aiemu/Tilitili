package com.example.tilitili.data;

import com.example.tilitili.Config;

import java.util.HashMap;

public class Contants {
    public static final String USER_JSON = "user_json";
    public static final String SESSION_PREFERENCE = "header_json";
    public static final String TOKEN = "token";

    public static class API {
        public static final String BASE_URL = Config.host + ":" + Config.port;
        public static final String LOGIN_URL = BASE_URL + "/login";
        public static final String CHANGE_PASSWORD = BASE_URL + " /user/password/modify";
        public static final String FORGET_PASSWORD = BASE_URL + "/user/password/forget";
        public static final String GET_HOT = BASE_URL + "/submission/hot";
        public static final String GET_PLATE = BASE_URL + "/plate/getall";
        public static final String GET_PLATE_DETAILS = BASE_URL + "/plate/list/";
        public static final String GET_USER_SUBMISSION = BASE_URL + "/submission/user";
        public static final String REGISTER_URL = BASE_URL + "/signup";
        public static final String UPLOAD_URL = BASE_URL + "/upload";
        public static final String SUBMISSION_UPLOAD_URL = BASE_URL + "/submission/upload/";
        public static final String GET_USER_PROFILE_URL = BASE_URL + "/user/profile/info/";
        public static final String UPDATE_USER_PROFILE_URL = BASE_URL + "/user/profile/edit";
        public static final String CHECK_LOGIN_URL = BASE_URL + "/checklogin";
        public static final String GET_PRIVILEGE_PLATES = BASE_URL + "/plate/getprivilege";

        public static String getUrlWithID(String url, String ID) {
            return url + ID;
        }

        public static class UploadType {
            public static final int IMAGE = 0;
            public static final int HTML = 1;
            public static final int VIDEO = 2;
        }
    }
}
