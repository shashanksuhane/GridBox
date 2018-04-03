package com.suhane.gridbox.di.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suhane.gridbox.common.Constants;
import com.suhane.gridbox.common.FileUtils;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by shashanksuhane on 03/04/18.
 */

@Module
public class OfflineModule {

    private final Application application;

    public OfflineModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    Interceptor provideOkHttpInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                HttpUrl url = chain.request().url();
                String path = url.encodedPath();
                switch(path) {
                    case Constants.GET_QUERY :
                        String response = FileUtils.readFromFile(application.getApplicationContext(), Constants.ITEMS_JSON_FILE_NAME);
                        return new Response.Builder()
                                .code(200)
                                .message(response)
                                .request(chain.request())
                                .protocol(Protocol.HTTP_2)
                                .body(ResponseBody.create(MediaType.parse("application/json"), response.getBytes()))
                                .addHeader("content-type", "application/json")
                                .build();

                    case Constants.POST_QUERY :

                        RequestBody body = chain.request().body();
                        Buffer buffer = new Buffer();
                        body.writeTo(buffer);
                        String data = buffer.readUtf8();


                        if (FileUtils.writeToFile(application.getApplicationContext(), Constants.ITEMS_JSON_FILE_NAME, data))
                            response = "Success";
                        else
                            response = "Fail";

                        return new Response.Builder()
                                .code(200)
                                .message(response)
                                .request(chain.request())
                                .protocol(Protocol.HTTP_2)
                                .body(ResponseBody.create(MediaType.parse("application/json"), response.getBytes()))
                                .addHeader("content-type", "application/json")
                                .build();

                }
                return null;
            }
        };
        return interceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }


    @Provides
    @Singleton
    Retrofit provideRetrofitClient(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

}
