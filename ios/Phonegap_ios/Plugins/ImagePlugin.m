//
//  ImagePlugin.m
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-6.
//
//

#import "ImagePlugin.h"

@implementation ImagePlugin

- (void)showImage:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* pluginResult = nil;
    
    for (id object in command.arguments) {
        BOOL isArray = [object isKindOfClass:[NSArray class]];
        NSLog(@"%d", isArray);
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isArray];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

@end
