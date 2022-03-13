package com.example.aplikasitremor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import static com.example.aplikasitremor.FourierTransform.fft;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class SensorMotion extends AppCompatActivity implements SensorEventListener {
    private static final float NS2S = 1.0f / 1000000000.0f;
    FuzzyMamdani Mamdani = new FuzzyMamdani();
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private static final double EPSILON = 0.05f;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private MadgwickAHRS mMadgwickAHRS = new MadgwickAHRS(0.01f, 0.00001f);
    FourierTransform Fourier = new FourierTransform();

    private int counter = 0;
    String Hasil, Hasil2, Hasil3, Hasil4, Hasil5;

    DecimalFormat DF = new DecimalFormat("#.###");
    private float Ax, Ay, Az, Ex, Ey, Ez,result;
    private double Gx, Gy, Gz;
    private TextView madZView, madYView, madXView, madWView;
    boolean isRunning;
    final String TAG = "SensorLog";
    SensorManager manager;
    Button buttonStart;
    FileWriter writer;
    TextView xValue;
    TextView yValue;
    TextView zValue;
    TextView resultfuzzy, resultfuzzy2, resultfuzzy3, resultfuzzy4, resultfuzzy5;
    TextView resultdesc, resultdesc2, resultdesc3, resultdesc4, resultdesc5;
    double hasilfuzzy, hasilfuzzy2, hasilfuzzy3, hasilfuzzy4, hasilfuzzy5,cinput;

    TextView zGyro;
    TextView textView, textView2, textView3, textView5, textView4;
    CountDownTimer countDownTimer;
    CountDownTimer countDownTimer2;
    CountDownTimer countDownTimer3;
    CountDownTimer countDownTimer4;
    CountDownTimer countDownTimer5;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_motion);
        isRunning = false;
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        buttonStart = (Button) findViewById(R.id.buttonstart);
        resultfuzzy = (TextView) findViewById(R.id.resultfuzzy);




        textView = findViewById(R.id.timer);

        
        


        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(millisUntilFinished / 1000 + " detik");
            }

            public void onFinish() {



                textView.setText("Waktu habis");
                Toast.makeText(SensorMotion.this, "Selesai", Toast.LENGTH_SHORT).show();
                manager.flush(SensorMotion.this);
                manager.unregisterListener(SensorMotion.this);

                if (cinput >= 3 && cinput <= 10) {
                    Hasil = String.format("Tremor Istirahat, Tremor Kinetik, Intensi, Task Spesific Tremor");
                }else if (cinput>=4 && cinput <= 12){
                    Hasil = String.format("Tremor Istirahat, Tremor Postural, Tremor Kinetik, Intensi, Task Spesific Tremor");
                }else if (cinput<=3){
                    Hasil = String.format("Normal");
                }
                resultdesc.setText("" + Hasil);



                final double frekuensi = cinput ;

                Mamdani.Rule();
                Mamdani.Fuzzyfikasi(frekuensi);
                Mamdani.ImplikasiMin();
                Mamdani.AgregasiMax();
                Mamdani.BatasArea();
                Mamdani.Momen();
                Mamdani.Luas();
                Mamdani.MomenLuas();
                Mamdani.resultfuzzy();

                hasilfuzzy = Double.parseDouble(DF.format(Mamdani.getresultfuzzy()));

                resultfuzzy.setText("Nilai Crisp: " + String.valueOf(hasilfuzzy*-1));




                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }


        };


        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                textView.setText("Waktu dimulai");


                Toast.makeText(SensorMotion.this, "Waktu dimulai", Toast.LENGTH_SHORT).show();
                countDownTimer.start();


                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), "HasilUji" + ".csv"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int redingrade = 1000; /** 1000ms **/
                manager.registerListener(SensorMotion.this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), redingrade);
                manager.registerListener(SensorMotion.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), redingrade);
                isRunning = true;
                return true;
            }
        });

    }


    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
        //  return "/storage/emulated/0/Android/data/com.iam360.sensorlog/";
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        resultdesc = (TextView) findViewById(R.id.resultdesc);



        //Accelerometer
        final float alpha = 0.8f;
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * evt.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * evt.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * evt.values[2];
        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = evt.values[0] - gravity[0];
        linear_acceleration[1] = evt.values[1] - gravity[1];
        linear_acceleration[2] = evt.values[2] - gravity[2];
        Ax = linear_acceleration[0];
        Ay = linear_acceleration[1];
        Az = linear_acceleration[2];

        if (evt.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            Gx = evt.values[0];
            Gy = evt.values[1];
            Gz = evt.values[2];
        }

        mMadgwickAHRS.update(Gx, Gy, Gz, Ax, Ay, Az);
        float[] quaternion = mMadgwickAHRS.getQuaternion();

        double[] input = {quaternion[0],quaternion[1],quaternion[2],quaternion[3]};
        Complex[] cinput = new Complex[input.length];
        for (int i = 0; i < input.length; i++)
            cinput[i] = new Complex(input[i], 0.0);

        Fourier.fft(cinput);
        Complex [] c = cinput;










        if (isRunning) {
            try {
                switch (Arrays.toString(input)) {

                    default:

                        writer.write(String.format("%f\n",cinput));
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



}