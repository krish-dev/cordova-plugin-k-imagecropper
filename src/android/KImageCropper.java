package in.co.indusnet.plugins.cordova.imagecropper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                cordovaCallbackContext.success(resultUri.toString());
                Log.d("Result URI", resultUri.toString());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
