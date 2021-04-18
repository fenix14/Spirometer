package com.fenix.spirometer.ui.home;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VideoFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener {
    VideoView videoView;
    private CustomToolbar toolbar;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(false);
        toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(null);
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        if (btmNav != null) {
            btmNav.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_video;
    }

    @Override
    protected void initView(View rootView) {
        videoView = rootView.findViewById(R.id.videoView);

        String videoPath = Constants.VIDEO_PATH + "demo.mp4";
        copyVideoToSdcard(videoPath);
        videoView.setVideoPath(videoPath);
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
    }

    private void copyVideoToSdcard(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                file.createNewFile();
                inputStream = getResources().getAssets().open("mp4/demo.mp4");
                fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("hff", "fileName = " + file.getPath() + ", exist = " + file.exists());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void initData() {
        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
    }
}
