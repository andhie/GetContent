package ndrlabs.dropboxcontent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1069;

    private ImageView mImage;
    private Button mPickImage;
    private TextView mLogView;

    private SpannableStringBuilder sb = new SpannableStringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            writeLog("Request Code Identified = PICK_IMAGE_REQUEST", Log.DEBUG);
            switch (resultCode) {
                case RESULT_OK:
                    writeLog("Result code = RESULT_OK", Log.INFO);
                    writeLog("Intent#getDataString = " + data.getDataString(), Log.INFO);
                    writeLog("Intent#toString = " + data.toString(), Log.DEBUG);
                    Glide.with(this).load(data.getData()).listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();
                            writeLog("Glide onException = " + e.getMessage(), Log.ERROR);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            writeLog("Glide onResourceReady: isFromMemoryCache = " + isFromMemoryCache + ", isFirstResource = " + isFirstResource, Log.DEBUG);
                            return false;
                        }
                    }).into(mImage);
                    break;
                case RESULT_CANCELED:
                    writeLog("Result code = RESULT_CANCELED", Log.DEBUG);
                    break;
                case RESULT_FIRST_USER:
                    writeLog("Result code = RESULT_FIRST_USER", Log.DEBUG);
                    break;
            }
        } else {
            writeLog("Request Code Unknown = " + requestCode, Log.ERROR);
        }
    }

    private void pickContent() {
        writeLog("Init Pick Content", Log.DEBUG);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void findViews() {
        mImage = (ImageView) findViewById(R.id.image);
        mPickImage = (Button) findViewById(R.id.pick_image);
        mLogView = (TextView) findViewById(R.id.log_view);

        mPickImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mPickImage) {
            pickContent();
        }
    }

    private void writeLog(String str, int logType) {
        int color;
        switch (logType) {
            case Log.ERROR:
                color = 0xFFFF5252;
                break;

            case Log.INFO:
                color = 0xFF4CAF50;
                break;

            case Log.DEBUG:
            default:
                color = 0xFF2196F3;
                break;
        }
        int start = sb.length();
        sb.append("###").append(str).append("\n")
                .setSpan(new ForegroundColorSpan(color), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mLogView.setText(sb);
    }
}
