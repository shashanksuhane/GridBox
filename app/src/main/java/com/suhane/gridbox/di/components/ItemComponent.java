package com.suhane.gridbox.di.components;

import android.app.Activity;

import com.suhane.gridbox.di.modules.ActivityModule;
import com.suhane.gridbox.di.modules.ItemModule;
import com.suhane.gridbox.di.scopes.ActivityScope;
import com.suhane.gridbox.ui.items.ItemActivity;

import dagger.Component;

/**
 * Created by shashanksuhane on 03/04/18.
 */

@ActivityScope
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, ItemModule.class})
public interface ItemComponent {
    Activity activityContext();
    void inject(ItemActivity activity);
}
