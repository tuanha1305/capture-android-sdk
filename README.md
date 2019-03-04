
## HyperSnapSDK Documentation for Android

## Overview

HyperSnapSDK is HyperVerge's documents + face capture SDK that captures images at a resolution appropriate for our proprietary Deep Learning OCR and Face Recognition Engines.

The framework provides a liveness feature that uses our advanced AI Engines to differentiate between a real user capturing his/her selfie from a photo/video recording.

### Specifications
- Gradle Version 4.1 (Recommended)
- Tested with Gradle Plugin for Android Studio - version 3.1.0
- minSdkVersion 19
- targetSdkVersion 26

### ChangeLog
You can find the ChangeLog in the [CHANGELOG.md](CHANGELOG.md) file

## Table Of Contents
- [Overview](#overview)
	- [Specifications](#specifications)
    - [ChangeLog](#changelog)
- [Table Of Contents](#table-of-contents)
- [Example Project](#example-project)
- [Integration Steps](#integration-steps)
	- [1. Adding the SDK to your project](#1-adding-the-sdk-to-your-project)
	- [2. Update Proguard File](#2-update-proguard-file)
	- [3. Initializing the SDK](#3-initializing-the-sdk)
	- [4. Launching the Activities](#4-launching-the-activities)
		- [For Document Capture](#for-document-capture)
		- [For Face Capture](#for-face-capture)
			- [Liveness in Face Capture](#liveness-in-face-capture)
- [API Calls](#api-calls)		
     - [OCR API Call](#ocr-api-call)
     - [Face Match Call](#face-match-call)
     - [APICompletionCallback](#apicompletioncallback)
- [HVError](#hverror)
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
          ndk {  
			    abiFilters "armeabi-v7a", "x86"  
			}
	  }
  }
  dependencies {
      implementation('co.hyperverge:hypersnapsdk:2.4.3@aar', {
          transitive=true
          exclude group: 'com.android.support'
          exclude group: 'com.google.android.gms'
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

### 2. Update Proguard File
  If you have a proguard file, add the following:
  ```
  -keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
         private javax.net.ssl.SSLSocketFactory delegate;
    }
```

### 3. Initializing the SDK
For initializing the SDK, add the following line to your app before calling any of the SDK functionalities. For example,  you can add it to the `onCreate` method of the application class.
  ```java
  HyperSnapSDK.init(context, APP_ID, APP_KEY, region);
  ```
Where,
	- appId, appKey are given by HyperVerge
	- region: This is an enum, `HyperSnapParams.Region` with three values - `AsiaPacific`, `India` and `UnitedStates`.

Please note: The SDK requires **Camera** and **Autofocus** permissions to capture images.
    Camera permissions will be handled by the SDK if not already handled by the app.

### 4. Launching the Activities

Both Document Capture and Face Capture happen via Activities provided in the SDK. The activitiess are called 'HVDocsActivity' and 'HVFaceActivity' respectively. To add them to your app:
    1. Set config parameters
    2. Create a completion callback
    3. Call the `start` method of the corresponding Activity

#### For Document Capture

  ```java
  //1. Set properties
  HVDocConfig hvDocConfig = new HVDocConfig();
  hvDocConfig.setDocumentType(HyperSnapParams.DocumentType.card)
  hvDocConfig.setShouldShowReviewScreen(true);
  hvDocConfig.setDocCaptureTitle("ID Card")
  //2. Create Completion Callback
  DocCaptureCompletionHandler completionCallback = new DocCaptureCompletionHandler() {
  @Override
  public void onResult(HVError error, JSONObject result) {
        if (error != null) {
            //Handler Error
        }
        if (result != null) {
            //Handler Results
        }
    }
  };
  //3. Call start method
  HVDocsActivity.start(context, hvDocConfig, completionCallback);
  ```
  where:
  - **context**: is the context of the current Activity being displayed.
  -  **completionCallback**: is an object of type `DocCaptureCompletionHandler`. It is an interface that has one `onResult` method that will be called incase fo success and failure. The `onResult` method has 2 parameters.
	  - **error** : Object of type `HVError`. Explained [here](#hverror) . If the process was successful, this object would be null.
	  - **result** : JSONObject. It has the `imageUri` of the captured image.
  - **hvDocConfig**: This is an object of type `HVDocConfig`. Its properties can be set with the setter methods provided for them. These are the various properties provided:
    - **setDocumentType**: (Document) Document is an enum of type   `HVDocConfig.Document`. It specifies the document type that needs to be captured. Its default value is `CARD`. The parameter can be initialized as follows:
        ```java
        Document document = Document.CARD;
        ```
      - Following are the types of documents supported by the Document enum:

          - **CARD**: Aspect ratio: 0.625. Example: Vietnamese National ID, Driving License, Motor Registration Certificate.
          - **PASSPORT**: Aspect ratio: 0.67. Example: Passports
          - **A4**: Aspect ratio: 1.4. Example: Bank statement, insurance receipt.
          - **OTHER**: This is for aspect ratios that don't fall in the above categories. In this case, the aspect ratio should be set in the next line by calling `document.setAspectRatio(aspectRatio);`
    	    where `aspectRatio` is a float specifying the aspectRatio of the document.

    - **setShouldShowReviewScreen**: (Boolean) To determine if the document review page should be shown after capture page. It defaults to `false`.
    - **setShouldShowInstructionPage**: (Boolean) To determine if the instructions page should be shown before capture page. It defaults to `false`.
    - **setShouldShowFlashIcon**: (Boolean) Setting this to true will add a flash toggle button at the top right corner of the screen. It defaults to `false`.
    - **setShouldAddPadding**: (Boolean) Setting this to true will enable extra padding that will be added to all images captured using the Document Capture activity. It defaults to `true`.
    -  **setDocCaptureTitle**: (String) To set the title text that is shown in the document capture page.
    - **setDocReviewTitle**: (String) To set the title text that is shown in the Review screen after a document has been captured.
    - **setDocReviewDescription**: (String) To set the instruction text that is shown in the Review screen after a document has been captured.   
     - **setDocCaptureDescription**: (String) The text displayed at the top section of the Camera Preview in HVDocsActivity. This is to communicate the positioning of the document to the user.

    - **setDocCaptureSubText**: (String) The text displayed at the bottom end of the Camera Preview in HVDocsActivity. It is meant to tell the user about the document type.


#### For Face Capture

```java
//1. Set properties
  HVFaceConfig hvFaceConfig = new HVFaceConfig();
  hvFaceConfig.setLivenessMode(LivenessMode.MODE);
  hvFaceConfig.setFaceCaptureTitle("Face capture");
  //2. Create Completion Callback
  FaceCaptureCompletionHandler completionCallback = new FaceCaptureCompletionHandler() {
  @Override
	  public void onResult(HVError error, JSONObject result, JSONObject headers) {
		  if (error != null) {
			  //Handler Error
		  }
		  if (result != null) {
			  //Handler Results
		  }
	  }
  };
//3. Call start method
  HVFaceActivity.start(context, hvFaceConfig, completionCallback);
  ```
  where:
  - **context**: is the context of the current Activity being displayed
  - **completionCallback**: is an object of type `FaceCaptureCompletionHandler`. It is an interface that has one `onResult` method that will be called incase fo success and failure. The `onResult` method has 3 parameters.
	  - **error** : Object of type `HVError`. Explained [here](#hverror). If the process was successful, this object would be null.
	  - **result** : JSONObject. It has the `imageUri` of the captured image. If the liveness call was successfully performed, it will have the entire results of the liveness call.
	  - **headers** : JSONObject. If the liveness call has reached the HyperVerge servers, this object would have the headers returned by the server. Otherwise, it will be null.
  - **hvFaceConfig**: This is an object of type `HVFaceConfig`. Its properties can be set with the setter methods provided for them. These are the various properties provided:
    - **setLivenessMode**: (HVFaceConfig.LivenessMode) Explained [later](#Liveness-in-Face-Capture).
    - **setShouldShowInstructionPage**: (Boolean) To determine if the instructions page should be shown before capture page. It defaults to `false`.
    - **setFaceCaptureTitle**: (String) It allows to modify the title text that is shown in HVFaceActivity.
    - **setShouldEnableDataLogging**: (Boolean) This will allow Hyperverge to log the data to facilitate easy debugging and analysis. It defaults to `false`.


#### Liveness in Face Capture
The `HVFaceConfig.LivenessMode` enum takes two values.

**.none**: No liveness test is performed. The selfie that is captured is simply returned. If successful, the result JSON in the CaptureCompletionHandler has one key-value pair.
- `imageUri`: local path of the image captured.

**.textureLiveness**: Texture liveness test is performed on the selfie captured.  If successful, a result JSON with the following key-value pairs is returned in the CaptureCompletionHandler.

- `imageUri`: String. Local path of the image captured.
- `result`: JSONObject. This is the result obtained from the liveness API call. It has 3 key-value pairs.
    - `live`: String with values 'yes'/'no'. Tells whether the selfie is live or not.
    - `liveness-score`: Float with values between 0 and 1.The  confidence score for the liveness prediction.
    - `to-be-reviewed`: String with values 'yes'/'no'. Yes indicates that it flagged for manual review.

## API Calls

### OCR API Call
 To make OCR API calls directly from the App, use the following method:
 ```java
    HVNetworkHelper.makeOCRCall(endpoint, documentUri, parameters, headers, completionCallback)
 ```
 where:

   - **parameters**: (JSONObject) This is usually an empty JSONObject. It could have the following optional field.
	  - *dataLogging*: (String - "yes"/"no")  If you want HyperVerge to temporarily store the image during POC or for debugging purposes, please set this to "yes".
   - **headers**: (JSONObject) This is usually an empty JSONObject. It could have the following optional field.
	 - *referenceId*: (String) This is a unique identifier that is assigned to the end customer by the API user. It would be the same for the different API calls made for the same customer. This would facilitate better analysis of user flow etc.
   - **documentUri**: (String) The `imageUri` received in the completionHandler after Document Capture.
   - **completionCallback**: (APICompletionCallback) This is an interface which is used to return the results back after making the network request. Explained [here](#apicompletioncallback).     
   - **endpoint**: (String)
        - For India KYC, these are the supported endpoints
	        - https://ind.docs.hyperverge.co/v1-1/readKYC
			- https://ind.docs.hyperverge.co/v1-1/readPAN
			- https://ind.docs.hyperverge.co/v1-1/readAadhaar
			- https://ind.docs.hyperverge.co/v1-1/readPassport
	        -  Please find the full documentation [here](https://github.com/hyperverge/kyc-india-rest-api)

	  - For Vietnam KYC, these are the supported endpoints
		- https://apac.docs.hyperverge.co/v1.1/readNID
		- https://apac.docs.hyperverge.co/v1.1/readMRC
		- https://apac.docs.hyperverge.co/v1.1/readDL
        - Please find the full documentation [here](https://github.com/hyperverge/kyc-vietnam-rest-api)

### Face Match Call
 To make Face ID match call directly from the App, use the following method:
  ```java
     HVNetworkHelper.makeFaceMatchCall(endPoint, faceUri, documentUri, parameters, headers, completionCallback)
  ```

  where:
   * **endpoint**: (String)
	   - India: https://ind.faceid.hyperverge.co/v1/photo/verifyPair
      - Asia Pacific: https://apac.faceid.hyperverge.co/v1/photo/verifyPair

For more information, please check out the documentation [here](https://github.com/hyperverge/face-match-rest-api)
- **parameters**: (JSONObject)
	- *type*: (required)For document to selfie match, please set "type" to "id". For selfie to selfie match, set "type" to "selfie".
	- *dataLogging*: (optional)If you want HyperVerge to store your image for debugging purpose, set "dataLogging" to "yes".
-  **headers**: (JSONObject) This is usually an empty JSONObject. I could have the following optional field.
	- *referenceId*: (String) This is a unique identifier that is assigned to the end customer by the API user. It would be the same for the different API calls made for the same customer. This would facilitate better analysis of user flow etc.
- **faceUri**: (String) The `imageUri` received in the CompletionHandler after Face Capture.
- **documentUri**: (String) The `imageUri` received in the completionHandler after Document Capture.
- **completionCallback**: (APICompletionCallback) This is an interface which is used to return the results back after making the network request. Explained [here](#apicompletioncallback).     


#### APICompletionCallback
APICompletionCallback is an interface whose object needs to be passed with `makeOCRCall` or `makeFaceMatchCall`. It has one `onResult` method that contains the error or result obtained in the process.

Following is a sample implementation of APICompletionCallback:
  ```java
  APICompletionCallback completionCallback = new APICompletionCallback() {
    @Override
    public void onResult(HVError error, JSONObject result, JSONObject headers) {
        if(error != null) {
            //Handler Error
        }
        else{
           //Handle Result
        }
    }
  }
  ```  
  Where,

 - **result**: The result JSONObject is the entire result obtained by the API without any modification.

 - **error**: It is a HVError Object. Explained in the next section.
 - **headers** : JSONObject. It is the headers returned by the HyperVerge servers.


## HVError
HVError is the error object returned in case of failure in Face Capture, Document Capture, makeOCRCall or makeFaceMatchCall.

It has two variables : `errorCode` and `errorMessage`. These can be accessed with the following method:
```
int code = hvError.getErrorCode();
String message = hvError.getErrorMessage();
```

Here are the various error codes returned by the SDK.

|Code|Description|Explanation|Action|
|-----|------|-----------|------|
|2|Internal SDK Error|Occurs when an unexpected error has happened with the HyperSnapSDK.|Notify HyperVerge|
|3|Operation Cancelled By User|When the user taps on cancel button before capture|Try again.|
|4|Camera Permission Denied|Occurs when the user has denied permission to access camera.|In the settings app, give permission to access camera and try again.|
|5|Hardware Error|Occurs when camera could not be initialized|Try again|
|6|Input Error|Occurs when the input needed by the start method/HVNetworkHelper method is not correct|Read error message for more details|
|11|Initialization Error|Occurs when SDK has not been initialized properly|Make sure HyperSnapSDK.initialise method is called before using the capture functionality|
|12|Network Error|Occurs when the internet is either non-existant or very patchy|Check internet and try again. If Internet is proper, contact HyperVerge|
|14|Internal Server Error|Occurs when there is an internal error at the server|Notify HyperVerge|
|22|Face Detection Error|Occurs when a face couldn't be detected in an image by the server|Try capture again|
|23|Face not clear Error|Occurs when face captured is blurry|Hold the phone stead while capture|

Apart from the above errors, if the server returns a non 200 status code for any of the API calls (Liveness, Face Match and OCR), the status code is as it is returned as the error code and the corresponding message is returned as the error message.

These are the possible error codes that could be received from the server:

|Code|Description|Explanation|Action|
|-----|------|-----------|------|
|400|Invalid Request|Something is wrong with the input|Check the error message/API documentation|
|401|Authentication Error|Credentials provided in the initialization step are wrong|Check credentails. If they seem correct, contact HyperVerge|
|404|Endpoint not found|Endpoint sent to makeFaceMatchCall or makeOCRCall is not correct|Check API documentation|
|423|No supported KYC document found|Happens only in makeOCRCall|Capture again with the correct document and ensure image quality|
|501, 502|Server Error|Internal server error has occured|Notify HyperVerge|
|503|Server Busy|Server Busy|Notify HyperVerge|

## Advanced

### Customizations
- Some text fields, element colors, font styles, and button icons are customizable so that the look and feel of the components inside SDK can be altered to match the look and feel of the app using the SDK. **Kindly note that this step is optional**. Below is the list of items that are customizable grouped by the resource file/type where the customized value/file(s) should be placed in.
    - **strings.xml**:
      - **Face capture strings**:
          ```xml
             <string name="faceCaptureTitle">Selfie Capture</string>
             <string name="faceCaptureFaceNotFound">Place your face within circle</string>
             <string name="faceCaptureFaceFound"> Capture Now </string>
             <string name="faceCaptureMoveAway"> Move away from the phone</string>
             <string name="faceCaptureActivity">Processing</string>
          ```
      - **Document capture strings**:
        ```xml
	    <string name="docCaptureTitle">Docs capture</string>
	    <string name="docCaptureDescription">Make sure your document is without any glare and is fully inside</string>
	    <string name="docCaptureSubText">Front Side</string>
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
              <string name="docReviewDescription">Is your document fully visible, glare free and not blurred?</string>
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
   - [Calligraphy](https://github.com/chrisjenx/Calligraphy) library is used to load the fonts from assets folder.
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
### EXIF Data
Both Face and Document images returned by the SDK have EXIF data stored in them.
If the app has permissions to access location, the EXIF data would also contain the 'GPS' data of the image.
Please note that, if GPS data is needed, location permissions should be handled by the app before launching the SDK.

To get the EXIF data, use the following code on the `imageUri` returned by the SDK
 ```
	  ExifInterface outFile = new ExifInterface(imageUri);
      String gpsLongitude = outFile.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
	  String gpsLatitude  = outFile.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
      String make = outFile.getAttribute(ExifInterface.TAG_MAKE);
      String model = outFile.getAttribute(ExifInterface.TAG_MODEL);
	  String flash = outFile.getAttribute(ExifInterface.TAG_FLASH);
	  String focalLength = outFile.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);

 ```           

## Contact Us
If you are interested in integrating this SDK, please contact us at [contact@hyperverge.co](mailto:contact@hyperverge.co). Learn more about HyperVerge [here](http://hyperverge.co/).
