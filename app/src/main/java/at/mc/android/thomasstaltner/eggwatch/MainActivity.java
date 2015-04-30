package at.mc.android.thomasstaltner.eggwatch;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.content.Intent.ACTION_VOICE_COMMAND;


public class MainActivity extends Activity implements View.OnClickListener {

    final static String TAG = "EggWatch";

    private TextView showLatitude;
    private TextView showLongitude;
    private TextView showAltitude;
    private TextView showLocation;

    private LocationManager manager;
    private LocationListener ll;

    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar_Altitude);
        pb.setVisibility(View.INVISIBLE);

        Button b = (Button) findViewById(R.id.button_start_boil);
        b.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_boil: {
                Log.i(TAG, "start-button pressed");
                pb.setVisibility(View.VISIBLE);


                showLocation = (TextView) findViewById(R.id.textView_show_location);
                showLatitude = (TextView) findViewById(R.id.textView_value_latitude);
                showLongitude = (TextView) findViewById(R.id.textView_value_longitude);
                showAltitude = (TextView) findViewById(R.id.textView_value_altitude);

                SeekBar size = (SeekBar) findViewById(R.id.seekBar_my_egg_is____);
                SeekBar consist = (SeekBar) findViewById(R.id.seekBar_how_should_it_be);

                manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                ll = new myLocationListener();
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

                showLocation.setText(Double.toString(size.getProgress()) + "\n" + consist.getProgress());

//                pb.setVisibility(View.INVISIBLE);

                /*
                Set the Alarm Intent
                 */

                final TextView mTextField = (TextView) findViewById(R.id.mTextField);

                final int countTimer = 5000;
                final int ringToneTimer = 4000;

                new CountDownTimer(countTimer, 100) {

                    final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                    public void onTick(long millisUntilFinished) {
                        mTextField.setText("seconds remaining: " + millisUntilFinished / 1000 + "." + millisUntilFinished % 10);
                    }

                    public void onFinish() {
                        mTextField.setText("done!");



                        if (audio.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                            try {
                                wait(ringToneTimer);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            r.stop();
                        } else {
                        }
                    }
                }.start();

                Intent i = new Intent(this, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
//                PendingIntent pi2 = Pend

                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (countTimer + ringToneTimer + 2000), pi);


            } break;
            default:
                Log.i(TAG, "unknown button pressed!");
        }
    }

            class myLocationListener implements LocationListener {

                boolean first = true;

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null && first) {

//                               pb.setVisibility(View.VISIBLE);

                        double pAlt = location.getAltitude();
                        double pLong = location.getLongitude();
                        double pLat = location.getLatitude();

                        showAltitude.setText(Double.toString(pAlt) + " meters");
                        showLatitude.setText(Double.toString(pLat) + " °");
                        showLongitude.setText(Double.toString(pLong) + " °");
//                        showLocation.setText((int) alt.getElevationFromGoogleMaps(pLong, pLat));
//                        showLocation.setText(Double.toString(alt.getElevationFromGoogleMaps(43d, 100d)));
//                        first = false;
                        manager.removeUpdates(ll);

                        pb.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    switch (status){
                        case LocationProvider.OUT_OF_SERVICE:{
                            double pAlt = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAltitude();
                        } break;
                        default:{}
                    }
                    if(status == LocationProvider.OUT_OF_SERVICE){

                    }
                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }

        }
