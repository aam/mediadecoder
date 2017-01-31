/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.basicmediadecoder;


import android.animation.TimeAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.example.android.common.media.MediaCodecWrapper;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This activity uses a {@link android.view.TextureView} to render the frames of a video decoded using
 * {@link android.media.MediaCodec} API.
 */
public class MainActivity extends Activity {

//    private TextureView mPlaybackView;
//    private TimeAnimator mTimeAnimator = new TimeAnimator();

    // A utility that wraps up the underlying input and output buffer processing operations
    // into an east to use API.
//    private MediaCodecWrapper mCodecWrapper;
//    private MediaExtractor mExtractor = new MediaExtractor();
    TextView mAttribView = null;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
//        mPlaybackView = (TextureView) findViewById(R.id.PlaybackView);
        mAttribView =  (TextView)findViewById(R.id.AttribView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(mTimeAnimator != null && mTimeAnimator.isRunning()) {
//            mTimeAnimator.end();
//        }
//
//        if (mCodecWrapper != null ) {
//            mCodecWrapper.stopAndRelease();
//            mExtractor.release();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_play) {
            mAttribView.setVisibility(View.VISIBLE);
            try {
                startDecode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            item.setEnabled(false);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startDecode() throws IOException {
        Uri videoUri = Uri.parse("android.resource://"
                + getPackageName() + "/"
                + R.raw.vid_bigbuckbunny);

        final MediaExtractor extractor = new MediaExtractor();
        MediaCodec codec = null;

        extractor.setDataSource(this, videoUri, null);
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; ++i) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            boolean weAreInterestedInThisTrack = true;
            if (mime.contains("video/")) {
                final MediaCodec videoCodec = MediaCodec.createDecoderByType(mime);
                videoCodec.configure(extractor.getTrackFormat(i), null /* surface */, null /* crypto */,  0 /* flags */);

                videoCodec.setCallback(new MediaCodec.Callback() {
                    @Override
                    public void onInputBufferAvailable(MediaCodec mc, int inputBufferId) {
                        ByteBuffer inputBuffer = videoCodec.getInputBuffer(inputBufferId);
                        while(true) {
                            int size = extractor.readSampleData(inputBuffer, 0);
                            long presentationTimeUs = extractor.getSampleTime();
                            if (size >= 0) {
                                videoCodec.queueInputBuffer(inputBufferId, 0, size, presentationTimeUs, extractor.getSampleFlags());
                            }
                            if (!extractor.advance()) {
                                videoCodec.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onOutputBufferAvailable(MediaCodec mediaCodec, int outputBufferId, MediaCodec.BufferInfo bufferInfo) {
                        ByteBuffer outputBuffer = videoCodec.getOutputBuffer(outputBufferId);
                        MediaFormat bufferFormat = videoCodec.getOutputFormat(outputBufferId); // option A
                        // bufferFormat is equivalent to mOutputFormat
                        // outputBuffer is ready to be processed or rendered.
//                        …
                        videoCodec.releaseOutputBuffer(outputBufferId,  …);
                    }

                    @Override
                    public void onError(MediaCodec mediaCodec, MediaCodec.CodecException e) {

                    }

                    @Override
                    public void onOutputFormatChanged(MediaCodec mc, MediaFormat format) {
                        // Subsequent data will conform to new format.
                        // Can ignore if using getOutputFormat(outputBufferId)
//                        mOutputFormat = format; // option B
                    }

//                    @Override
//                    void onError(…) {
//                        …
//                    }
                });

                extractor.selectTrack(i);
                codec = videoCodec;
                break;
            }
        }

        if (codec == null) {
            return;
        }
//
//
//
//        ByteBuffer inputBuffer = ByteBuffer.allocate(16384)
//        while (extractor.readSampleData(inputBuffer, 0) >= 0) {
//            int trackIndex = extractor.getSampleTrackIndex();
//            long presentationTimeUs = extractor.getSampleTime();
//            /// ...
//            extractor.advance();
//        }
//
//        extractor.release();
//        extractor = null;
//
//
//        for (int i = 0; i < nTracks; ++i) {
//            // Try to create a video codec for this track. This call will return null if the
//            // track is not a video track, or not a recognized video format. Once it returns
//            // a valid MediaCodecWrapper, we can break out of the loop.
//            mCodecWrapper = MediaCodecWrapper.fromVideoFormat(mExtractor.getTrackFormat(i),
//                    new Surface(mPlaybackView.getSurfaceTexture()));
//            if (mCodecWrapper != null) {
//                mExtractor.selectTrack(i);
//                break;
//            }
//        }
//
//        MediaCodec codec = MediaCodec.createByCodecName(name);
//        MediaFormat mOutputFormat; // member variable
//        codec.setCallback(new MediaCodec.Callback() {
//            @Override
//            void onInputBufferAvailable(MediaCodec mc, int inputBufferId) {
//                ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
//                // fill inputBuffer with valid data
////                …
//                codec.queueInputBuffer(inputBufferId, …);
//            }
//
//            @Override
//            void onOutputBufferAvailable(MediaCodec mc, int outputBufferId, …) {
//                ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
//                MediaFormat bufferFormat = codec.getOutputFormat(outputBufferId); // option A
//                // bufferFormat is equivalent to mOutputFormat
//                // outputBuffer is ready to be processed or rendered.
//                …
//                codec.releaseOutputBuffer(outputBufferId, …);
//            }
//
//            @Override
//            void onOutputFormatChanged(MediaCodec mc, MediaFormat format) {
//                // Subsequent data will conform to new format.
//                // Can ignore if using getOutputFormat(outputBufferId)
//                mOutputFormat = format; // option B
//            }
//
//            @Override
//            void onError(…) {
//                …
//            }
//        });
//        videoCodec.configure(format, …);
//        mOutputFormat = codec.getOutputFormat(); // option B
        codec.start();
        // wait for processing to complete
        codec.stop();
        codec.release();
    }


//    public void startPlayback() {
//
//        // Construct a URI that points to the video resource that we want to play
//        Uri videoUri = Uri.parse("android.resource://"
//                + getPackageName() + "/"
//                + R.raw.vid_bigbuckbunny);
//
//        try {
//
//            // BEGIN_INCLUDE(initialize_extractor)
//            mExtractor.setDataSource(this, videoUri, null);
//            int nTracks = mExtractor.getTrackCount();
//
//            // Begin by unselecting all of the tracks in the extractor, so we won't see
//            // any tracks that we haven't explicitly selected.
//            for (int i = 0; i < nTracks; ++i) {
//                mExtractor.unselectTrack(i);
//            }
//
//
//            // Find the first video track in the stream. In a real-world application
//            // it's possible that the stream would contain multiple tracks, but this
//            // sample assumes that we just want to play the first one.
//            for (int i = 0; i < nTracks; ++i) {
//                // Try to create a video codec for this track. This call will return null if the
//                // track is not a video track, or not a recognized video format. Once it returns
//                // a valid MediaCodecWrapper, we can break out of the loop.
//                mCodecWrapper = MediaCodecWrapper.fromVideoFormat(mExtractor.getTrackFormat(i),
//                        new Surface(mPlaybackView.getSurfaceTexture()));
//                if (mCodecWrapper != null) {
//                    mExtractor.selectTrack(i);
//                    break;
//                }
//            }
//            // END_INCLUDE(initialize_extractor)
//
//
//
//
//            // By using a {@link TimeAnimator}, we can sync our media rendering commands with
//            // the system display frame rendering. The animator ticks as the {@link Choreographer}
//            // recieves VSYNC events.
//            mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
//                @Override
//                public void onTimeUpdate(final TimeAnimator animation,
//                                         final long totalTime,
//                                         final long deltaTime) {
//
//                    boolean isEos = ((mExtractor.getSampleFlags() & MediaCodec
//                            .BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//
//                    // BEGIN_INCLUDE(write_sample)
//                    if (!isEos) {
//                        // Try to submit the sample to the codec and if successful advance the
//                        // extractor to the next available sample to read.
//                        boolean result = mCodecWrapper.writeSample(mExtractor, false,
//                                mExtractor.getSampleTime(), mExtractor.getSampleFlags());
//
//                        if (result) {
//                            // Advancing the extractor is a blocking operation and it MUST be
//                            // executed outside the main thread in real applications.
//                            mExtractor.advance();
//                        }
//                    }
//                    // END_INCLUDE(write_sample)
//
//                    // Examine the sample at the head of the queue to see if its ready to be
//                    // rendered and is not zero sized End-of-Stream record.
//                    MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
//                    mCodecWrapper.peekSample(out_bufferInfo);
//
//                    // BEGIN_INCLUDE(render_sample)
//                    if (out_bufferInfo.size <= 0 && isEos) {
//                        mTimeAnimator.end();
//                        mCodecWrapper.stopAndRelease();
//                        mExtractor.release();
//                    } else if (out_bufferInfo.presentationTimeUs / 1000 < totalTime) {
//                        // Pop the sample off the queue and send it to {@link Surface}
//                        mCodecWrapper.popSample(true);
//                    }
//                    // END_INCLUDE(render_sample)
//
//                }
//            });
//
//            // We're all set. Kick off the animator to process buffers and render video frames as
//            // they become available
//            mTimeAnimator.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
