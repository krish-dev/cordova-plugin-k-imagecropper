#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>
#import "KImageCropper.h"

#define CDV_PHOTO_PREFIX @"cdv_photo_"

@interface KImageCropper ()
@property (copy) NSString* callbackId;
@end

@implementation KImageCropper

- (void)open:(CDVInvokedUrlCommand*)command {
    UIImage *image;
    NSDictionary *options = [command.arguments objectAtIndex:0];
    NSString *imagePath = [options objectForKey:@"url"];
    CGFloat ratioX = [[options objectForKey:@"ratioX"] floatValue];
    CGFloat ratioY = [[options objectForKey:@"ratioY"] floatValue];

    NSString *filePrefix = @"file://";

    if ([imagePath hasPrefix:filePrefix]) {
        imagePath = [imagePath substringFromIndex:[filePrefix length]];
    }


    if (!(image = [UIImage imageWithContentsOfFile:imagePath])) {
        NSDictionary *err = @{
                                @"message": @"Image doesn't exist",
                                @"code": @"ENOENT"
                            };
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:err];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    PECropViewController *cropController = [[PECropViewController alloc] init];
    cropController.delegate = self;
    cropController.image = image;
    cropController.toolbarHidden = YES;
    cropController.rotationEnabled = NO;
    cropController.cropAspectRatio = ratioX / ratioY;
    cropController.keepingCropAspectRatio = YES;

    self.callbackId = command.callbackId;
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:cropController];

    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        navigationController.modalPresentationStyle = UIModalPresentationFormSheet;
    }

    [self.viewController presentViewController:navigationController animated:YES completion:NULL];
}

- (void)getImageDimension:(CDVInvokedUrlCommand *)command {
    CDVPluginResult *result;
    NSMutableArray *imageArr = [command.arguments objectAtIndex:0];

    NSMutableArray *finalImages = [[NSMutableArray alloc] init];
    [finalImages removeAllObjects];

    for (int i = 0; i < [imageArr count]; i++) {
        UIImage *image;
        NSString *imagePath = [imageArr objectAtIndex:i];

        NSString *filePrefix = @"file://";

        if ([imagePath hasPrefix:filePrefix]) {
            imagePath = [imagePath substringFromIndex:[filePrefix length]];
        }


        if (!(image = [UIImage imageWithContentsOfFile:imagePath])) {
            NSObject *err = @{ @"error": @"Image doesn't exist" };
            [finalImages addObject:err];
        }else {
            NSMutableDictionary *imgObj = [[NSMutableDictionary alloc] init];
            [imgObj setValue:[NSNumber numberWithInt:image.size.width] forKey:@"width"];
            [imgObj setValue:[NSNumber numberWithInt:image.size.height] forKey:@"height"];
            [imgObj setValue:[NSString stringWithString:[[NSURL fileURLWithPath:imagePath] absoluteString]] forKey:@"imgPath"];
            [finalImages addObject:imgObj];
        }
    }

    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:finalImages];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

#pragma mark - PECropViewControllerDelegate

- (void)cropViewController:(PECropViewController *)controller didFinishCroppingImage:(UIImage *)croppedImage {
    [controller dismissViewControllerAnimated:YES completion:nil];
    if (!self.callbackId) return;

    NSData *data = UIImageJPEGRepresentation(croppedImage, (CGFloat) 100);
    NSString* filePath = [self tempFilePath:@"jpg"];
    CDVPluginResult *result;
    NSError *err;

    // save file
    if (![data writeToFile:filePath options:NSAtomicWrite error:&err]) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_IO_EXCEPTION messageAsString:[err localizedDescription]];
    }
    else {

        NSMutableDictionary *success = [[NSMutableDictionary alloc] init];
        [success setValue:[NSNumber numberWithInt:croppedImage.size.width] forKey:@"width"];
        [success setValue:[NSNumber numberWithInt:croppedImage.size.height] forKey:@"height"];
        [success setValue:[NSString stringWithString:[[NSURL fileURLWithPath:filePath] absoluteString]] forKey:@"imgPath"];

        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:success];
    }

    [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
    self.callbackId = nil;
}

- (void)cropViewControllerDidCancel:(PECropViewController *)controller {
    [controller dismissViewControllerAnimated:YES completion:nil];
    NSDictionary *err = @{
                          @"message": @"User cancelled",
                          @"code": @"userCancelled"
                          };
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:err];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    self.callbackId = nil;
}

#pragma mark - Utilites

- (NSString*)tempFilePath:(NSString*)extension
{
    NSString* docsPath = [NSTemporaryDirectory()stringByStandardizingPath];
    NSFileManager* fileMgr = [[NSFileManager alloc] init]; // recommended by Apple (vs [NSFileManager defaultManager]) to be threadsafe
    NSString* filePath;

    // generate unique file name
    int i = 1;
    do {
        filePath = [NSString stringWithFormat:@"%@/%@%03d.%@", docsPath, CDV_PHOTO_PREFIX, i++, extension];
    } while ([fileMgr fileExistsAtPath:filePath]);

    return filePath;
}

@end
