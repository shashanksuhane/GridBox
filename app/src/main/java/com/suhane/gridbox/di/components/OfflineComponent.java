package com.suhane.gridbox.di.components;

import com.suhane.gridbox.di.modules.OfflineModule;
import com.suhane.gridbox.repository.data.offline.OfflineItemAPI;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shashanksuhane on 03/04/18.
 */

@Singleton
@Component(modules = {OfflineModule.class})
public interface OfflineComponent {
    void inject(OfflineItemAPI api);
}