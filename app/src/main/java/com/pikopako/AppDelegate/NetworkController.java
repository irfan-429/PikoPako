package com.pikopako.AppDelegate;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pikopako.AppUtill.UiHelper;

import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkController {

    public interface APIHandler {
        void Success(Object jsonObject);

        void Error(String error);

        void isConnected(boolean isConnected);
    }

    public void post(final Context context, final Call<JsonObject> call, final APIHandler apiHandler) {
        if (UiHelper.isOnline(context)) {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful())
                    {
                        try {
                            final String body = response.body().toString(); // Or use response.raw(), if want raw response string
                            JsonParser jsonParser = new JsonParser();
                            JsonObject res = jsonParser.parse(body).getAsJsonObject();
                            JSONObject jsonObject = new JSONObject(String.valueOf(res));
                            Log.w("Tag", "responce" + jsonObject.toString());
                            String error_code = jsonObject.has("error_code") ? jsonObject.getString("error_code") : "";
                            if (error_code.equalsIgnoreCase("delete_user") || error_code.equalsIgnoreCase("inactive_user")) {
                               /* ClinicallsApplication.getInstance().getSession().clearSession();
                                CommonSignInActivity.openLoginAcitivity(context,jsonObject.getString("message"));*/
                            } else {
                                apiHandler.Success(res);
                                apiHandler.isConnected(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        apiHandler.Success(null);
                        apiHandler.Error("Server error");
                        apiHandler.isConnected(true);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    apiHandler.Success(null);
                    apiHandler.Error(t.getMessage());
                    apiHandler.isConnected(true);
                }
            });
        } else {
            apiHandler.Success(null);
            apiHandler.Error("You are not connected to internet..");
            apiHandler.isConnected(false);
        }

    }

    public void get(Context context, final Call<JsonObject> call, final APIHandler apiHandler) {
        if (UiHelper.isOnline(context)) {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        apiHandler.Success(response.body());
                        //  apiHandler.Error(response.message());
                        apiHandler.isConnected(true);
                    } else {
                        apiHandler.Success(null);
                        apiHandler.Error("Server error");
                        apiHandler.isConnected(true);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    apiHandler.Success(null);
                    apiHandler.Error(t.getMessage());
                    apiHandler.isConnected(true);
                }
            });
        } else {
            apiHandler.Success(null);
            apiHandler.Error("You are not connected to internet..");
            apiHandler.isConnected(false);
        }

    }


}



