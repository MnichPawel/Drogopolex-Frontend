package com.example.drogopolex.constants;

public class AppConstant {

//       public static final String BASE_URL = "http://192.168.0.106:5000/";
    public static final String BASE_URL = "http://10.0.2.2:5000/";
    public static final int PERMISSIONS_REQUEST_LOCATION = 99;

    public static final String DROGOPOLEX_SETTINGS_SHARED_PREFERENCES = "DrogopolexSettings";
    public static final String USER_ID_SHARED_PREFERENCES = "user_id";
    public static final String TOKEN_SHARED_PREFERENCES = "token";

    private AppConstant() {
        throw new IllegalStateException("Constants class");
    }
}
