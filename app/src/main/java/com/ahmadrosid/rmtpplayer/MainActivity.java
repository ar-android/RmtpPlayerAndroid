package com.ahmadrosid.rmtpplayer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SimpleExoPlayerView player_view;
    private SimpleExoPlayer player;
    private String rtmpUrl = "rtmp://139.59.124.59:1935/live/mystream";
    private DefaultTrackSelector trackSelector;
    private com.google.android.exoplayer2.upstream.BandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private com.google.android.exoplayer2.source.MediaSource mediaSource;
    private RtmpDataSource.RtmpDataSourceFactory rtmpDataSourceFactory;
    private Uri uri;
    private Handler mainHandler;
    private com.google.android.exoplayer2.source.ExtractorMediaSource.EventListener eventLogger;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player_view = (SimpleExoPlayerView) findViewById(R.id.player_view);
        player_view.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override public void onVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE){
                    Log.d(TAG, "onVisibilityChange: is visible");
                }
            }
        });
        player_view.requestFocus();

        rtmpDataSourceFactory = new RtmpDataSource.RtmpDataSourceFactory();
        uri = Uri.parse(rtmpUrl);
        mainHandler = new Handler();
        eventLogger = new ExtractorMediaSource.EventListener() {
            @Override public void onLoadError(IOException error) {
                Log.e(TAG, "onLoadError: ", error);
            }
        };

        mediaSource = new ExtractorMediaSource(uri, rtmpDataSourceFactory, new DefaultExtractorsFactory(),
                mainHandler, eventLogger);

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player_view.setPlayer(player);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }
}
