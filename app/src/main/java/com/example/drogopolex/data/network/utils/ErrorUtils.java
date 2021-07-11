package com.example.drogopolex.data.network.utils;


import com.example.drogopolex.data.network.response.LoginResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Response;

public class ErrorUtils {

    public static LoginResponse parseErrorResponse(Response<?> response) {
        LoginResponse loginResponse = new LoginResponse();
        try {
            if(response.errorBody() != null) {
                String responseBody = response.errorBody().string();
                JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
                String success = jsonObject.get("success").getAsString();
                String errorString = jsonObject.get("errorString").getAsString();
                loginResponse.setSuccess(success);
                loginResponse.setErrorString(errorString);
            }
            else {
                loginResponse.setSuccess("false");
                loginResponse.setErrorString("Nie udało się przetworzyć odpowiedzi.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginResponse;
    }
}
