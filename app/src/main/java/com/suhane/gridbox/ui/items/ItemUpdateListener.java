package com.suhane.gridbox.ui.items;

import com.suhane.gridbox.repository.model.item.Item;

import java.util.List;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public interface ItemUpdateListener {
    void onItemUpdate(List<Item> itemList);
}
