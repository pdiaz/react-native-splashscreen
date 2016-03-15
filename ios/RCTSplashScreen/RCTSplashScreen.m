//
//  RCTSplashScreen.m
//  RCTSplashScreen
//
//  Created by fangyunjiang on 15/11/20.
//  Copyright (c) 2015年 remobile. All rights reserved.
//

#import "RCTSplashScreen.h"

static RCTRootView *rootView = nil;

@interface RCTSplashScreen()

@end

@implementation RCTSplashScreen

RCT_EXPORT_MODULE(SplashScreen)

+ (void)show:(RCTRootView *)v image:(UIImage*)image
{
    rootView = v;

    UIImageView *view = [[UIImageView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    view.image = image;
    
    [[NSNotificationCenter defaultCenter] removeObserver:rootView  name:RCTContentDidAppearNotification object:rootView];
    
    [rootView setLoadingView:view];
}


RCT_EXPORT_METHOD(hide)
{
    if (!rootView) {
        return;
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)),
                   dispatch_get_main_queue(),
                   ^{
                       [UIView animateWithDuration:0.4
                                        animations:^{
                                            UIView *loadingView = rootView.loadingView;
                                            loadingView.alpha = 0;
                                        }
                                        completion:^(__unused BOOL finished) {
                                            [rootView.loadingView removeFromSuperview];
                                        }];
                   });
}

@end
