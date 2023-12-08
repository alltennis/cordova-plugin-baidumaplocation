package com.jsha.cordova.baidumap;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;

import com.jsha.cordova.baidumap.service.LocationService;
import com.jsha.cordova.baidumap.service.Utils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 百度定位cordova插件android端
 *
 * @author aruis
 * @author KevinWang15
 */
public class BaiduMapLocation extends CordovaPlugin {

  private LocationService locationService;
  /**
   * LOG TAG
   */
  private static final String LOG_TAG = BaiduMapLocation.class.getSimpleName();

  /**
   * JS回调接口对象
   */
  public static CallbackContext cbCtx = null;


  /*****
   *
   * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
   *
   */
  final private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {

    /**
     * 定位请求回调函数
     * @param location 定位结果
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
      //获取纬度信息
      double latitude = location.getLatitude();
      //获取经度信息
      double longitude = location.getLongitude();
      //获取定位精度，默认值为0.0f
      float radius = location.getRadius();
      //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
      String coorType = location.getCoorType();
      //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
      int errorCode = location.getLocType();

      // TODO Auto-generated method stub
      if (null != location && location.getLocType() != BDLocation.TypeServerError) {
        int tag = 1;
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        sb.append(location.getTime());
        sb.append("\nlocType : ");// 定位类型
        sb.append(location.getLocType());
        sb.append("\nlocType description : ");// *****对应的定位类型说明*****
        sb.append(location.getLocTypeDescription());
        sb.append("\nlatitude : ");// 纬度
        sb.append(location.getLatitude());
        sb.append("\nlongtitude : ");// 经度
        sb.append(location.getLongitude());
        sb.append("\nradius : ");// 半径
        sb.append(location.getRadius());
        sb.append("\nCountryCode : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\nProvince : ");// 获取省份
        sb.append(location.getProvince());
        sb.append("\nCountry : ");// 国家名称
        sb.append(location.getCountry());
        sb.append("\ncitycode : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\ncity : ");// 城市
        sb.append(location.getCity());
        sb.append("\nDistrict : ");// 区
        sb.append(location.getDistrict());
        sb.append("\nTown : ");// 获取镇信息
        sb.append(location.getTown());
        sb.append("\nStreet : ");// 街道
        sb.append(location.getStreet());
        sb.append("\naddr : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\nStreetNumber : ");// 获取街道号码
        sb.append(location.getStreetNumber());
        sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\nDirection(not all devices have value): ");
        sb.append(location.getDirection());// 方向
        sb.append("\nlocationdescribe: ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        sb.append("\nPoi: ");// POI信息
        if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
          for (int i = 0; i < location.getPoiList().size(); i++) {
            Poi poi = (Poi) location.getPoiList().get(i);
            sb.append("poiName:");
            sb.append(poi.getName() + ", "); 
            sb.append("poiTag:");
            sb.append(poi.getTags() + "\n");
          }
        }
        if (location.getPoiRegion() != null) {
          sb.append("PoiRegion: ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI信息时才会返回，在网络不通或无法获取时有可能返回null
          PoiRegion poiRegion = location.getPoiRegion();
          sb.append("DerectionDesc:"); // 获取POIREGION的位置关系，ex:"内"
          sb.append(poiRegion.getDerectionDesc() + "; ");
          sb.append("Name:"); // 获取POIREGION的名字字符串
          sb.append(poiRegion.getName() + "; ");
          sb.append("Tags:"); // 获取POIREGION的类型
          sb.append(poiRegion.getTags() + "; ");
        }
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
          sb.append("\nspeed : ");
          sb.append(location.getSpeed());// 速度 单位：km/h
          sb.append("\nsatellite : ");
          sb.append(location.getSatelliteNumber());// 卫星数目
          sb.append("\nheight : ");
          sb.append(location.getAltitude());// 海拔高度 单位：米
          sb.append("\ngps status : ");
          sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
          sb.append("\ndescribe : ");
          sb.append("gps定位成功");
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
          // 运营商信息
          if (location.hasAltitude()) {// *****如果有海拔高度*****
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
          }
          sb.append("\noperationers : ");// 运营商信息
          sb.append(location.getOperators());
          sb.append("\ndescribe : ");
          sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
          sb.append("\ndescribe : ");
          sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
          sb.append("\ndescribe : ");
          sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
          sb.append("\ndescribe : ");
          sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
          sb.append("\ndescribe : ");
          sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        logMsg(sb.toString(), tag);
      }
      try {
        JSONObject json = new JSONObject();

        json.put("time", location.getTime());
        json.put("locType", location.getLocType());
        json.put("locTypeDescription", location.getLocTypeDescription());
        json.put("latitude", location.getLatitude());
        json.put("longitude", location.getLongitude());
        json.put("radius", location.getRadius());

        json.put("countryCode", location.getCountryCode());
        json.put("country", location.getCountry());
        json.put("citycode", location.getCityCode());
        json.put("city", location.getCity());
        json.put("district", location.getDistrict());
        json.put("street", location.getStreet());
        json.put("addr", location.getAddrStr());
        json.put("province", location.getProvince());

        json.put("userIndoorState", location.getUserIndoorState());
        json.put("direction", location.getDirection());
        json.put("locationDescribe", location.getLocationDescribe());

        PluginResult pluginResult;
        if (location.getLocType() == BDLocation.TypeServerError
          || location.getLocType() == BDLocation.TypeNetWorkException
          || location.getLocType() == BDLocation.TypeCriteriaException) {

          json.put("describe", "定位失败");
          pluginResult = new PluginResult(PluginResult.Status.ERROR, json);
        } else {
          pluginResult = new PluginResult(PluginResult.Status.OK, json);
        }


        cbCtx.sendPluginResult(pluginResult);
      } catch (JSONException e) {
        String errMsg = e.getMessage();
        LOG.e(LOG_TAG, errMsg, e);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
        cbCtx.sendPluginResult(pluginResult);
      } finally {
        locationService.stop();
      }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {
      super.onConnectHotSpotMessage(s, i);
    }

    /**
     * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
     * @param locType 当前定位类型
     * @param diagnosticType 诊断类型（1~9）
     * @param diagnosticMessage 具体的诊断信息释义
     */
    @Override
    public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
      super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
      int tag = 2;
      StringBuffer sb = new StringBuffer(256);
      sb.append("诊断结果: ");
      if (locType == BDLocation.TypeNetWorkLocation) {
        if (diagnosticType == 1) {
          sb.append("网络定位成功，没有开启GPS，建议打开GPS会更好");
          sb.append("\n" + diagnosticMessage);
        } else if (diagnosticType == 2) {
          sb.append("网络定位成功，没有开启Wi-Fi，建议打开Wi-Fi会更好");
          sb.append("\n" + diagnosticMessage);
        }
      } else if (locType == BDLocation.TypeOffLineLocationFail) {
        if (diagnosticType == 3) {
          sb.append("定位失败，请您检查您的网络状态");
          sb.append("\n" + diagnosticMessage);
        }
      } else if (locType == BDLocation.TypeCriteriaException) {
        if (diagnosticType == 4) {
          sb.append("定位失败，无法获取任何有效定位依据");
          sb.append("\n" + diagnosticMessage);
        } else if (diagnosticType == 5) {
          sb.append("定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位");
          sb.append(diagnosticMessage);
        } else if (diagnosticType == 6) {
          sb.append("定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试");
          sb.append("\n" + diagnosticMessage);
        } else if (diagnosticType == 7) {
          sb.append("定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试");
          sb.append("\n" + diagnosticMessage);
        } else if (diagnosticType == 9) {
          sb.append("定位失败，无法获取任何有效定位依据");
          sb.append("\n" + diagnosticMessage);
        }
      } else if (locType == BDLocation.TypeServerError) {
        if (diagnosticType == 8) {
          sb.append("定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
          sb.append("\n" + diagnosticMessage);
        }
      }
      logMsg(sb.toString(), tag);
    }
  };




  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    if (cbCtx == null || requestCode != REQUEST_CODE)
      return;
    for (int r : grantResults) {
      if (r == PackageManager.PERMISSION_DENIED) {
        JSONObject json = new JSONObject();
        json.put("describe", "定位失败");
        LOG.e(LOG_TAG, "权限请求被拒绝");
        cbCtx.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, json));
        return;
      }
    }

    performGetLocation();
  }

  /**
   * 权限获得完毕后进行定位
   */
  private void performGetLocation() {
    if (locationService == null) {
      locationService = new LocationService(this.webView,myListener);
    }
    locationService.start();
  }


  /**
   * 显示请求字符串
   *
   * @param str
   */
  public void logMsg(final String str, final int tag) {
    try {

      new Thread(new Runnable() {
        @Override
        public void run() {
          if (tag == Utils.RECEIVE_TAG) {
            LOG.e(LOG_TAG, str);
          } else if (tag == Utils.DIAGNOSTIC_TAG) {
            LOG.e(LOG_TAG, str);
          }
        }
      }).start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  /**
   * 安卓6以上动态权限相关
   */

  private static final int REQUEST_CODE = 100001;

  private boolean needsToAlertForRuntimePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return !cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    } else {
      return false;
    }
  }

  private void requestPermission() {
    ArrayList<String> permissionsToRequire = new ArrayList<String>();

    if (!cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
      permissionsToRequire.add(Manifest.permission.ACCESS_COARSE_LOCATION);

    if (!cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
      permissionsToRequire.add(Manifest.permission.ACCESS_FINE_LOCATION);

    String[] _permissionsToRequire = new String[permissionsToRequire.size()];
    _permissionsToRequire = permissionsToRequire.toArray(_permissionsToRequire);
    cordova.requestPermissions(this, REQUEST_CODE, _permissionsToRequire);
  }



  // /**
  //  * 插件主入口
  //  */
  @Override
  public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
    cbCtx = callbackContext;
    if ("getCurrentPosition".equalsIgnoreCase(action)) {
      if (!needsToAlertForRuntimePermission()) {
        performGetLocation();
      } else {
        requestPermission();
        // 会在onRequestPermissionResult时performGetLocation
      }
      return true;
    }

    return false;
  } 

}
