package in.co.indusnet.plugins.cordova.imagecropper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class KImageCropper extends CordovaPlugin {

    String IMAGE_URL;
    Integer ASPECT_RATIO_X;
    Integer ASPECT_RATIO_Y;
    Boolean IS_ZOOMABLE;
    String ACTIVITY_TITLE;

    CallbackContext cordovaCallbackContext;

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        //Log.d("JSON", args.toString());
        if (action.equals("open")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    cordovaCallbackContext = callbackContext;
                    parseOptions(args);
                }
            });

            return true;
        }
        if (action.equals("getImageDimension")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    cordovaCallbackContext = callbackContext;
                    parseImagePaths(args);
                }
            });

            return true;
        }

        return false;
    }

    private void parseOptions(JSONArray jsonArray) {
        JSONObject options;
        try {
            options = jsonArray.getJSONObject(0);
            IMAGE_URL = options.getString("url");
            ASPECT_RATIO_X = options.getInt("ratioX");
            ASPECT_RATIO_Y = options.getInt("ratioY");
            IS_ZOOMABLE = options.getBoolean("autoZoomEnabled");
            ACTIVITY_TITLE = options.getString("title");

            openCropper();

        }catch (JSONException e){
            cordovaCallbackContext.error(e.getMessage());
        }
    }

    private  void parseImagePaths(JSONArray jsonArray) {
        JSONArray imagePathsArr;

        JSONArray successArr = new JSONArray();

        try {
            imagePathsArr = jsonArray.getJSONArray(0);

            for(Integer i=0;i<imagePathsArr.length();i++) {
                JSONObject img = new JSONObject();
                String imgPath = imagePathsArr.getString(i);
                String finalImgPath;

                // checking media path or absolute path.
                String ext = imgPath.substring(imgPath.lastIndexOf(".") + 1);

                if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("JPG") || ext.equals("JPEG") || ext.equals("png") || ext.equals("PNG")) {
                    finalImgPath =  imgPath;
                }else {
                    finalImgPath = getRealPathFromURI(cordova.getActivity().getApplicationContext(),Uri.parse(imgPath));
                }
                img = imageRatioCalculation(Uri.parse(finalImgPath));
                img.put("imgPath", imgPath);
                img.put("imgPathAbsolute", finalImgPath);
                successArr.put(img);
            }

            cordovaCallbackContext.success(successArr);

        }catch (JSONException e){
            cordovaCallbackContext.error(e.getMessage());
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void openCropper() {

        cordova.setActivityResultCallback(this);
        CropImage.activity(Uri.parse(IMAGE_URL))
                .setAspectRatio(ASPECT_RATIO_X,ASPECT_RATIO_Y)
                .setAutoZoomEnabled(IS_ZOOMABLE)
                .setActivityTitle(ACTIVITY_TITLE)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowRotation(false)
                .start(cordova.getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject succesJson = new JSONObject();
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    succesJson = imageRatioCalculation(resultUri);
                    succesJson.put("imgPath",resultUri.toString());
                    cordovaCallbackContext.success(succesJson);
                }catch (JSONException e) {
                    cordovaCallbackContext.error(e.getMessage());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                cordovaCallbackContext.error(error.getMessage());
            }
        }
    }

    private JSONObject imageRatioCalculation(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("width", imageWidth);
            jsonObject.put("height", imageHeight);
        }catch (JSONException e) {

        }
        return jsonObject;
    }
}
