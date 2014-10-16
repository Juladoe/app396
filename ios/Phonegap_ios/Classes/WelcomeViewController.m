//
//  WelcomeViewController.m
//  Phonegap_ios
//
//  Created by Edusoho on 14-6-11.
//
//

#import "WelcomeViewController.h"
#import "UIImageView+XHURLDownload.h"
#import "MYBlurIntroductionView.h"

@interface WelcomeViewController () <MYIntroductionDelegate>

@end

@implementation WelcomeViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(instancetype)initWithLocalImages:(NSArray *)paths
{
    self = [super init];
    if (self) {
        NSMutableArray *panels = [NSMutableArray array];
        for (int i = 0; i < [paths count]; i ++) {
            UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.view.bounds];
            imageView.image = [UIImage imageNamed:[paths objectAtIndex:i]];
            
            MYIntroductionPanel *panel = [[MYIntroductionPanel alloc] initWithFrame:self.view.bounds];
            [panel addSubview:imageView];
            [panels addObject:panel];
        }
        MYBlurIntroductionView *welcomeView = [[MYBlurIntroductionView alloc] initWithFrame:self.view.bounds];
        welcomeView.delegate = self;
        [welcomeView buildIntroductionWithPanels:panels];
        [self.view addSubview:welcomeView];
    }
    
    return self;
}

- (instancetype)initWithImageUrls:(NSArray *)urls
{
    self = [super init];
    if (self) {
        NSMutableArray *panels = [NSMutableArray array];
        for (int i = 0; i < [urls count]; i ++) {
            UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.view.bounds];
            [imageView loadWithURL:[NSURL URLWithString:[urls objectAtIndex:i]]
                        placeholer:[UIImage imageNamed:@"HolderImage"]
         showActivityIndicatorView:YES];
            
            MYIntroductionPanel *panel = [[MYIntroductionPanel alloc] initWithFrame:self.view.bounds];
            [panel addSubview:imageView];
            [panels addObject:panel];
        }
        MYBlurIntroductionView *welcomeView = [[MYBlurIntroductionView alloc] initWithFrame:self.view.bounds];
        welcomeView.delegate = self;
        [welcomeView buildIntroductionWithPanels:panels];
        [self.view addSubview:welcomeView];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - MYIntroduction Delegate

- (void)introduction:(MYBlurIntroductionView *)introductionView didFinishWithType:(MYFinishType)finishType
{
    [self dismissViewControllerAnimated:YES completion:^{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"finishWelcome" object:nil];
    }];
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
