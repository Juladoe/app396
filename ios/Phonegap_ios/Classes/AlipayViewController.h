//
//  AlipayViewController.h
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import <UIKit/UIKit.h>

@interface AlipayViewController : UIViewController<UIWebViewDelegate>

@property (nonatomic, retain)NSString *url;
@property (nonatomic, retain)NSString *host;
@property (nonatomic, retain)UIWebView *webView;

- (id)initWithUrl:(NSString *)url;

@end
