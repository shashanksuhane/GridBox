package com.suhane.gridbox.repository.data;

import io.reactivex.Observer;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public interface ItemAPI {
    /*
     * To get the response for the get api
     * and return the response as result observer
     */
    boolean get(Observer observer);

    /*
     * To post the updated item json using the post api
     * and return the status as result observer
     */
    boolean post(String itemsJson, Observer observer);
}
