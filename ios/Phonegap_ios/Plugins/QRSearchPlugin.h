//
//  QRSearchPlugin.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-8.
//
//

#import <Cordova/CDV.h>
#import "ZXingWidgetController.h"
#import "QRCodeReader.h"

@interface QRSearchPlugin : CDVPlugin<ZXingDelegate>

- (void) search:(CDVInvokedUrlCommand*)command;
@property (nonatomic, retain) CDVPluginResult* pluginResult;
@property (nonatomic, retain) NSString* callbackId;
@end


