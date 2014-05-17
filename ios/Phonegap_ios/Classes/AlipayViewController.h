//
//  AlipayViewController.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import <UIKit/UIKit.h>
#import "AlipayPlugin.h"

@interface AlipayViewController : UIViewController<UIWebViewDelegate>{
    UIWebView *webView;
}

@property NSString* url;
@property AlipayPlugin* alipayPlugin;
@property UIWebView* webView;

-(id) initWithUrl:(NSString*)url alipayPlugin:(AlipayPlugin*) alipayPlugin;
@end
