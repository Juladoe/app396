//
//  ImagesViewController.m
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-6.
//
//

#import "ImagesViewController.h"
#import "XHImageViewer.h"
#import "UIImageView+XHURLDownload.h"

@interface ImagesViewController () <XHImageViewerDelegate>

@property (nonatomic, strong)NSArray *imageViews;
@property (nonatomic, strong)NSArray *urls;
@property (nonatomic)NSInteger index;

@end

@implementation ImagesViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (instancetype)initWithImageUrls:(NSArray *)urls atIndex:(NSInteger)index
{
    self = [super init];
    if (self){
        _urls = [NSArray arrayWithArray:urls];
        _index = index;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:YES];
    self.view.backgroundColor = [UIColor clearColor];
    
    NSMutableArray *tempArray = [NSMutableArray array];
    for (int i = 0; i < [_urls count]; i ++) {
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 100, 300, 280)];
        [imageView loadWithURL:[NSURL URLWithString:[_urls objectAtIndex:i]]
                    placeholer:nil
     showActivityIndicatorView:YES];
        
        [tempArray addObject:imageView];
    }
    _imageViews = [NSArray arrayWithArray:tempArray];
    
    XHImageViewer *imageViewer = [[XHImageViewer alloc] init];
    imageViewer.delegate = self;
    [imageViewer showWithImageViews:_imageViews selectedView:[_imageViews objectAtIndex:_index]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)prefersStatusBarHidden
{
    return YES;
}

- (BOOL)shouldAutorotate
{
    return YES;
}

- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

#pragma mark - XHImageViewerDelegate

- (void)imageViewer:(XHImageViewer *)imageViewer DidDismissWithSelectedView:(UIImageView *)selectedView
{
    [selectedView removeFromSuperview];
    [self dismissModalViewControllerAnimated:YES];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
