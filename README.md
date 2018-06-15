# HyperSnapSDK Documentation

## Overview

HyperSnapSDK is HyperVerge's documents + face capture SDK that captures images at a resolution appropriate for our proprietary Deep Learning OCR and Face Recognition Engines.

The framework provides a liveness feature that uses our advanced AI Engines to tell if a captured image is that of a real person or a photograph.

### Requirements
- Gradle Version: 4.1 (Recommended)
- Tested with Gradle Plugin for Android Studio - version 3.0.1
- minSdkVersion 19
- targetSdkVersion 26

## Table Of Contents
- [Overview](#overview)
	- [Requirements](#requirements)
- [Table Of Contents](#table-of-contents)
- [Example Project](#example-project)
- [Integration Steps](#integration-steps)
	- [1. Adding the SDK to your project](#1-adding-the-sdk-to-your-project)
	- [2. App Permissions](#2-app-permissions)
	- [3. Initializing the SDK](#3-initializing-the-sdk)
	- [4. Launching the Activities](#4-launching-the-activities)
		- [For Document Capture](#for-document-capture)
		- [For Face Capture](#for-face-capture)
		- [CaptureCompletionHandler](#capturecompletionhandler)
		- [Liveness in Face Capture:](#liveness-in-face-capture)
- [Error Codes](#error-codes)
- [Advanced](#advanced)
	- [Customizations](#customizations)
- [Contact Us](#contact-us)

## Example Project
- Please refer to the sample app provided in the repo to get an understanding of the implementation process.
- To run the app, clone/download the repo and open **sample** using latest version of Android Studio
- Open project build.gradle and replace **aws_access_key** and **aws_secret_pass** with the credentials provided by HyperVerge
- Build and run the app

## Integration Steps

### 1. Adding the SDK to your project
- Add the following set of lines to your `app/build.gradle`

  ```groovy
  android {
      defaultConfig {
          renderscriptTargetApi 19
          renderscriptSupportModeEnabled true
	  }
  }
  dependencies {
      implementation('co.hyperverge:hypersnapsdk:2.0.3@aar', {
          transitive=true
          exclude group: 'com.android.support'
      })
  }
  ```
- Add the following set of lines to the Project (top-level) `build.gradle`

  ```groovy
  allprojects {
      repositories {
          maven {
              url "s3://hvsdk/android/releases"
              credentials(AwsCredentials) {
                  accessKey aws_access_key
                  secretKey aws_secret_pass
              }
          }
      }
  }
  ```
  where **aws_access_key** and **aws_secret_pass** will be given by HyperVerge
  
### 2. App Permissions
-  The app requires the following permissions to work.
    - *Camera*
    - *Autofocus*

    Kindly note that for android v23 (Marshmallow) and above, you need to handle the runtime permissions inside your app.

### 3. Initializing the SDK
- Add the following line to your Application class (the class which extends android.app.Application) for initializing our Library. This must be run only once. Check [this](https://guides.codepath.com/android/Understanding-the-Android-Application-Class) link if you are unsure of what an Application class is. 
  ```java
  HyperSnapSDK.init(context, APP_ID, APP_KEY, region, product);
  ```
	Where,
	- appId, appKey are given by HyperVerge
	- region: This is an enum, `HypeSnapParams.Region` with three values - `AsiaPacific`, `India` and `UnitedStates`.
	- product: This is an enum, `HyperSnapParams.Product` with two values - `faceID` annd `faceIAM`. 

### 4. Launching the Activities

#### For Document Capture
 For capturing documents (based on Aspect Ratio), following method should be called:

  ```java
  HVDocsActivity.start(context, document, myCaptureCompletionListener);
  ```
  where:
  - **context** is the context of the current Activity being displayed
  -  **myCaptureCompletionListener** is an object of `CaptureCompletionHandler` and has been described later
  - **document** is an enum of type HyperSnapParams.Document. It specifies the document type that needs to be captured. 
    The parameter can be initialized as follows:
   ```java
  Document document = HyperSnapParams.Document.CARD;
  ```
   where `aspectRatio` is a float specifying the aspectRatio of the document
      
  Following are the types of documents supported by the Document enum:
  - **CARD**: Aspect ratio : 0.625. Example: Vietnamese National ID, Driving License, Motor Registration Certificate
  - **PASSPORT**: Aspect ratio: 0.67. Example: Passports
  - **A4**: Aspect ratio: 1.4. Example: Bank statement, insurance receipt
  - **OTHER**: This is for aspect ratios that don't fall in the above categories. In this case, the aspect ratio should be set in the next line by calling `document.setAspectRatio(aspectRatio);`

  Also, Document supports the following customizations:
  - **topText**: The text displayed at the top section of the Camera Preview in HVDocsActivity.  It meant to tell the user about the document type. The text can be altered by calling following method:
    ```java
    document.setTopText("Driving Licence Front Side");
    ```
  - **bottomText**: The text displayed at the bottom end of the Camera Preview in HVDocsActivity. This is to communicate the positioning of the document to the user. The text can be altered by calling following method:
    ```java
    document.setBottomText("Place your Driving License inside the Box");

#### For Face Capture
For capturing face image, following method should be called:
  ```java
  HVFaceActivity.start(context, HyperSnapParams.LivenessMode.MODE, myCaptureCompletionListener);
  ```
  where:
  - **context** is the context of the current Activity being displayed
  - **myCaptureCompletionListener** is an object of `CaptureCompletionHandler` and has been described later.
  - **LivenessMode.MODE** described later.
 

#### CaptureCompletionHandler
CaptureCompletionHandler is an interface whose object needs to be passed with start method of both FaceCaptureActivity and DocumentActivity. It has methods which has to be implemented by the object to handle both the responses of document capture and the errors that occured during capture. Following is a sample implementation of CaptureCompletionHandler:
  ```java
  CaptureCompletionHandler myCaptureCompletionListener = new CaptureCompletionHandler() {
    @Override
    public void onResult(CaptureError error, JSONObject result) {
        if(error != null) {
            Log.e("LandingActivity", error.getError() + " :: " + error.getErrMsg());
        }
        else{
            Log.i("Landing Activity", result.toString());
            //result will have following keys:
            //    • imageUri: String path of the captured image
        }
    }
  }
  ```

#### Liveness in Face Capture:
The SDK has two liveness detection methods. Texture liveness and Gesture Liveness. This can be set using the `livenessMode` parameter in the `start` method of `
ctivity` discussed earlier.

Here, `livenessMode` is of type `HypeSnapParams.LivenessMode`, an enum with 3 values:

**.none**: No liveness test is performed. The selfie that is captured is simply returned. If successful, the result JSON in the CaptureCompletionHandler has one key-value pair. 
- `imageUri` : local path of the image captured

**.textureLiveness** : Texture liveness test is performed on the selfie captured.  If successful, a result JSON with the following key-value pairs is returned in the CaptureCompletionHandler

- `imageUri` : String. Local path of the image captured <br/>
- `live`: String with values 'yes'/'no'. Tells whether the selfie is live or not.
- `liveness-score`: Float with values between 0 and 1.
- The confidence score for the liveness prediction.
- `to-be-reviewed`: String with values 'yes'/'no'. Yes indicates that it flagged for manual review.

**.textureAndGestureLiveness**: In this mode, based on the results of the texture Liveness call, the user might be asked to do a series of gestures to confirm liveness. The user performing the gestures is arbitrarily matched with the selfie captured. If  one or more of these matches fail, a 'faceMatch' error is returned (refer to 'Error Codes' section). 
If all the gestures are succefully performed and the face matches are sucessful, a result JSON with the following key-value pairs is returned in the CaptureCompletionHandler
- `imageUri` : String. Local path of the image captured <br/>
- `live`: String with values 'yes'/'no'. Tells whether the selfie is live or not.
  Following are the errors that can occur during capture process:

## Error Codes
Descriptions of the error codes returned in the CaptureCompletionHandler are given here. 


|Description|Explanation|Action|
|-----------|-----------|------|
|2|Internal SDK Error|Occurs when an unexpected error has happened with the HyperSnapSDK.|Notify HyperVerge|
|3|Operation Cancelled By User|When the user taps on cancel button before capture|Try again.|
|4|Camera Permission Denied|Occurs when the user has denied permission to access camera.|In the settings app, give permission to access camera and try again.|
|101|Initialization Error|Occurs when SDK has not been initialized properly.|Make sure HyperSnapSDK.initialise method is called before using the capture functionality|
|102|Network Error|Occurs when the internet is either non-existant or very patchy.|Check internet and try again. If Internet is proper, contact HyperVerge|
|103|Authentication Error|Occurs when the request to the server could not be Authenticated/Authorized.|Make sure appId and appKey set in the initialization method are correct|
|104|Internal Server Error|Occurs when there is an internal error at the server.|Notify HyperVerge|
|201|Face Match Error|Occurs when one or more faces captured in the gestures flow do not match the selfie|This is equivalent to liveness fail. Take corresponding action|
|202|Face Detection Error|Occurs when a face couldn't be detected in an image by the server|Try capture again|

## Advanced

### Customizations
- Some text fields, element colors, font styles, and button icons are customizable so that the look and feel of the components inside SDK can be altered to match the look and field of the app using the SDK. **Kindly note that this step is optional**. Below is the list of items that are customizable grouped by the resource file/type where the customized value/file(s) should be placed in.
    - **strings.xml**:
      ```xml
      <string name="document_screen_title_text">Document Scanner</string>
      <string name="face_screen_title_text">Face Scanner</string>
      <string name="place_face">Place your face within circle</string>
      <string name="stay_still"> Capture Now </string>
      ```


## Contact Us
If you are interested in integrating this SDK, please do send us a mail at [contact@hyperverge.co](mailto:contact@hyperverge.co) explaining your use case. We will give you the `aws_access_key` & `aws_secret_pass` so that you can try it out. Learn more about HyperVerge [here](http://hyperverge.co/).
