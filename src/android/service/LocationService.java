package com.aruistar.cordova.baidumap.service;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.content.Context;
import android.webkit.WebView;

import org.apache.cordova.CordovaWebView;

/**
 * 定位服务LocationClient 相关
 *
 * @author baidu
 */
public class LocationService {
  private static LocationClient client = null;
  private static LocationClientOption mOption;
  private static LocationClientOption  DIYoption;
  private Object objLock;

  /***
   * 初始化 LocationClient
   *
   * @param locationContext
   */
  public LocationService(CordovaWebView webView, BDAbstractLocationListener myListener){

    objLock = new Object();
    synchronized (objLock) {
      if (client == null) {
        client =  new LocationClient(webView.getContext());
        client.registerLocationListener(myListener);
        client.setLocOption(getDefaultLocationClientOption());
      }
    }
  }

  /***
   * 注册定位监听
   *
   * @param listener
   * @return
   */

  public boolean registerListener(BDAbstractLocationListener listener) {
    boolean isSuccess = false;
    if (listener != null) {
      client.registerLocationListener(listener);
      isSuccess = true;
    }
    return isSuccess;
  }

  public void unregisterListener(BDAbstractLocationListener listener) {
    if (listener != null) {
      client.unRegisterLocationListener(listener);
    }
  }

  /**
   * @return 获取sdk版本
   */
  public String getSDKVersion() {
    if (client != null) {
      String version = client.getVersion();
      return version;
    }
    return null;
  }

  /***
   * 设置定位参数
   *
   * @param option
   * @return isSuccessSetOption
   */
  public static boolean setLocationOption(LocationClientOption option) {
    boolean isSuccess = false;
    if (option != null) {
      if (client.isStarted()) {
        client.stop();
      }
      DIYoption = option;
      client.setLocOption(option);
    }
    return isSuccess;
  }



  /**
   * 停止H5辅助定位
   */
  public void disableAssistantLocation() {
    if (client != null) {
      client.disableAssistantLocation();
    }
  }


  public LocationClientOption getDefaultLocationClientOption() {
    if (mOption == null) {
      mOption = new LocationClientOption();

      //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
      mOption.setLocationMode(LocationMode.Hight_Accuracy);
      //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
      mOption.setCoorType("bd09ll");
      //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
      mOption.setScanSpan(0);
      //可选，设置是否需要地址信息，默认不需要
      mOption.setIsNeedAddress(false);
      //可选，设置是否需要地址描述
      mOption.setIsNeedLocationDescribe(false);
      //可选，设置是否需要设备方向结果
      mOption.setNeedDeviceDirect(true);
      //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
      mOption.setLocationNotify(false);
      //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
      mOption.setIgnoreKillProcess(true);
      //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
      mOption.setIsNeedLocationDescribe(true);
      //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
      mOption.setIsNeedLocationPoiList(false);
      //可选，默认false，设置是否收集CRASH信息，默认收集
      mOption.SetIgnoreCacheException(false);
      //可选，默认false，设置是否开启Gps定位
      mOption.setOpenGps(true);
      //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
      mOption.setIsNeedAltitude(false);

    }
    return mOption;
  }



  /**
   * @return DIYOption 自定义Option设置
   */
  public LocationClientOption getOption() {
    if (DIYoption == null) {
      DIYoption = new LocationClientOption();
    }
    return DIYoption;
  }

  public void start() {
    synchronized (objLock) {
      if (client != null && !client.isStarted()) {
        client.start();
      }
    }
  }

  public void requestLocation() {
    if (client != null) {
      client.requestLocation();
    }
  }

  public void stop() {
    synchronized (objLock) {
      if (client != null && client.isStarted()) {
        client.stop();
      }
    }
  }

  public boolean isStart() {
    return client.isStarted();
  }

  public boolean requestHotSpotState() {
    return client.requestHotSpotState();
  }
}
