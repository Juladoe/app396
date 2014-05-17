//
//  UtilPlugin.m
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import "UtilPlugin.h"

@implementation UtilPlugin

@synthesize callbackId;
@synthesize pluginResult;

-(void)hideTop:(CDVInvokedUrlCommand *)command
{
    [self setTopStatus:false];
    self.callbackId = command.callbackId;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)showTop:(CDVInvokedUrlCommand *)command
{
    [self setTopStatus:true];
    self.callbackId = command.callbackId;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

-(void)setTopStatus:(BOOL)isShow
{
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7) {
        CGRect viewBounds = [self.webView bounds];
        if (isShow) {
            viewBounds.origin.y = 20;
            viewBounds.size.height = viewBounds.size.height - 20;
        } else {
            viewBounds.origin.y = 0;
            viewBounds.size.height = viewBounds.size.height;
        }
        self.webView.frame = viewBounds;
    }
}
@end
