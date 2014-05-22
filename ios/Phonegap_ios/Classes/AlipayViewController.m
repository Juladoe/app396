//
//  AlipayViewController.m
//  Phonegap_ios
//
//  Created by howzhi on 14-5-14.
//
//

#import "AlipayViewController.h"
#import "HeaderViewController.h"

@interface AlipayViewController ()

@end

@implementation AlipayViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (UIView*)buildOverlayView {

    CGRect bounds = self.view.bounds;
    bounds = CGRectMake(0, 0, bounds.size.width, bounds.size.height);
    
    UIView* overlayView = [[[UIView alloc] initWithFrame:bounds] autorelease];
    overlayView.autoresizesSubviews = YES;
    overlayView.autoresizingMask    = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    overlayView.opaque              = NO;
    
    UIToolbar* toolbar = [[[UIToolbar alloc] init] autorelease];
    toolbar.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    
    id cancelButton = [[[UIBarButtonItem alloc] autorelease]
                       initWithBarButtonSystemItem:UIBarButtonSystemItemCancel
                       target:(id)self
                       action:@selector(leftBtnClick:)
                       ];
    
    id flexSpace = [[[UIBarButtonItem alloc] autorelease]
                    initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace
                    target:nil
                    action:nil
                    ];
    
#if USE_SHUTTER
    id shutterButton = [[UIBarButtonItem alloc]
                        initWithBarButtonSystemItem:UIBarButtonSystemItemCamera
                        target:(id)self
                        action:@selector(shutterButtonPressed)
                        ];
    
    toolbar.items = [NSArray arrayWithObjects:flexSpace,cancelButton,flexSpace,shutterButton,nil];
#else
    toolbar.items = [NSArray arrayWithObjects:flexSpace,cancelButton,flexSpace,nil];
#endif
    bounds = overlayView.bounds;
    
    [toolbar sizeToFit];
    CGFloat toolbarHeight  = [toolbar frame].size.height;
    CGFloat rootViewHeight = CGRectGetHeight(bounds);
    CGFloat rootViewWidth  = CGRectGetWidth(bounds);
    CGRect  rectArea       = CGRectMake(0, rootViewHeight - toolbarHeight, rootViewWidth, toolbarHeight);
    [toolbar setFrame:rectArea];
    
    [overlayView addSubview: toolbar];
    
    
    CGRect r = [ UIScreen mainScreen ].bounds;
    CGRect frame = [ UIScreen mainScreen ].applicationFrame;
    
    _webView = [[UIWebView alloc] initWithFrame:CGRectMake(0, 0, r.size.width, frame.size.height - toolbarHeight)];
    _webView.delegate = self;
    
    [overlayView addSubview: _webView];
    
    return overlayView;
}

- (void)viewWillAppear:(BOOL)animated
{
     if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7) {
         CGRect viewBounds = [_webView bounds];
         viewBounds.origin.y = 20;
         viewBounds.size.height = viewBounds.size.height - 20;
         _webView.frame = viewBounds;
     }
    [super viewWillAppear:animated];
}

-(id) initWithUrl:(NSString*)url alipayPlugin:(AlipayPlugin*) alipayPlugin
{
    self = [super init];
    if (self){
        _url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        _alipayPlugin = alipayPlugin;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
  
    NSURLRequest *request =[NSURLRequest requestWithURL:[NSURL URLWithString:_url]];

    [self.view addSubview:[self buildOverlayView]];
    [_webView loadRequest:request];
    NSLog(@"init webview %@", _url);
}

-(void)leftBtnClick:(id)sender{
    //发送支付回调信息
    [[NSNotificationCenter defaultCenter] postNotificationName:@"alipayCallback" object:@"error"];
    [self.view removeFromSuperview];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(BOOL)webView:(UIWebView*)webView shouldStartLoadWithRequest:(NSURLRequest*)request navigationType:(UIWebViewNavigationType)navigationType{
    NSURL* queryURL = request.URL;
    if (! queryURL) {
        return true;
    }
    NSString* scheme = queryURL.scheme;
    if ([scheme isEqualToString:@"objc"]) {
        NSString* host = queryURL.host;
        if ([host isEqualToString:@"alipayCallback"]) {
            NSString* query = queryURL.query;
            //发送支付回调信息
            [[NSNotificationCenter defaultCenter] postNotificationName:@"alipayCallback" object:query];
            [self.view removeFromSuperview];
            return false;
        }
    }
    return true;
}


@end
