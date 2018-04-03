package com.suhane.gridbox.di.components;

import android.app.Application;
import android.content.Context;

import com.suhane.gridbox.GridBoxApp;
import com.suhane.gridbox.di.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shashanksuhane on 03/04/18.
 */

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    /**
     * Injections for the dependencies
     */
    void inject(GridBoxApp app);

    void inject(Context context);

    /**
     * Used in child components
     */
    Application application();
}
