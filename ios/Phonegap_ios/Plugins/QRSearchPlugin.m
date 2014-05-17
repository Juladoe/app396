//
//  QRSearchPlugin.m
//  Phonegap_ios
//
//  Created by howzhi on 14-5-8.
//
//

#import "QRSearchPlugin.h"
@implementation QRSearchPlugin

@synthesize pluginResult;
@synthesize callbackId;

-(void)search:(CDVInvokedUrlCommand *)command
{
    NSString* param = [command.arguments objectAtIndex:0];
    self.callbackId = command.callbackId;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(searchCallback:) name:@"searchCallback" object:nil];
}

-(void)searchCallback:(NSNotification*)noitfy
{
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:@"searchCallback"
                                                  object:nil];
    
    NSString* currentTime = @"xxx";
    NSString* totalTime = @"yyy";
    
    NSArray* array = [NSArray arrayWithObjects:currentTime,totalTime, nil];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:array];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
}

-(void)showCamera
{
    ZXingWidgetController *widController = [[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO];
    NSMutableSet *readers = [[NSMutableSet alloc ] init];
    QRCodeReader* qrcodeReader = [[QRCodeReader alloc] init];
    [readers addObject:qrcodeReader];
    [qrcodeReader release];
    widController.readers = readers;
    [readers release];
    [self presentModalViewController:widController animated:YES];
    [widController release];
}

- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result;
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"ddd"
                                                    message:@"ddddddxxx"
                                                   delegate:self
                                          cancelButtonTitle:@"确定"
                                          otherButtonTitles:@"取消", nil];
    [alert show];
    [alert release];
    NSLog(@"%@",result);
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller
{
    NSLog(@"cancel");
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
