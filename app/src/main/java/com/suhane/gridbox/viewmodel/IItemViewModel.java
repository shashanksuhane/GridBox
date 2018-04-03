package com.suhane.gridbox.viewmodel;

import com.suhane.gridbox.repository.model.item.Items;
import com.suhane.gridbox.viewmodel.base.BaseViewModel;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public interface IItemViewModel extends BaseViewModel {

    void init(View view);
    void get();
    void post(Items items);

    interface View {
        void load(Items items);
        void success(String message);
        void error(String error);
    }
}