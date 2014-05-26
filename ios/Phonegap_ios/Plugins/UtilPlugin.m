//
//  UtilPlugin.m
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import "UtilPlugin.h"
#import <sys/types.h>
#import <sys/sysctl.h>

@implementation UtilPlugin

-(void)hideTop:(CDVInvokedUrlCommand *)command
{
    [self setTopStatus:false];
    _callbackId = command.callbackId;
    _pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:_pluginResult callbackId:command.callbackId];
}

-(void)showTop:(CDVInvokedUrlCommand *)command
{
    [self setTopStatus:true];
    _callbackId = command.callbackId;
    _pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:_pluginResult callbackId:command.callbackId];

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

#pragma mark - platform

- (NSString *)checkPlatform {
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];
    free(machine);
    return platform;
}

- (void)platform:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSString *platform = [self checkPlatform];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:platform];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)platformString:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSString *platform = [self checkPlatform];
    if ([platform hasPrefix:@"iPhone6"])                platform = @"iPhone 5s";
    else if ([platform isEqualToString:@"iPhone5,3"]
        || [platform isEqualToString:@"iPhone5,4"])     platform = @"iPhone 5c";
    else if ([platform hasPrefix:@"iPhone5"])           platform = @"iPhone 5";
    else if ([platform hasPrefix:@"iPhone4"])           platform = @"iPhone 4S";
    else if ([platform hasPrefix:@"iPhone3"])           platform = @"iPhone 4";
    else if ([platform isEqualToString:@"iPhone2,1"])   platform = @"iPhone 3GS";
    else if ([platform isEqualToString:@"iPhone1,2"])   platform = @"iPhone 3G";
    else if ([platform isEqualToString:@"iPhone1,1"])   platform = @"iPhone 1G";
    else if ([platform isEqualToString:@"iPod5,1"])     platform = @"iPod Touch 5G";
    else if ([platform isEqualToString:@"iPod4,1"])     platform = @"iPod Touch 4G";
    else if ([platform isEqualToString:@"iPod3,1"])     platform = @"iPod Touch 3G";
    else if ([platform isEqualToString:@"iPod2,1"])     platform = @"iPod Touch 2G";
    else if ([platform isEqualToString:@"iPod1,1"])     platform = @"iPod Touch 1G";
    else if ([platform isEqualToString:@"iPad2,5"]
        || [platform isEqualToString:@"iPad2,6"]
        || [platform isEqualToString:@"iPad2,7"])       platform = @"iPad mini";
    else if ([platform isEqualToString:@"iPad3,4"]
        || [platform isEqualToString:@"iPad3,5"]
        || [platform isEqualToString:@"iPad3,6"])       platform = @"iPad 4G";
    else if ([platform hasPrefix:@"iPad3"])             platform = @"iPad 3G";
    else if ([platform hasPrefix:@"iPad2"])             platform = @"iPad 2G";
    else if ([platform isEqualToString:@"iPad1,1"])     platform = @"iPad";
    else if ([platform isEqualToString:@"i386"]
        || [platform isEqualToString:@"x86_64"])        platform = @"iPhone Simulator";
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:platform];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)systemVersion:(CDVInvokedUrlCommand *)command {
    NSString *callbackId = command.callbackId;
    NSString *sVersion = [[UIDevice currentDevice] systemVersion];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:sVersion];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

@end
