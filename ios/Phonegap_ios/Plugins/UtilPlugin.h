//
//  UtilPlugin.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import <Cordova/CDV.h>

@interface UtilPlugin : CDVPlugin

@property (nonatomic, retain) CDVPluginResult* pluginResult;
@property (nonatomic, retain) NSString* callbackId;

-(void)hideTop:(CDVInvokedUrlCommand*)command;
-(void)showTop:(CDVInvokedUrlCommand*)command;

/**
 *  Find out what current device is
 *
 *  @return Model Identifier
 */
- (NSString *)platform;

/**
 *  Convert Model Identifier into real device name
 *
 *  @return Readable device name
 */
- (NSString *)platformString;

/**
 *  Check current system version
 *
 *  @return System version string
 */
- (NSString *)systemVersion;

@end
