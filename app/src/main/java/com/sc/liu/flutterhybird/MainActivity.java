package com.sc.liu.flutterhybird;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout nativeLayout;
    private LinearLayout flutterContainer;
    private TextView nativeResult;
    private Button nativeSend;
    private EditText nativeEdit;
    private FlutterView mFlutterView;
    private MethodChannel mMethodChannel;
    public static final String CHANNEL_NAME = "nscode.flutter.io/message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initChannel();
    }

    private void initChannel() {
        mMethodChannel = new MethodChannel(mFlutterView,CHANNEL_NAME);
        mMethodChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                if(methodCall.method.equals("setText")){
                    nativeResult.setText((String)methodCall.arguments);
                    result.success("Flutter call successfully");
                }else if(methodCall.method.equals("releaseFocus")){
                    releaseFocus();
                }
            }
        });
    }

    private void initView() {
        nativeLayout = getView(R.id.layout_native);
        flutterContainer = getView(R.id.layout_flutter);
        nativeResult = getView(R.id.native_result);
        nativeSend = getView(R.id.navtive_send);
        nativeEdit = getView(R.id.navtive_edit);
        mFlutterView = Flutter.createView(this, getLifecycle(), "main");
        flutterContainer.addView(mFlutterView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        nativeSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFlutter();
            }
        });
        nativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nativeEdit.setFocusable(true);
                nativeEdit.setFocusableInTouchMode(true);
                nativeEdit.requestFocus();

            }
        });
    }

    private <T extends View> T getView(@IdRes int id) {
        return (T) findViewById(id);
    }

    private void sendToFlutter(){
        if(!TextUtils.isEmpty(nativeEdit.getText())) {
            mMethodChannel.invokeMethod("setText", nativeEdit.getText().toString(), new MethodChannel.Result() {
                @Override
                public void success(Object o) {
                    Log.d("FlutterHybird","result is " + o.toString());
                }

                @Override
                public void error(String s, String s1, Object o) {

                }

                @Override
                public void notImplemented() {

                }
            });
        }
    }

    private void releaseFocus(){
        Log.d("FlutterHybird","trigger release focus");
        nativeEdit.clearFocus();
        nativeEdit.setFocusable(false);
    }
}
