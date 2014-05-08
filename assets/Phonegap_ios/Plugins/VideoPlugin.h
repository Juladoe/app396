//
//  VideoPlugin.h
//  Phonegap_ios
//
//  Created by howzhi on 14-4-4.
//
//

#import <Cordova/CDV.h>
#import <MediaPlayer/MediaPlayer.h>
@interface VideoPlugin : CDVPlugin{
    MPMoviePlayerViewController *player;
    CDVPluginResult* pluginResult;
    NSString* callbackId;
}
- (void) playvideo:(CDVInvokedUrlCommand*)command;

@property (nonatomic, retain) MPMoviePlayerViewController *player;
@property (nonatomic, retain) CDVPluginResult* pluginResult;
@property (nonatomic, retain) NSString* callbackId;
@end
