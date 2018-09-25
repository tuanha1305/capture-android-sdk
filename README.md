## HyperSnapSDK Documentation

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
      implementation('co.hyperverge:hypersnapsdk:1.0.8@aar', {
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
  HVDocConfig hvDocConfig = new HVDocConfig();
  hvDocConfig.setShouldShowReviewScreen(true);
  hvDocConfig.setShouldShowInstructionPage(true);
  hvDocConfig.setShouldShowFlashIcon(true);
  hvDocConfig.setDocument(document);
  HVDocsActivity.start(context, hvDocConfig, myCaptureCompletionListener);
  ```
  where:
  - **context** is the context of the current Activity being displayed
  -  **myCaptureCompletionListener** is an object of `CaptureCompletionHandler` and has been described later
  - **hvDocConfig** This is an object of type `HVDocConfig`. Its properties can be set with the setter methods provided for them. These are the various properties provided.
    - **setDocument**: (Document) Document is an enum of type   `HVDocConfig.Document`. It specifies the document type that needs to be captured.The parameter can be initialized as follows:
    ```java
      Document document = Document.CARD;
      ```
       where `aspectRatio` is a float specifying the aspectRatio of the document
          
      -Following are the types of documents supported by the Document enum:
        - **CARD**: Aspect ratio : 0.625. Example: Vietnamese National ID, Driving License, Motor Registration Certificate
        - **PASSPORT**: Aspect ratio: 0.67. Example: Passports
        - **A4**: Aspect ratio: 1.4. Example: Bank statement, insurance receipt
        - **OTHER**: This is for aspect ratios that don't fall in the above categories. In this case, the aspect ratio should be set in the next line by calling `document.setAspectRatio(aspectRatio);`
    
      Also, Document supports the following customizations:
      - **capturePageInstructionText**: The text displayed at the top section of the Camera Preview in HVDocsActivity.  It meant to tell the user about the document type. The text can be altered by calling following method:
        ```java
        document.setCapturePageInstructionText("Make sure your document is without any glare and is fully inside");
        ```
      - **capturePageSubText**: The text displayed at the bottom end of the Camera Preview in HVDocsActivity. This is to communicate the positioning of the document to the user. The text can be altered by calling following method:
        ```java
        document.setCapturePageSubText("Front side");
    
    - **setShouldShowReviewScreen** : (Boolean) To determine if the document review page should be shown after capture page. It defaults to `false`.
    - **setShouldShowInstructionPage** : (Boolean) To determine if the instructions page should be shown before capture page. It defaults to `false`.
    - **setShouldShowFlashIcon** : (Boolean) Setting this to true will add a flash toggle button at the top right corner of the screen. It defaults to `false`.
    -  `setCaptureScreenTitleText`, `setReviewScreenTitleText`, `setReviewScreenInstructionText` are properties which will allow to modify the strings shown in document capture activity and review screen activity.              

#### For Face Capture
For capturing face image, following method should be called:
  ```java
  HVFaceConfig hvFaceConfig = new HVFaceConfig();
  hvFaceConfig.setLivenessMode(LivenessMode.MODE);
  hvFaceConfig.setFaceCaptureTitleText(" Face capture  ");
  HVFaceActivity.start(context, hvFaceConfig, myCaptureCompletionListener);
  ```
  where:
  - **context** is the context of the current Activity being displayed
  - **myCaptureCompletionListener** is an object of `CaptureCompletionHandler` and has been described later.
  - **hvFaceConfig** This is an object of type `HVFaceConfig`. Its properties can be set with the setter methods provided for them. These are the various properties provided:.
    - **setLivenessMode** Explained here.
    - **setClientID**: (String) This is an optional parameter that could be sent with the liveness call.
    - **setShouldShowInstructionPage**: (Boolean) To determine if the instructions page should be shown before capture page. It defaults to `false`.
    - `setFaceCaptureTitleText` allows to modify the title text that is shown in HVFaceActivity.
    
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
The SDK has two liveness detection methods. Texture liveness and Gesture Liveness. This can be set using the `livenessMode` parameter in the `setLivenessMode` method of `HVFaceConfig` discussed earlier.

Here, `livenessMode` is of type `HVFaceConfig.LivenessMode`, an enum with 3 values:

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
- `live`: String with values 'yes'/'no'. Tells whether the selfie is live or not. This mode is currently in beta. It is highly recommended to not use it in production.

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
      - **Face capture strings**:
          ```xml
           
          <string name="faceCaptureFaceNotFound">Place your face within circle</string>
          <string name="faceCaptureFaceFound"> Capture Now </string>
          <string name="faceCaptureMoveAway"> Move away from the phone</string>
          <string name="faceCaptureActivity">Processing</string>
          ```
      - **Face capture instruction strings**:
          ```xml
              <string name="faceInstructionsTitle">Selfie Tips</string>
              <string name="faceInstructionsTop1">Good lighting on your face</string>
              <string name="faceInstructionsTop2">Hold phone in front of you</string>
              <string name="faceInstructionsNoGlasses">No Glasses</string>
              <string name="faceInstructionsBrightLight">Bright Light</string>
              <string name="faceInstructionsNoHat">No Hat</string>
              <string name="faceInstructionsProceed">Proceed to Take Selfie</string>

          ```
          
      - **Document capture instruction strings**:
          ```xml
             <string name="docInstructionsProceed">Proceed to Capture ID</string
             <string name="docInstructionsTitle">ID Capture Tips</string>
             <string name="docInstructions1">Place it within the box</string>
             <string name="docInstructions2">Do not place outside</string>
             <string name="docInstructions3">Avoid glare</string>
        ```
      
       - **Document capture review page strings**:
           ```xml
              <string name="docReviewRetakeButton">Retake</string>
              <string name="docReviewContinueButton">Use This Photo</string>
              <string name="docReviewTitle">Review Your Photo</string>
              <string name="docReviewDescription">Is your document fully visible, glare free and not blurred ?</string>
            ```                  
                
                    
          
   - **colors.xml**:
        - **Customisable colors in the SDK**:
            ```xml
              <color name="camera_button_color">#273646</color>
              <color name="review_button_color">#2c3e50</color>
              <color name="review_button_border_color">#015aff</color>
              <color name="title_text_color">#737373</color>
              <color name="content_text_color">#4a4a4a</color>
              <color name="shadow_color">#80000000</color>
            ```
   
   - In order to customise font, border color, text size, text color different styles are used within the TextViews. The style names can be used in the parent app to change the properties.   
   - Calligraphy library is used to load the fonts from assets folder.
   - **styles.xml**:      
        - **The following style is used by all the title texts in the SDK.**
            ```xml
                <style name="TextViewHeader">
                    <item name="android:textColor">@color/title_text_color</item>
                    <item name="android:textSize">18.7sp</item>
                    <item name="fontPath">Roboto-Medium.ttf</item>
                
                </style>

            ```
        - **The following style is used by all the content description texts in the SDK.**
             ```xml
                 <style name="TextViewContent">
                   <item name="android:textColor">@color/content_text_color</item>
                   <item name="android:textSize">16sp</item>
                   <item name="fontPath">Roboto_regular.ttf</item>
                </style>
            
            ```
        - **The following style is used by the sub text that is displayed in the document capture screen.**
           ```xml
                 <style name="TextviewShadow">
                     <item name="android:textSize">16sp</item>
                     <item name="fontPath">Roboto-Medium.ttf</item>
                     <item name="android:textColor">@color/white </item>
                     <item name="android:shadowColor">@color/shadow_color</item>
                     <item name="android:shadowDy">2</item>
                     <item name="android:shadowRadius">4</item>
                 </style>   
                    
             ```
        - **The following style is used by the sub text that is displayed in the document capture screen.**
           ```xml
                 <style name="TextviewShadow">
                     <item name="android:textSize">16sp</item>
                     <item name="fontPath">Roboto-Medium.ttf</item>
                     <item name="android:textColor">@color/white </item>
                     <item name="android:shadowColor">@color/shadow_color</item>
                     <item name="android:shadowDy">2</item>
                     <item name="android:shadowRadius">4</item>
                 </style>   
                    
             ```
        - **The following style is used by Confirm button in Document Capture Review screen, Proceed buttons in Face and document capture instruction screen.**
           ```xml
             <style name="ButtonRoundedRectangle">
                <item name="android:textSize">13.3sp</item>
                <item name="android:textColor">@color/white</item>
                <item name="android:background">@drawable/drawable_rounded_button_solid</item>
                <item name="fontPath">Roboto-Medium.ttf</item>
            </style>  
                    
             ```
        - **The following style is used by Retake button in Document Capture Review screen.**
           ```xml
                <style name="ButtonRoundedRectangleBorder">
                   <item name="android:textSize">13.3sp</item>
                   <item name="android:textColor">@color/review_button_color</item>
                   <item name="android:background">@drawable/drawable_rounded_button_border</item>
                   <item name="fontPath">Roboto-Medium.ttf</item>
               </style>
                            
            ```
       
             
             

## Contact Us
If you are interested in integrating this SDK, please do send us a mail at [contact@hyperverge.co](mailto:contact@hyperverge.co) explaining your use case. We will give you the `aws_access_key` & `aws_secret_pass` so that you can try it out. Learn more about HyperVerge [here](http://hyperverge.co/).
