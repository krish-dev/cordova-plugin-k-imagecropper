# Cordova Image Cropper
Cordova plugin for image cropping with custom aspect ratio.

Platform Support
----------------
* android
* iOS (in dev)

## Usage

How to add plugin
------------------------
Type following command from CLI to add this plugin

```
    cordova plugin add cordova-plugin-k-imagecropper
```

The plugin creates the object `window.plugins.k.imagecropper` with the methods `open(options, onSuccess, onError)`.

Example:
-------------------------------------------------------
```
    var options = {
        url: imageUrl,              // required.
        ratio: "6/4",               // optional. (here you can define your custom ration) default: 1:1
        title: "Custom tilte"       // optional. (here you can put title of image cropper activity) default: Image Cropper
        autoZoomEnabled: false      // optional. (if it is true then cropper will automatically adjust the view) default: true
    }
    window.plugins.k.imagecropper.open(options, function(data) {
        // its return the cropped image cached url, you need to manually delete the image from the application cache.
        console.log(data);          
        $scope.croppedImage = data;
    }, function(error) {
        console.log(error);
    })
```
