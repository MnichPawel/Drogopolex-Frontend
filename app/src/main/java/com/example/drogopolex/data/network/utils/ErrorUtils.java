package com.example.drogopolex.data.network.utils;


import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.response.TemplateResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Response;

public class ErrorUtils {

    private ErrorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static TemplateResponse parseErrorResponse(Response<?> baseResponse, ResponseType responseType) {
        TemplateResponse response;

        switch (responseType) {
            case LOGIN_RESPONSE:
                response = new LoginResponse();
                break;
            case BASIC_RESPONSE:
                response = new BasicResponse();
                break;
            case PROFILE_RESPONSE:
                response = new ProfileResponse();
                break;
            default:
                return null;
        }

        try {
            if (baseResponse.errorBody() != null) {
                String responseBody = baseResponse.errorBody().string();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                String error = jsonObject.get("error").getAsString();
                response.setError(error);
            } else {
                response.setError("Nie udało się przetworzyć odpowiedzi.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
