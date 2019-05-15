package com.lexxdigital.easyfoodvone.utility;

import android.app.Application;

import com.lexxdigital.easyfoodvone.utility.printerutil.AidlUtil;

public class ApplicationContext extends Application
{
    ApplicationContext applicationContext;

    public ApplicationContext getInstance(){
        return applicationContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        AidlUtil.getInstance().connectPrinterService(this);
    }
}
