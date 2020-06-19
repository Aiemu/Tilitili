package com.example.tilitili.data;

import com.example.tilitili.Config;

import java.util.HashMap;

public class Contants {
    public static final String USER_JSON = "user_json";
    public static final String SESSION_PREFERENCE = "header_json";
    public static final String TOKEN = "token";

    public static class API {
        // url最后有/表示需要进行getUrlWithID操作，如果后面不加id则不需要/
        public static final String BASE_URL = Config.host + ":" + Config.port;
        public static final String LOGIN_URL = BASE_URL + "/login";
        public static final String CHANGE_PASSWORD = BASE_URL + "/user/%s/password"; // %s username
        public static final String GET_HOT = BASE_URL + "/submission/hot";
        public static final String REGISTER_URL = BASE_URL + "/signup";
        public static final String UPLOAD_URL = BASE_URL + "/upload";
        public static final String SUBMISSION_UPLOAD_URL = BASE_URL + "/submission/upload/";
        public static final String GET_USER_PROFILE_URL = BASE_URL + "/user/profile/info/";
        public static final String UPDATE_USER_PROFILE_URL = BASE_URL + " /user/profile/edit";

        public static String getUrlWithID(String url, String ID) {
            return url + ID;
        }

        public static class UploadType {
            public static final int IMAGE = 0;
            public static final int HTML = 1;
            public static final int VIDEO = 2;
        }
    }

    public enum PRIVILEGE {
        UserPrivilegeNormal,
        UserPrivilegeOrganizer,
        UserPrivilegeSuperuser
    }

    public static HashMap<Integer, PRIVILEGE> privilegeMap = new HashMap<Integer, PRIVILEGE>() {
        {
            put(0, PRIVILEGE.UserPrivilegeNormal);
            put(1, PRIVILEGE.UserPrivilegeOrganizer);
            put(2, PRIVILEGE.UserPrivilegeSuperuser);
        }
    };
}
