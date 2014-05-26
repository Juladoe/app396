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

- (NSString *)platform {
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];
    free(machine);
    return platform;
}

- (NSString *)platformString {
    NSString *platform = [self platform];
    if ([platform hasPrefix:@"iPhone6"])            return @"iPhone 5s";
    if ([platform isEqualToString:@"iPhone5,3"]
        || [platform isEqualToString:@"iPhone5,4"]) return @"iPhone 5c";
    if ([platform hasPrefix:@"iPhone5"])            return @"iPhone 5";
    if ([platform hasPrefix:@"iPhone4"])            return @"iPhone 4S";
    if ([platform hasPrefix:@"iPhone3"])            return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone2,1"])    return @"iPhone 3GS";
    if ([platform isEqualToString:@"iPhone1,2"])    return @"iPhone 3G";
    if ([platform isEqualToString:@"iPhone1,1"])    return @"iPhone 1G";
    if ([platform isEqualToString:@"iPod5,1"])      return @"iPod Touch 5G";
    if ([platform isEqualToString:@"iPod4,1"])      return @"iPod Touch 4G";
    if ([platform isEqualToString:@"iPod3,1"])      return @"iPod Touch 3G";
    if ([platform isEqualToString:@"iPod2,1"])      return @"iPod Touch 2G";
    if ([platform isEqualToString:@"iPod1,1"])      return @"iPod Touch 1G";
    if ([platform isEqualToString:@"iPad2,5"]
        || [platform isEqualToString:@"iPad2,6"]
        || [platform isEqualToString:@"iPad2,7"])   return @"iPad mini";
    if ([platform isEqualToString:@"iPad3,4"]
        || [platform isEqualToString:@"iPad3,5"]
        || [platform isEqualToString:@"iPad3,6"])   return @"iPad 4G";
    if ([platform hasPrefix:@"iPad3"])              return @"iPad 3G";
    if ([platform hasPrefix:@"iPad2"])              return @"iPad 2G";
    if ([platform isEqualToString:@"iPad1,1"])      return @"iPad";
    if ([platform isEqualToString:@"i386"]
        || [platform isEqualToString:@"x86_64"])    return @"iPhone Simulator";
    return platform;
}

- (NSString *)systemVersion {
    return [[UIDevice currentDevice] systemVersion];
}

@end
