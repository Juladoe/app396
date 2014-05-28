//
//  AlipayPlugin.m
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import "AlipayPlugin.h"
#import "AlipayViewController.h"

@interface AlipayPlugin ()

@property (nonatomic, strong)AlipayViewController *alipayView;
@property (nonatomic, strong)CDVPluginResult *pluginResult;
@property (nonatomic, strong)NSString *callbackId;

@end

@implementation AlipayPlugin

-(void)showPay:(CDVInvokedUrlCommand *)command
{
    NSString* url = [command.arguments objectAtIndex:0];
    //???:_callbackId will crash
    self.callbackId = command.callbackId;
    
    if (url != nil && [url length] > 0) {
        _alipayView = [[AlipayViewController alloc] initWithUrl:url];
        //注册回调事件
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(alipayFinishCallback:) name:@"alipayCallback" object:nil];
        
        [_alipayView.view setFrame:[self.webView bounds]];
        [self.webView addSubview:_alipayView.view];
    } else {
        _pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:_pluginResult callbackId:command.callbackId];
    }
}

- (void)alipayFinishCallback:(NSNotification*)noitfy
{
    NSLog(@"alipay finish");
    [_alipayView.view removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:@"alipayCallback"
                                                  object:nil];
    
    NSString* result = [noitfy object];
    _pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
    [self.commandDelegate sendPluginResult:_pluginResult callbackId:_callbackId];
}

@end
