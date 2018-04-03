package com.suhane.gridbox.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.suhane.gridbox.common.Constants;
import com.suhane.gridbox.repository.data.offline.OfflineItemAPI;
import com.suhane.gridbox.repository.data.server.ServerItemAPI;
import com.suhane.gridbox.repository.model.item.Items;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public class ItemViewModel implements IItemViewModel{

    private static final String TAG = ItemViewModel.class.getSimpleName();

    View view;

    @Inject
    ItemViewModel(){};

    @Override
    public void init(View view) {
        this.view = view;
    }

    @Override
    public void get() {
        // if result can't be shown, better not retrieve it
        if (view == null) {
            return;
        }

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String response) {
                Gson gson = new Gson();
                Items items = gson.fromJson(response, Items.class);
                if (items != null) view.load(items);
            }

            @Override
            public void onError(Throwable e) {
                view.error(e.toString());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };

        if (Constants.IS_LOCAL) {
            new OfflineItemAPI().get(observer);
        } else {
            //call server api
            new ServerItemAPI().get(observer);
        }

    }

    @Override
    public void post(final Items items) {
        // if result can't be shown, better not retrieve it
        if (view == null) {
            return;
        }

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String response) {
                if (!response.equalsIgnoreCase("Success")) {
                    // since failed to update items, get the items and reload
                    view.error("Failed to update data");
                    get();
                } else {
                    view.success(response);
                }
            }

            @Override
            public void onError(Throwable e) {
                view.error(e.toString());

                // since failed to update items, get the items and reload
                get();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };

        Gson gson = new Gson();
        String itemsJson = gson.toJson(items);

        if (Constants.IS_LOCAL) {
            new OfflineItemAPI().post(itemsJson, observer);
        } else {
            //call server api
            new ServerItemAPI().post(itemsJson, observer);
        }
    }
}
