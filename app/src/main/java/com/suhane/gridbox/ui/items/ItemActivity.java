package com.suhane.gridbox.ui.items;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.suhane.gridbox.GridBoxApp;
import com.suhane.gridbox.R;
import com.suhane.gridbox.di.components.DaggerItemComponent;
import com.suhane.gridbox.di.components.ItemComponent;
import com.suhane.gridbox.di.modules.ActivityModule;
import com.suhane.gridbox.repository.model.item.Item;
import com.suhane.gridbox.repository.model.item.Items;
import com.suhane.gridbox.ui.base.BaseActivity;
import com.suhane.gridbox.viewmodel.ItemViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public class ItemActivity extends BaseActivity implements ItemViewModel.View{

    private ItemComponent component;
    private ItemGridView itemGridView;

    @Inject
    ItemViewModel viewModel;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.scrollView)
    protected ScrollView scrollView;

    @BindView(R.id.gridLayout)
    protected GridLayout gridLayout;

    public ItemComponent component() {
        if (component == null) {
            component = DaggerItemComponent.builder()
                    .applicationComponent(((GridBoxApp) getApplication()).component())
                    .activityModule(new ActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);
        init();
    }

    @Override
    protected void onDestroy() {
        reset();
        super.onDestroy();
    }

    @Override
    protected void init() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        scrollView.setSmoothScrollingEnabled(true);

        itemGridView = new ItemGridView(this, gridLayout, scrollView);

        itemGridView.setItemUpdateListener(new ItemUpdateListener() {
            @Override
            public void onItemUpdate(List<Item> itemList) {
                Items items = new Items();
                items.setItems(itemList);
                viewModel.post(items);
            }
        });

        // initialize view model
        viewModel.init(this);
        viewModel.get();
        showProgress();
    }

    @Override
    protected void reset() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_item;
    }

    @Override
    protected BaseActivity getActivity() {
        return this;
    }

    @Override
    public void load(Items items) {
        hideProgress();
        itemGridView.addAllItems(items);
    }

    @Override
    public void success(String message) {
        hideProgress();
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error(String error) {
        hideProgress();
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
