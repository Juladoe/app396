//
//  WelcomePlugin.m
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-11.
//
//

#import "WelcomePlugin.h"
#import "WelcomeViewController.h"

@implementation WelcomePlugin

- (void)showWelcomeImages:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = nil;
    
    NSArray *urls = command.arguments;
    if (urls) {
        WelcomeViewController *welcomeView = [[WelcomeViewController alloc] initWithImageUrls:urls];
        [[[UIApplication sharedApplication] delegate].window.rootViewController presentViewController:welcomeView
                                                                                             animated:YES
                                                                                           completion:nil];
        [[NSNotificationCenter defaultCenter] addObserverForName:@"finishWelcome" object:nil queue:nil usingBlock:^(NSNotification *note) {
            [self.webView stringByEvaluatingJavaScriptFromString:@"loadSchoolPanel"];
            [[NSNotificationCenter defaultCenter] removeObserver:self name:@"finishWelcome" object:nil];
        }];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}


@end
