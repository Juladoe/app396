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
- (void)platform:(CDVInvokedUrlCommand *)command;

/**
 *  Convert Model Identifier into real device name
 *
 *  @return Readable device name
 */
- (void)platformString:(CDVInvokedUrlCommand *)command;

/**
 *  Check current system version
 *
 *  @return System version string
 */
- (void)systemVersion:(CDVInvokedUrlCommand *)command;

/**
 *  Visit App Store to update
 *
 *  @param command url string
 */
- (void)visitAppStore:(CDVInvokedUrlCommand *)command;

@end
