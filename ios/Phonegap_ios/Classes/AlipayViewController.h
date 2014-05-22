//
//  AlipayViewController.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import <UIKit/UIKit.h>
#import "AlipayPlugin.h"

@interface AlipayViewController : UIViewController<UIWebViewDelegate>

@property (nonatomic, retain)NSString *url;
@property (nonatomic, retain)AlipayPlugin *alipayPlugin;
@property (nonatomic, retain)UIWebView *webView;

- (id)initWithUrl:(NSString *)url alipayPlugin:(AlipayPlugin *)alipayPlugin;

@end
