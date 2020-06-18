package com.pikopako.AppDelegate;
import com.pikopako.AppUtill.Constant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    static Retrofit mRetrofit,mRetrofit1;





   public static Retrofit getClient() {
        if (mRetrofit == null) {

            OkHttpClient.Builder client = new OkHttpClient.Builder();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client.addInterceptor(loggingInterceptor);

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        }
        return mRetrofit;
    }
    public static Retrofit getGeocodeRestInterface() {
        mRetrofit1 = new Retrofit.Builder()
                .baseUrl(Constant.GEOCODER_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return mRetrofit1;
    }
}
