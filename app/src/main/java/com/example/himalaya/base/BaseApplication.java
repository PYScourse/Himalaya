package com.example.himalaya.base;

import android.app.Application;
import android.os.Handler;

import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;



public class BaseApplication extends Application {

    //创建一个通用的handler
    private static Handler sHandler = null;


    @Override
    public void onCreate() {
        super.onCreate();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        //对外公开的
        if(DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this ,mAppSecret);
        } else {//对内公开的，有可能有特权
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this ,mAppSecret);
        }
        //初始化LogUtils
        //更改等级，使log不会被看到
       // LogUtil.init(this.getPackageName(), false);
        LogUtil.init(this.getPackageName(), false);

        //然后在create的时候我们创建
        sHandler = new Handler();
    }

    public static Handler getHandler(){
      return sHandler;
    }
}
