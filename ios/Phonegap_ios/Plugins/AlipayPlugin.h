//
//  AlipayPlugin.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import <Cordova/CDV.h>

@interface AlipayPlugin : CDVPlugin

-(void)showPay:(CDVInvokedUrlCommand*)command;

@end
