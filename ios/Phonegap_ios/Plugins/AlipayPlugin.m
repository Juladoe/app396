//
//  AlipayPlugin.m
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import "AlipayPlugin.h"
#import "AlipayViewController.h"

@implementation AlipayPlugin

@synthesize pluginResult;
@synthesize callbackId;

-(void)showPay:(CDVInvokedUrlCommand *)command
{
    NSString* url = [command.arguments objectAtIndex:0];
    self.callbackId = command.callbackId;
    
    if (url != nil && [url length] > 0) {
        AlipayViewController *alipayView = [[AlipayViewController alloc] initWithUrl:url alipayPlugin:self];
        //注册回调事件
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(alipayFinishCallback:) name:@"alipayCallback" object:nil];
        
        [[alipayView view] setFrame:[self.webView bounds]];
        [self.webView addSubview:[alipayView view]];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)alipayFinishCallback:(NSNotification*)noitfy
{
    NSLog(@"alipay finish");
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:@"alipayCallback"
                                                  object:nil];
    
    NSString* result = [noitfy object];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}
@end
