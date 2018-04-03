package com.suhane.gridbox.repository.data.server;

import com.suhane.gridbox.GridBoxApp;
import com.suhane.gridbox.repository.data.ItemAPI;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import static com.suhane.gridbox.common.Constants.GET_QUERY;
import static com.suhane.gridbox.common.Constants.POST_QUERY;

/**
 * Created by shashanksuhane on 03/04/18.
 */
public class ServerItemAPI implements ItemAPI {
    @Inject
    Retrofit retrofit;

    public ServerItemAPI() {
        GridBoxApp.getApp().serverComponent().inject(this);
    }

    @Override
    public boolean get(Observer observer) {
        if (observer == null) return false;

        ItemGetService api = retrofit.create(ItemGetService.class);

        api.get().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        return true;
    }

    /*
     * Interface for get api
     */
    private interface ItemGetService {
        @GET(GET_QUERY)
        Observable<String> get();
    }


    @Override
    public boolean post(String itemsJson, Observer observer) {
        if (observer == null) return false;

        ItemPostService api = retrofit.create(ItemPostService.class);

        api.post(itemsJson).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        return true;
    }

    /*
     * Interface for get api
     */
    private interface ItemPostService {
        @POST(POST_QUERY)
        Observable<String> post(@Body String itemsJson);
    }
}

