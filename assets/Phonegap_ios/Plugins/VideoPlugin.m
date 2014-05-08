//
//  VideoPlugin.m
//  Phonegap_ios
//
//  Created by howzhi on 14-4-4.
//
//

#import "VideoPlugin.h"

@implementation VideoPlugin
@synthesize player;

@synthesize pluginResult;
@synthesize callbackId;

- (void) playvideo:(CDVInvokedUrlCommand *)command
{
    NSString* url = [command.arguments objectAtIndex:0];
    self.callbackId = command.callbackId;
    
    if (url != nil && [url length] > 0) {
        player = [[MPMoviePlayerViewController alloc] initWithContentURL:[NSURL URLWithString:url]];
        //设置视频的播放模式
        player.moviePlayer.controlStyle=MPMovieControlStyleFullscreen;
        
        CGAffineTransform landscapeTransform = CGAffineTransformMakeRotation(M_PI / 2);
        player.view.transform = landscapeTransform;
        //设置视频屏幕模式
        player.moviePlayer.scalingMode=MPMovieScalingModeAspectFit;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(movieFinishEndCallback:) name:MPMoviePlayerPlaybackDidFinishNotification object:player.moviePlayer];
        
        //self.webView 获取当前webView，添加播放器view
        [[player view] setFrame:[self.webView bounds]]; // size to fit parent view exactly
        [self.webView addSubview:[player view]];
        [player.moviePlayer play];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
    
}

//播放器结束回调
- (void)movieFinishEndCallback:(NSNotification*)noitfy
{
    
    MPMoviePlayerController* theControl = [noitfy object];
    [[NSNotificationCenter defaultCenter] removeObserver:self
        name:MPMoviePlayerPlaybackDidFinishNotification
        object:theControl];
    [theControl.view removeFromSuperview];
    int intTime = theControl.currentPlaybackTime * 1000;
    int duration = theControl.duration * 1000;
    NSString* currentTime = [NSString stringWithFormat:@"%i", intTime];
    NSString* totalTime = [NSString stringWithFormat:@"%i", duration];
    
    NSArray* array = [NSArray arrayWithObjects:currentTime,totalTime, nil];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:array];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

@end
