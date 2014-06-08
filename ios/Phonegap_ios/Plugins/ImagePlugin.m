//
//  ImagePlugin.m
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-6.
//
//

#import "ImagePlugin.h"
#import "XHImageViewer.h"
#import "UIImageView+XHURLDownload.h"

@interface ImagePlugin () <XHImageViewerDelegate>

@end

@implementation ImagePlugin

- (void)showImage:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = nil;
    
    NSInteger index = [[command.arguments objectAtIndex:0] integerValue];
    NSArray *urls = [command.arguments objectAtIndex:1];
    
    if ((index >=0) && urls) {
        NSMutableArray *imageViews = [NSMutableArray array];
        for (int i = 0; i < [urls count]; i ++) {
            UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 100, 300, 280)];
            [imageView loadWithURL:[NSURL URLWithString:[urls objectAtIndex:i]]
                        placeholer:nil
         showActivityIndicatorView:YES];
            [imageViews addObject:imageView];
        }
        
        XHImageViewer *imageViewer = [[XHImageViewer alloc] init];
        imageViewer.delegate = self;
        [imageViewer showWithImageViews:imageViews selectedView:[imageViews objectAtIndex:index]];
        
        [[UIApplication sharedApplication]setStatusBarHidden:YES
                                               withAnimation:UIStatusBarAnimationFade];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

#pragma mark - XHImageViewerDelegate

- (void)imageViewer:(XHImageViewer *)imageViewer DidDismissWithSelectedView:(UIImageView *)selectedView
{
    [selectedView removeFromSuperview];
    [[UIApplication sharedApplication]setStatusBarHidden:NO
                                           withAnimation:UIStatusBarAnimationFade];
}

@end
