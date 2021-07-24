package com.example.drogopolex.data.network.utils;


import com.example.drogopolex.data.network.response.BasicResponse;
import com.example.drogopolex.data.network.response.LoginResponse;
import com.example.drogopolex.data.network.response.ProfileResponse;
import com.example.drogopolex.data.network.response.ResponseType;
import com.example.drogopolex.data.network.response.TemplateResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Response;

public class ErrorUtils {

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
            if(baseResponse.errorBody() != null) {
                String responseBody = baseResponse.errorBody().string();
                JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
                String success = jsonObject.get("success").getAsString();
                String errorString = jsonObject.get("errorString").getAsString();
                response.setSuccess(success);
                response.setErrorString(errorString);
            }
            else {
                response.setSuccess("false");
                response.setErrorString("Nie udało się przetworzyć odpowiedzi.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
