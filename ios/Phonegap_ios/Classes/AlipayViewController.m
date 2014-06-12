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
        
        if (IOS7_OR_LATER) {
            self.edgesForExtendedLayout = UIRectEdgeNone;
            self.extendedLayoutIncludesOpaqueBars = NO;
            self.automaticallyAdjustsScrollViewInsets = NO;
        }
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel
                                                                                              target:self
                                                                                              action:@selector(leftBtnClick:)];
    }
    return self;
}

-(id) initWithUrl:(NSString*)url
{
    self = [super init];
    if (self){
        _url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSURL* tempUrl = [NSURL URLWithString:url];
        _host = tempUrl.host;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _webView = [[UIWebView alloc] initWithFrame:self.view.bounds];
    _webView.delegate = self;
    [self.view addSubview:_webView];
    NSURLRequest *request =[NSURLRequest requestWithURL:[NSURL URLWithString:_url]];
    [_webView loadRequest:request];
}

- (void)leftBtnClick:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
    //发送支付回调信息
    [[NSNotificationCenter defaultCenter] postNotificationName:@"alipayCallback" object:@"error"];
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
            [self dismissViewControllerAnimated:YES completion:nil];
            return false;
        }
    }
    return true;
}

@end
