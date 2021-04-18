package com.fenix.spirometer.ui.home;

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
        if (!new File(videoPath).exists()) {
            videoPath = "file:///android_assets/demo.mp4";
        }
        if (new File(videoPath).exists()) {
            videoView.setVideoPath(videoPath);
            MediaController mediaController = new MediaController(getContext());
            videoView.setMediaController(mediaController);
            videoView.requestFocus();
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
