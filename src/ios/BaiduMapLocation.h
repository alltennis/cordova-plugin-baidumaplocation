/*
 * @Date: 2021-08-16 13:37:42
 * @LastEditTime: 2021-09-07 16:56:23
 * @LastEditors: Please set LastEditors
 * @Description: 升级百度地图2.0 SDK
 * @FilePath: /cordova-plugin-baidumaplocation/src/ios/BaiduMapLocation.h
 */
//
//  BaiduMapLocation.h
//
//  Created by LiuRui on 2017/2/25.
//

#import <Cordova/CDV.h>
 
#import <BMKLocationkit/BMKLocationComponent.h>
#import <BMKLocationkit/BMKLocationAuth.h>

@interface BaiduMapLocation : CDVPlugin<BMKLocationManagerDelegate> {
    CDVInvokedUrlCommand* _execCommand;
}
@property(nonatomic, assign) int errorLocnum;
@property(nonatomic, strong) BMKLocationManager *mapManager;

- (void)getCurrentPosition:(CDVInvokedUrlCommand*)command;
@end
