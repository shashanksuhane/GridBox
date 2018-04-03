package com.suhane.gridbox.di.components;

import com.suhane.gridbox.di.modules.ServerModule;
import com.suhane.gridbox.repository.data.server.ServerItemAPI;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shashanksuhane on 03/04/18.
 */

@Singleton
@Component(modules = {ServerModule.class})
public interface ServerComponent {
    void inject(ServerItemAPI api);
}