package com.nancy.cs478_p5_musicclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nancy.cs478_p5_musicaidl.MusicAIDL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.nancy.cs478_p5_musicclient.R;

/*
*
* NAME: NANCY PITTA
* UIN: 672134497
* */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    protected static MusicAIDL musicaidlobj;
    public static MediaPlayer mediaPlayer ;
    public static boolean mIsBound = false;
    static boolean serviceState = false;
    public  Bundle bundle_2A = new Bundle();
    private Bitmap[] image = new Bitmap[7];
    private byte[][] byteArray = new byte[7][];
    static Button startService;
    static Button stopService;
    static Button GetallSongsInfo;
    static Spinner spinner;
    static int singleSongID;
    static int updatedSongID;
    static Button selectSingleSong;
    static TextView status;
    static String title;
    @Override
    protected void onStart() {
        super.onStart();
        checkbutton_availability();
    }

    @Override
    protected void onStop() {
        Log.i("NANCY","onStop Activity");
        super.onStop();
        checkbutton_availability();
    }

    @Override
    protected void onDestroy() {
        Log.i("NANCY","ondestroy Activity");
        super.onDestroy();
        stopService();
        checkbutton_availability();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkbutton_availability();
        Log.i("NANCY","onResume Activity");
    }

    @Override
    public void onPause() {
        Log.i("NANCY","onPause Activity");
        super.onPause();
    }

    @Override
    public void onCreate(Bundle bundleData)  {

        super.onCreate(bundleData);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.status);
        selectSingleSong = (Button) findViewById(R.id.onesong);
        GetallSongsInfo = (Button) findViewById(R.id.songlist);
        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);
        spinner = (Spinner) findViewById(R.id.song_spinner);
        spinner.setPrompt("Select favorite song");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.song_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(this);


        if (serviceState) {
            status.setText("Service bound now");
            checkFG();
            checkbutton_availability();
        } else {
            status.setText("Service not bound now");
        }
        stopService.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               stopService();
                                               checkbutton_availability();
                                           }
                                       }

        );

        startService.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                status.setText("Initating Start Service");
                                                checkFG();
                                            }
                                        }

        );

        selectSingleSong.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 singleSongID = spinner.getSelectedItemPosition();
                                                 if(singleSongID!=0) {
                                                     try {
                                                         if (mIsBound) {
                                                             updatedSongID = singleSongID-1;
                                                             Bundle bundle = musicaidlobj.getSingleSong(singleSongID-1);


                                                             Bitmap onebmImg = bundle.getParcelable("oneimage");
                                                             title = bundle.getString("songnames");
                                                             String band = bundle.getString("singername");
                                                             String songurl = bundle.getString("SONGURL");
                                                             String[] titles = {title};
                                                             String[] bands = {band};
                                                             String[] songurls = {songurl};
                                                             ByteArrayOutputStream onestream = new ByteArrayOutputStream();
                                                             onebmImg.compress(Bitmap.CompressFormat.PNG, 100, onestream);
                                                             byte[] onebyteArray = onestream.toByteArray();

                                                             //passing to second activity
                                                             bundle_2A.putStringArray("SONGS", titles);
                                                             bundle_2A.putStringArray("BANDS", bands);
                                                             bundle_2A.putStringArray("URLs", songurls);
                                                             bundle_2A.putByteArray("image", onebyteArray);


                                                             Intent SecondActivityIntent = new Intent(getApplicationContext(), SecondActivity.class);
                                                             SecondActivityIntent.putExtra("click", "getOneSong");
                                                             SecondActivityIntent.putExtras(bundle_2A);
                                                             startActivity(SecondActivityIntent);
                                                         } else {
                                                             status.setText("Service not bound");

                                                         }
                                                     } catch (RemoteException e) {
                                                         Log.e("NANCY", e.toString());
                                                     }
                                                 }else{
                                                     Toast.makeText(getApplicationContext(), "Please Select a song", Toast.LENGTH_LONG).show() ;
                                                 }
                                             }
                                         }


        );


        GetallSongsInfo.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   try {
                                                       if (mIsBound) {

                                                           Bundle bundle = musicaidlobj.getAllSongs();
                                                           String[] titles = bundle.getStringArray("songnames");
                                                           String[] band = bundle.getStringArray("singername");
                                                           String[] songurl = bundle.getStringArray("SONGURL");
                                                           for (int i = 0; i < 5; i++) {
                                                               String s = "img" + (i + 1);

                                                               image[i] = bundle.getParcelable(s);
                                                               ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                               image[i].compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                               byteArray[i] = stream.toByteArray();
                                                               bundle_2A.putByteArray(s, byteArray[i]);
                                                           }

                                                           bundle_2A.putStringArray("SONGS", titles);
                                                           bundle_2A.putStringArray("BANDS", band);
                                                           bundle_2A.putStringArray("URLs", songurl);


                                                           Intent SecondActivityIntent = new Intent(getApplicationContext(), SecondActivity.class);
                                                           SecondActivityIntent.putExtra("click", "getAllSongs");
                                                           SecondActivityIntent.putExtras(bundle_2A);
                                                           startActivity(SecondActivityIntent);
                                                       }
                                                       else{
                                                           status.setText("Service Not bound");
                                                           Log.i("NANCY", "SERVICE NOT BOUND");
                                                       }
                                                   } catch (RemoteException e) {
                                                       Log.e("NANCY", e.toString());
                                                   }
                                               }
                                           }

        );
    }

    //Spinner OnItem Select
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    protected static void PlaySong(String url) throws IOException {
       if(!IsPlaying()) {
            mediaPlayer= new MediaPlayer();
            mediaPlayer.setLooping(false);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer mp) {
                                                        mediaPlayer.release();
                                                        mediaPlayer = null;
                                                    }
                                                }

            );
        }
        else {
           stopSong();
           PlaySong(url);
        }

    }


    protected static boolean pausePlayer() {
        if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            status.setText("Song Paused");
            Log.i("NANCY", "Song Paused");
            return true;
        } else {
            return false;
        }
    }


    public static boolean resumeSong() {
        if (!mediaPlayer.isPlaying() && mediaPlayer != null ) {
            mediaPlayer.start();
            status.setText("Song Resumed");
            Log.i("NANCY", "Song Resumed");
            return true;
        } else {
            return false;
        }
    }


    public static boolean stopSong() {
        if (mediaPlayer!= null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            status.setText("Song Stopped");
            return true;
        } else {
            return false;
        }
    }

    protected static boolean IsPlaying() {
        if (mediaPlayer != null) {
            Log.i("NANCY", "Song is playing");
            return mediaPlayer.isPlaying();
        }
        else {
            Log.i("NANCY", "Song is not playing");
            return false;
        }
    }


    private  void checkbutton_availability() {
        Log.i("NANCY","Enabling and disabling"+mIsBound);
        if(!mIsBound) {
            startService.setEnabled(true);
            stopService.setEnabled(false);
            GetallSongsInfo.setEnabled(false);
            spinner.setEnabled(false);
            selectSingleSong.setEnabled(false);
        }
        if(mIsBound) {
            startService.setEnabled(true);
            stopService.setEnabled(true);
            GetallSongsInfo.setEnabled(true);
            spinner.setEnabled(true);
            selectSingleSong.setEnabled(true);
        }
    }
    protected   void checkbind() {
        boolean bindValue = false;
        if (!mIsBound && serviceState) {
            Intent i = new Intent(MusicAIDL.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, 0);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
            bindValue = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if (bindValue) {
                status.setText("BindService Succeeded!");

            } else {
                Log.i("NANCY", "bindService failed");
            }
        }
    }


    private void stopService() {
        doUnbindService();
        Intent i = new Intent(MusicAIDL.class.getName());
        ResolveInfo info = getPackageManager().resolveService(i, 0);
        i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        if (getApplicationContext().stopService(i) && serviceState) {
            // successfully stopped service
            serviceState = false;
            Log.i("NANCY", "stop service succeeded");
        }        status.setText("Service Stopped");
        Log.i("NANCY","Service Stopped");
        checkbutton_availability();
    }

    private void  doUnbindService(){
        if (mIsBound) {
            Log.i("NANCY","Unbinding Service");
            mIsBound=false;
            unbindService(this.mConnection);
            status.setText("Service Unbinded");
            Log.i("NANCY","Service Unbinded");
        }
    }

    protected void checkFG() {
        Intent i = new Intent(MusicAIDL.class.getName());
        ResolveInfo info = getPackageManager().resolveService(i, 0);
        i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
        if (getApplicationContext().startForegroundService(i) != null && !serviceState) {
            serviceState = true;
            status.setText("Service started");
            Log.i("NANCY", "Start service success");

        }
        checkbind();
    }





    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iservice) {
            musicaidlobj = MusicAIDL.Stub.asInterface(iservice);
            mIsBound = true;
            checkbutton_availability();
            Log.i("NANCY","on_service connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            musicaidlobj = null;
            mIsBound = false;
            if(mediaPlayer!=null) {
                mediaPlayer.stop();
                Log.i("NANCY", "Music Stopped");
            }
            checkbutton_availability();
            Log.i("NANCY","on_service disconnected");
        }
    };
}