package org.opencv.samples.colorblobdetect;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class DetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Detector mDetector;
    private CameraBridgeViewBase mOpenCvCameraView;
    int count = 0;
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(DetectionActivity.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };
    public DetectionActivity() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.detection_surface_view);
        mOpenCvCameraView = findViewById(R.id.detection_activity_surface_view);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setMaxFrameSize(800,600); //800x600 for nokia 5.1
        mOpenCvCameraView.setCvCameraViewListener(this);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e("INIT", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);//(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.e("INIT", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mDetector = new Detector();
        //Toast.makeText(getApplicationContext(), "Detection running...", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "Tap screen to detect message", Toast.LENGTH_LONG).show();
        mIsColorSelected = true;
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (!mIsColorSelected){
            Toast.makeText(getApplicationContext(), "Detection running", Toast.LENGTH_SHORT).show();
            mIsColorSelected = true;
        }
        else {
            Toast.makeText(getApplicationContext(), "Detection paused", Toast.LENGTH_SHORT).show();
            mIsColorSelected = false;
        }
        return false;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        TextView txt = findViewById(R.id.textView2);
        StringBuilder sb = new StringBuilder();
        StringBuilder bb = new StringBuilder();
        StringBuilder data = new StringBuilder();
        StringBuilder xrt = new StringBuilder();
        StringBuilder tooManyBuilders = new StringBuilder();


        if (mIsColorSelected) {
            Log.e("msg", "------------------- Frame captured -------------------");
            mDetector.process(mRgba);
            int max = mDetector.detected.length;
            for(int x = 0; x < max; x++) {
                int detections = mDetector.detected[x];
                if (detections == 1) {
                    sb.append("1");
                }
                else {
                    sb.append("0");
                }
                if (sb.length() == max) {
                    for (int j = 0; j < sb.length(); j++){
                        char str = sb.charAt(j);
                        if ((j - 1) > 0) {
                            char str1 = sb.charAt(j - 1);
                            if (str1 == '1' && str == '0'){
                                bb.append(" ");
                            }
                            if (str1 == '0' && str == '1'){
                                bb.append(" ");
                            }
                        }
                        if (str == '0'){
                            bb.append("0");
                        }
                        if (str == '1'){
                            bb.append("1");
                        }
                    }
                    String[] dat = bb.toString().split(" ");
                    for (String temp : dat) {
                        if (temp.contains("1")) {
                            data.append("1");
                            if (temp.length() >= 10) {
                                data.append("1");
                                if (temp.length() >= 20) {
                                    data.append("11");
                                }
                            }
                        }
                        if (temp.contains("0")) {
                            data.append("0");
                            if (temp.length() >= 7) {
                                data.append("0");
                                if (temp.length() > 17) {
                                    data.append("0");
                                    if (temp.length() > 19) {
                                        data.append("0");
                                    }
                                }
                            }
                        }
                    }
                    String[] testing = data.toString().split("011110");
                    for (String s : testing) {
                        if (s.length() == 28) {
                            String[] new_f = s.replaceAll("..(?!$)", "$0 ").split(" ");
                            // decoding Manchester code
                            for (String temp : new_f) {
                                if (temp.equals("10")) {
                                    xrt.append("1");
                                    continue;
                                }
                                if (temp.equals("01")) {
                                    xrt.append("0");
                                    continue;
                                }
                                if (temp.equals("00")) {
                                    xrt.append("-");
                                    break;
                                }
                                if (temp.equals("11")) {
                                    xrt.append("-");
                                    break;
                                }
                            }
                        }
                    }
                    if (xrt.length() >= 14 && !xrt.toString().contains("-") ) {
                        int decimalValue = Integer.parseInt(xrt.substring(0,7), 2);
                        int charC = Integer.parseInt(xrt.substring(7, 14), 2);
                        tooManyBuilders.append(decimalValue).append(" ").append((char) charC).append("  ");
                    }
                    if (xrt.length() >= 28 && !xrt.toString().contains("-") ) {
                        int decimalValue1 = Integer.parseInt(xrt.substring(14,21), 2);
                        int charC = Integer.parseInt(xrt.substring(21, 28), 2);
                        tooManyBuilders.append(decimalValue1).append(" ").append((char) charC).append("  ");
                    }
                    if (xrt.length() >= 42 && !xrt.toString().contains("-") ) {
                        int decimalValue2 = Integer.parseInt(xrt.substring(28,35), 2);
                        int charC = Integer.parseInt(xrt.substring(35, 42), 2);
                        tooManyBuilders.append(decimalValue2).append(" ").append((char) charC).append("  ");
                    }

                    addText(txt, tooManyBuilders.toString());
                }
            }
        }
        return mRgba;
    }

    private void addText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override // TODO: fix (contains) because of possible future errors.. WIP for now..
            // TODO:  'toggle' now waits for signal start (slows msg received down a bit)
            public void run() {
                String txt = text.getText().toString();
                ProgressBar progress = findViewById(R.id.progressBar2);
                StringBuilder url_string = new StringBuilder();
                ObjectAnimator animation = ObjectAnimator.ofInt(progress, "progress",count);
                progress.setMax(103);  // #TODO: implement method for recording the end-of-signal, instead of a fixed value.
                String[] values = value.split("  ");
                String[] textview = txt.split("  ");
                int match = 0;
                for (int i = 0; i < values.length; i++) {
                    for (int y = 0; y < textview.length; y++){
                        if (values[i].equals(textview[y])){
                            match = 1;
                            break;
                        }
                    }
                    if (match != 1 && values[i].length() >= 2){
                        Log.e("test", "adding value " + values[i]);
                        text.append(values[i]);
                        text.append("  ");
                        match = 0;
                        count++;
                        progress.setProgress(count);
                    }
                    animation.setDuration(200);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }
                if (textview.length >= 103) {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i <= 103; i++){
                        list.add(i,"  ");
                    }
                    for (int i = 0; i <= 102 ; i++){
                        if (Integer.parseInt(textview[i].split(" ")[0]) < 103){
                            Log.e("test", "attempting to remove index: "+ Integer.parseInt(textview[i].split(" ")[0]));
                            list.remove(Integer.parseInt(textview[i].split(" ")[0]));
                            list.add(Integer.parseInt(textview[i].split(" ")[0]), Arrays.toString(textview[i].split(" ")));
                            Log.e("test", "looped list contains: " + list.toString());
                        }
                    }
                    url_string.append("https://");
                    for (int j = 0; j < list.size() - 1; j++){
                        url_string.append(list.get(j).split(" ")[1].replace("[", "").replace("]", ""));
                        Log.e("test", "list to url: " + url_string.toString());
                    }
                    text.setText(url_string);
                    progress.setVisibility(View.GONE);
                    startOOB(null);
                }
            }
        });
    }

    public void startOOB(View view) {
        Intent intent = new Intent(this, oob.class);
        TextView txt = findViewById(R.id.textView2);
        String message = txt.getText().toString();
        intent.putExtra("OOB", message);
        Log.e("Message", String.format("Starting OOB with: " + message));
        count = 0;
        startActivity(intent);
        finish();
    }
}
