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

   
    
}

- (void)pluginInitialize
{
    
    NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
    NSString* IOS_KEY = [[plistDic objectForKey:@"BaiduMapLocation"] objectForKey:@"IOS_KEY"];

    // 注册
    [[BMKLocationAuth sharedInstance] checkPermisionWithKey:IOS_KEY authDelegate:nil];
    //同意隐私合格政策
   [[BMKLocationAuth sharedInstance] setAgreePrivacy:YES];
 
    //初始化实例
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
    _mapManager.locationTimeout = 10;
    //设置获取地址信息超时时间
    _mapManager.reGeocodeTimeout = 10;
}

//字符串转时间戳 如：2017-4-10 17:15:10
- (NSString *)getTimeStrWithString:(NSString *)str{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];// 创建一个时间格式化对象
    [dateFormatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"]; //设定时间的格式
    NSDate *tempDate = [dateFormatter dateFromString:str];//将字符串转换为时间对象
    NSString *timeStr = [NSString stringWithFormat:@"%ld", (long)[tempDate timeIntervalSince1970]*1000];//字符串转成时间戳,精确到毫秒*1000
    return timeStr;
}

+(NSString *)dateConversionTimeStamp:(NSDate *)date
{
    NSString *timeSp = [NSString stringWithFormat:@"%ld", (long)[date timeIntervalSince1970]*1000];
    return timeSp;
}

- (void)dealloc {
    _mapManager = nil;
}

-(void) execGetSingleloc{
    [_mapManager requestLocationWithReGeocode:false withNetworkState:true completionBlock:^(BMKLocation * _Nullable userLocation, BMKLocationNetworkState state, NSError * _Nullable error){
        NSMutableDictionary* _data = [[NSMutableDictionary alloc] init];
        NSLog(@"netstate = %d",state);
        NSNumber* errorCode = nil;
        if(error){
            NSLog(@"locError:{%ld - %@};", (long)error.code, error.localizedDescription);
            errorCode = [NSNumber numberWithInteger: error.code];
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
        [self->_mapManager stopUpdatingLocation];
        // 未知错误 重试
        if(1 == (long)error.code && 4 > self->_errorLocnum){
            [self errorRetry];
        }else{
            [self execCallbakCommond:_data];
        }

    }];
}
- (void)getCurrentPosition:(CDVInvokedUrlCommand*)command
{
    _errorLocnum = 0;
    _execCommand = command;
    [self execGetSingleloc];

}

// 重新定位
- (void)errorRetry{
    _errorLocnum ++;
    NSLog(@"netstate = %d",_errorLocnum);
    [self performSelector:@selector(execGetSingleloc) withObject:nil afterDelay:1.0];
}
 
-(void)execCallbakCommond:(NSMutableDictionary *)_data
{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:_data];
    [result setKeepCallbackAsBool:TRUE];
    [self.commandDelegate sendPluginResult:result callbackId:_execCommand.callbackId];
    _execCommand = nil;
}


@end
