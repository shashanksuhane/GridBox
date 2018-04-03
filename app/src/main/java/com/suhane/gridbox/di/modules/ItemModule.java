package com.suhane.gridbox.di.modules;

import com.suhane.gridbox.di.scopes.ActivityScope;
import com.suhane.gridbox.viewmodel.IItemViewModel;
import com.suhane.gridbox.viewmodel.ItemViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shashanksuhane on 03/04/18.
 */

@Module
public class ItemModule {
    @Provides
    @ActivityScope
    IItemViewModel provideItemViewModel(ItemViewModel viewModel) {
        return viewModel;
    }

}
