/*
 *
 * 　　┏┓　　　┏┓+ +
 * 　┏┛┻━━━┛┻┓ + +
 * 　┃　　　　　　　┃
 * 　┃　　　━　　　┃ ++ + + +
 *  ████━████ ┃+
 * 　┃　　　　　　　┃ +
 * 　┃　　　┻　　　┃
 * 　┃　　　　　　　┃ + +
 * 　┗━┓　　　┏━┛
 * 　　　┃　　　┃
 * 　　　┃　　　┃ + + + +
 * 　　　┃　　　┃
 * 　　　┃　　　┃ +  神兽保佑
 * 　　　┃　　　┃    代码无bug
 * 　　　┃　　　┃　　+
 * 　　　┃　 　　┗━━━┓ + +
 * 　　　┃ 　　　　　　　┣┓
 * 　　　┃ 　　　　　　　┏┛
 * 　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　┃┫┫　┃┫┫
 * 　　　　┗┻┛　┗┻┛+ + + +
 *
 */

//
//  BaiduMapLocation.mm
//
//  Created by LiuRui on 2017/2/25.
//  modify by ShawnSha on 2021/8/16
//

#import "BaiduMapLocation.h"

@implementation BaiduMapLocation

+ (void)load{
    NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
    NSString* IOS_KEY = [[plistDic objectForKey:@"BaiduMapLocation"] objectForKey:@"IOS_KEY"];

    // 注册
    [[BMKLocationAuth sharedInstance] checkPermisionWithKey:IOS_KEY authDelegate:nil];
    //同意隐私合格政策
    [[BMKLocationAuth sharedInstance] setAgreePrivacy:YES];
}

- (void)pluginInitialize
{
    // 要使用百度地图，请先启动BaiduMapManager
    _mapManager = [[BMKLocationManager alloc] init];
    //设置delegate
    _mapManager.delegate = self;
    //设置返回位置的坐标系类型
    _mapManager.coordinateType = BMKLocationCoordinateTypeBMK09LL;
    //设置距离过滤参数
    _mapManager.distanceFilter = kCLDistanceFilterNone;
    //设置预期精度参数
    _mapManager.desiredAccuracy = kCLLocationAccuracyBest;
    //设置应用位置类型
    _mapManager.activityType = CLActivityTypeAutomotiveNavigation;
     //设置是否自动停止位置更新
    _mapManager.pausesLocationUpdatesAutomatically = NO;
    //设置是否允许后台定位
    _mapManager.allowsBackgroundLocationUpdates = YES;
     //设置位置获取超时时间
    _mapManager.locationTimeout = 8;
    //设置获取地址信息超时时间
    _mapManager.reGeocodeTimeout = 8;
}

- (void)BMKLocationManager:(BMKLocationManager * _Nonnull)manager didUpdateLocation:(BMKLocation * _Nullable)userLocation orError:(NSError * _Nullable)error

{
//    if (error)
//    {
//        NSLog(@"locError:{%ld - %@};", (long)error.code, error.localizedDescription);
//    } if (location) {//得到定位信息，添加annotation
//
//                if (location.location) {
//                    NSLog(@"LOC = %@",location.location);
//                }
//                if (location.rgcData) {
//                    NSLog(@"rgc = %@",[location.rgcData description]);
//                }
//
//                if (location.rgcData.poiList) {
//                    for (BMKLocationPoi * poi in location.rgcData.poiList) {
//                        NSLog(@"poi = %@, %@, %f, %@, %@", poi.name, poi.addr, poi.relaiability, poi.tags, poi.uid);
//                    }
//                }
//
//                if (location.rgcData.poiRegion) {
//                    NSLog(@"poiregion = %@, %@, %@", location.rgcData.poiRegion.name, location.rgcData.poiRegion.tags, location.rgcData.poiRegion.directionDesc);
//                }
//
//            }
    
    
    //获取经纬度和该定位点对应的位置信息
    NSMutableDictionary* _data = [[NSMutableDictionary alloc] init];
    if(error){
        _errorLocnum++;
        NSLog(@"locError:{%ld - %@};", (long)error.code, error.localizedDescription);
        NSNumber* errorCode = [NSNumber numberWithInteger: error.code];
        NSString* errorDesc = error.localizedDescription;
        [_data setValue:errorCode forKey:@"errorCode"];
        [_data setValue:errorDesc forKey:@"errorDesc"];
    }if(userLocation){
        if(userLocation.location){
            NSDate* time = userLocation.location.timestamp;
            NSNumber* latitude = [NSNumber numberWithDouble:userLocation.location.coordinate.latitude];
            NSNumber* longitude = [NSNumber numberWithDouble:userLocation.location.coordinate.longitude];
            NSNumber* radius = [NSNumber numberWithDouble:userLocation.location.horizontalAccuracy];
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
            [_data setValue:[dateFormatter stringFromDate:time] forKey:@"time"];
            [_data setValue:latitude forKey:@"latitude"];
            [_data setValue:longitude forKey:@"longitude"];
            [_data setValue:radius forKey:@"radius"];
        }
        if(userLocation.rgcData){
            NSString* country = userLocation.rgcData.country;
            NSString* countryCode = userLocation.rgcData.countryCode;
            NSString* city = userLocation.rgcData.city;
            NSString* cityCode = userLocation.rgcData.cityCode;
            NSString* district = userLocation.rgcData.district;
            NSString* street = userLocation.rgcData.street;
            NSString* province = userLocation.rgcData.province;
            NSString* locationDescribe = userLocation.rgcData.locationDescribe;
            NSString* streetNumber = userLocation.rgcData.streetNumber;
            NSString* adCode = userLocation.rgcData.adCode;
            NSString* locTypeDescription  = @"successful";
            [_data setValue:countryCode forKey:@"countryCode"];
            [_data setValue:country forKey:@"country"];
            [_data setValue:cityCode forKey:@"citycode"];
            [_data setValue:city forKey:@"city"];
            [_data setValue:district forKey:@"district"];
            [_data setValue:street forKey:@"street"];
            [_data setValue:streetNumber forKey:@"street"];
            [_data setValue:province forKey:@"province"];
            [_data setValue:adCode forKey:@"adCode"];
            [_data setValue:locationDescribe forKey:@"locationDescribe"];
            [_data setValue:locTypeDescription forKey:@"locTypeDescription"];
        }
    }

    [self execCallbakCommond:_data];

}

-(void)startLocUpdate{
    [self->_mapManager stopUpdatingLocation];
    [self.commandDelegate runInBackground:^{
        //如果需要持续定位返回地址信息（需要联网），请设置如下：
        [self->_mapManager setLocatingWithReGeocode:NO];
        [self->_mapManager startUpdatingLocation];
    }];
}

- (void)getCurrentPosition:(CDVInvokedUrlCommand*)command
{
    _execCommand = command;
    _errorLocnum = 0;
    [self startLocUpdate];
}

-(void)execCallbakCommond:(NSMutableDictionary *)_data
{
    [_mapManager stopUpdatingLocation];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:_data];
    [result setKeepCallbackAsBool:TRUE];
    [self.commandDelegate sendPluginResult:result callbackId:_execCommand.callbackId];
    _execCommand = nil;
}


@end
