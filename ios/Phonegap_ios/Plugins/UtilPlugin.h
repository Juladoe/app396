//
//  UtilPlugin.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import <Cordova/CDV.h>

@interface UtilPlugin : CDVPlugin{
    CDVPluginResult* pluginResult;
    NSString* callbackId;
}

-(void)hideTop:(CDVInvokedUrlCommand*)command;
-(void)showTop:(CDVInvokedUrlCommand*)command;

@property (nonatomic, retain) CDVPluginResult* pluginResult;
@property (nonatomic, retain) NSString* callbackId;
@end
