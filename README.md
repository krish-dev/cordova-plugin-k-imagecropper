# Cordova Image Cropper
Cordova plugin for image cropping with custom aspect ratio.

Platform Support
----------------
* android
* iOS

## Usage

How to add plugin
------------------------
Type following command from CLI to add this plugin

```
cordova plugin add cordova-plugin-k-imagecropper
```

The plugin creates the object `window.plugins.k.imagecropper`.

Methods
------------------------
* open
* getImageDimension

Example: open
-------------------------------------------------------
```js
    var options = {
        url: imageUrl,              // required.
        ratio: "6/4",               // required. (here you can define your custom ration) "1/1" for square images
        title: "Custom title",      // optional. android only. (here you can put title of image cropper activity) default: Image Cropper
        autoZoomEnabled: false      // optional. android only. for iOS its always true (if it is true then cropper will automatically adjust the view) default: true
    }
    window.plugins.k.imagecropper.open(options, function(data) {
        // its return an object with the cropped image cached url, cropped width & height, you need to manually delete the image from the application cache.
        console.log(data);          
        $scope.croppedImage = data;
    }, function(error) {
        console.log(error);
    })
```

Example: getImageDimension
-------------------------------------------------------
```js
    var imagesArr = [
        "file:///data/user/0/com.ionicframework.creativesdk/cache/tmp_IMG_20170123_0857001689046463.jpg",
        "file:///data/user/0/com.ionicframework.creativesdk/cache/tmp_IMG_20170123_0855131930060303.jpg",
        "file:///data/user/0/com.ionicframework.creativesdk/cache/tmp_IMG-20170123-WA0004-768394128.jpg",
        "file:///data/user/0/com.ionicframework.creativesdk/cache/tmp_IMG_20170123_0856091088191830.jpg",
        "file:///data/user/0/com.ionicframework.creativesdk/cache/tmp_IMG_20170123_085513-1271654176.jpg"
    ];
    window.plugins.k.imagecropper.getImageDimension(imagesArr, function(data) {
        // its return an array of object with the image url, width & height
        console.log(data);          
        $scope.croppedImage = data;
    }, function(error) {
        console.log(error);
    })
```
