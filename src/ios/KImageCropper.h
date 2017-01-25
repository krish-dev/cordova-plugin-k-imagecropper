#import <Cordova/CDV.h>
#import "PECropViewController.h"

/* ================================================================================
 * Developed By     : Krishnendu Sekhar Das
 * Company          : Indusnet Technologies Pvt. Ltd.
 * File             : KImageCropper.h
 ==================================================================================*/

@interface KImageCropper : CDVPlugin <PECropViewControllerDelegate> {}

- (void) open:(CDVInvokedUrlCommand*)command;
- (void) getImageDimension:(CDVInvokedUrlCommand*)command;
@end
