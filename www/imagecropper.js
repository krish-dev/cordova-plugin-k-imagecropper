/*
=============================================================================
 * Developed By     :   Krishnendu Sekhar Das
 * Company          :   Indus Net Technologies Pvt. Ltd.
 * Plugin Name      :   KImageCropper
===========================================================================
*/

function KImageCropper() {}

KImageCropper.prototype.open = function (options, successCallback, errorCallback) {
    var finalOption = {
        url : "",
        ratioX : 1,
        ratioY : 1,
        autoZoomEnabled : true,
        title : "Image Cropper"
    };

    if(!options.url) {
        var error = {
            code : 101,
            message : "you need to pass the a valid image path in url key on your option object"
        }
        errorCallback(error);
        return false;
    } else {
        finalOption.url = options.url;
    }

    if(options.ratio) {
        var rationArr = options.ratio.split("/");
        if(rationArr.length > 0) {
            finalOption.ratioX = parseInt(rationArr[0]);
            finalOption.ratioY = parseInt(rationArr[1]);
        }
    }else {
        var error = {
            code : 102,
            message : "You have not provided valid format ratio"
        }
        errorCallback(error);
        return false;
    }

    if(options.autoZoomEnabled) {
        finalOption.autoZoomEnabled = options.autoZoomEnabled;
    }

    if(options.title) {
        finalOption.title = options.title;
    }

    cordova.exec(
        successCallback,
        errorCallback,
        "KImageCropper",
        "open",
        [finalOption]
    );
};

KImageCropper.prototype.getImageDimension = function (imageArr, successCallback, errorCallback) {
    if(!Array.isArray(imageArr)) {
        var error = {
            code : 201,
            message : "you should pass an array of image paths"
        }
        errorCallback(error);
        return false;
    }else {
        if(imageArr.length < 1) {
            var error = {
                code : 202,
                message : "Image array should not be less than one"
            }
            errorCallback(error);
            return false;
        }
    }

    cordova.exec(
        successCallback,
        errorCallback,
        "KImageCropper",
        "getImageDimension",
        [imageArr]
    );
};

KImageCropper.install = function () {
    if (!window.plugins.k) {
        window.plugins.k = {};
    }
    window.plugins.k.imagecropper = new KImageCropper();
    return window.plugins.k.imagecropper;
};

cordova.addConstructor(KImageCropper.install);
