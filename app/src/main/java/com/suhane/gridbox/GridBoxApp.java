package com.suhane.gridbox;

import android.app.Application;

import com.suhane.gridbox.common.Constants;
import com.suhane.gridbox.common.FileUtils;
import com.suhane.gridbox.di.components.ApplicationComponent;
import com.suhane.gridbox.di.components.DaggerApplicationComponent;
import com.suhane.gridbox.di.components.DaggerOfflineComponent;
import com.suhane.gridbox.di.components.DaggerServerComponent;
import com.suhane.gridbox.di.components.OfflineComponent;
import com.suhane.gridbox.di.components.ServerComponent;
import com.suhane.gridbox.di.modules.ApplicationModule;
import com.suhane.gridbox.di.modules.OfflineModule;
import com.suhane.gridbox.di.modules.ServerModule;

/**
 * Created by shashanksuhane on 03/04/18.
 */
public class GridBoxApp extends Application {

    private static GridBoxApp app;
    private ApplicationComponent applicationComponent;
    private OfflineComponent offlineComponent;
    private ServerComponent serverComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        initialize();
    }

    private void initialize() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        offlineComponent = DaggerOfflineComponent.builder()
                .offlineModule(new OfflineModule(this))
                .build();

        serverComponent = DaggerServerComponent.builder()
                .serverModule(new ServerModule(this))
                .build();

        if (!FileUtils.isFileExist(this, Constants.ITEMS_JSON_FILE_NAME)) {
            FileUtils.writeToFile(this, Constants.ITEMS_JSON_FILE_NAME, getString(R.string.json_default_response));
        }
    }

    public static GridBoxApp getApp() {
        return app;
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

    public OfflineComponent offlineComponent() {
        return offlineComponent;
    }

    public ServerComponent serverComponent() {
        return serverComponent;
    }
}
