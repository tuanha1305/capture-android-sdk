Starting with HyperSnap SDK version 3.1.0, we are moving away from chrisjenx library and have replaced it with Android's Downloadable Font Library. Following are the steps to customise fonts going further -


### 1. Customising fonts in document capture screen

- **Set title TextView font**
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setTitleTypeFace(R.font.roboto_regular);
```

- **Set description TextView font**
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setDescTypeFace(R.font.roboto_regular);
```

- **Set hint TextView font**
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setHintTypeface(R.font.roboto_regular);
```

### 2. Customising fonts in document image review screen 

- Set review screen title TextView font
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setReviewScreenTitleTypeface(R.font.roboto_regular);
```

- **Set review screen description TextView font**
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setReviewScreenDescTypeface(R.font.roboto_regular);
```

- **Set review screen retake image Button font**
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setReviewScreenRetakeButtonTypeface(R.font.roboto_regular);
```

- **Set review screen confirm Button font**
```
HVDocConfig hvDocConfig = new HVDocConfig();
hvDocConfig.setReviewScreenConfirmButtonTypeface(R.font.roboto_regular);
```
### 3. Customising fonts in document instructions screen
```
HVDocConfig hvDocConfig = new HVDocConfig();
JSONObject stringObj = new JSONObject();

stringObj.put("docInstructionsTitleTypeFace", R.font.roboto_regular);
stringObj.put("docInstructions1TypeFace", R.font.roboto_regular);
stringObj.put("docInstructions2TypeFace", R.font.roboto_regular);
stringObj.put("docInstructions3TypeFace", R.font.roboto_regular);
stringObj.put("docInstructionsProceedTypeFace", R.font.roboto_regular);

hvDocConfig.setCustomUIStrings(R.font.roboto_regular);
```

### 4. Customising fonts in face capture screen

- **Set face capture screen title TextView font**
```
HVFaceConfig hvFaceConfig = new HVFaceConfig();
hvFaceConfig.setTitleTypeface(R.font.roboto_regular);
```

- **Set face capture screen status TextView font**
```
HVFaceConfig hvFaceConfig = new HVFaceConfig();
hvFaceConfig.setStatusTypeFace(R.font.roboto_regular);
```

### 5. Customising fonts in face instructions screen
```
HVFaceConfig hvFaceConfig = new HVFaceConfig();
JSONObject stringObj = new JSONObject();

stringObj.put("faceInstructionsTitleTypeFace", R.font.bad_script);
stringObj.put("faceInstructionsTop1TypeFace", R.font.bad_script);
stringObj.put("faceInstructionsTop2TypeFace", R.font.bad_script);
stringObj.put("faceInstructionsBrightLightTypeFace", R.font.bad_script);
stringObj.put("faceInstructionsNoHatTypeFace", R.font.bad_script);
stringObj.put("faceInstructionsNoGlassesTypeFace", R.font.bad_script);
stringObj.put("faceInstructionsProceedBackCamTypeFace", R.font.bad_script);

config.setCustomUIStrings(stringObj);
```
