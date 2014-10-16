//
//  WelcomeViewController.h
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-11.
//
//

#import <Cordova/CDVViewController.h>
#import <UIKit/UIKit.h>

@interface WelcomeViewController : CDVViewController

- (instancetype)initWithImageUrls:(NSArray *)urls;
- (instancetype)initWithLocalImages:(NSArray *) paths;
@end
