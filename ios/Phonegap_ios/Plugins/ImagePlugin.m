//
//  ImagePlugin.m
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-6.
//
//

#import "ImagePlugin.h"
#import "ImagesViewController.h"
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
        ImagesViewController *imageViewer = [[ImagesViewController alloc] initWithImageUrls:urls
                                                                                    atIndex:index];
        [[[UIApplication sharedApplication] delegate].window.rootViewController presentViewController:imageViewer
                                                                                                 animated:YES
                                                                                               completion:nil];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

@end
