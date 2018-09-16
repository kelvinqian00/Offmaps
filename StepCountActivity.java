package edu.jhu.kqian2.offmaps;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import static java.lang.Math.sqrt;

public class StepCountActivity extends AppCompatActivity implements SensorEventListener {

    private boolean startCount;
    int numSteps;

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor stepDetector;
    private Sensor accelerometer;

    // UI variables
    private TextView counterText;
    private Button counterButton;
    private TextView heightText;
    private TextView distanceText;

    private long[] startLocation;
    private long[] endLocation;
    private double strideLength;

    // Kinematic variables
    private double prevDist;
    private double[] prevVel = {0, 0, 0};
    private double prevTime;

    private float height;

    private double avgDist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        if(!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Intent pastIntent = this.getIntent();
        startLocation = pastIntent.getLongArrayExtra("start_coords");
        endLocation = pastIntent.getLongArrayExtra("end_coords");

        strideLength = this.getPreferences(Context.MODE_PRIVATE).getFloat("stride", 0) / 100;

        counterText = (TextView) findViewById(R.id.text_counter);
        heightText = findViewById(R.id.text_altitude);
        distanceText = findViewById(R.id.text_distance);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        Sensor altitudeDetector = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(this, altitudeDetector, SensorManager.SENSOR_DELAY_NORMAL);

        counterButton = (Button) findViewById(R.id.button_counter);
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCount = !startCount;
                if(startCount) {
                    sensorManager.registerListener(StepCountActivity.this,
                        stepDetector, SensorManager.SENSOR_DELAY_NORMAL);
                    sensorManager.registerListener(StepCountActivity.this, accelerometer,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    counterButton.setText("End Counter");
                } else {
                    sensorManager.unregisterListener(StepCountActivity.this, stepDetector);
                    sensorManager.unregisterListener(StepCountActivity.this, accelerometer);
                    counterButton.setText("Start Counter");
                    StepCountActivity.this.resetFields();
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR && startCount) {
            ++numSteps;
            counterText.setText(getString(R.string.step_text, numSteps));
        } else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressureValue = event.values[0];
            height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureValue);
            heightText.setText(getString(R.string.altitude_text, height));
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double[] accel = new double[3];
            for (int i = 0; i < 3; i++) {
                accel[i] = event.values[i];
            }
            avgDist = averageDistance(stepDistance(strideLength, numSteps),
                    sensorDistance(accel));
            distanceText.setText(getString(R.string.distance_text, avgDist));
            //distanceText.setText("Sensor Distance : " + sensorDistance(accel));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    /* private void callPython() {
        Python py = Python.getInstance();
        PyObject pysearch = py.getModule("search");

        PyObject startLoc = pysearch.put("start_loc", startLocation);
        PyObject endLoc = pysearch.put("end_loc", endLocation);

        PyObject stepLen = pysearch.put("step_length", strideLength);
        PyObject windowLen = pysearch.put("window_len", 20);
        PyObject totalSteps = pysearch.put("total_steps", numSteps);
        PyObject pedoDist = pysearch.put("pedo_distance", stepDistance(strideLength, numSteps));
        PyObject totalDist = pysearch.put("total_distance", avgDist);

        PyObject locations = pysearch.get("locations");
        PyObject allG = pysearch.callAttr("create_graph", locations);
        PyObject shortestPath = pysearch.callAttr("search_shortest_path",
                allG, locations, startLoc, endLoc);
        PyObject kShortPaths = pysearch.callAttr("k_shortest_paths", allG,
                locations.callAttr("index", startLoc), locations.callAttr("index", endLoc));


        pysearch.get("find_path").call();
    } */


    private double stepDistance(double strideLength, double numSteps) {
        return strideLength * numSteps;
    }

    private double sensorDistance(double[] accel) {
        if (prevTime == 0) prevTime = ((double) System.currentTimeMillis()) / 1000.00;
        double newTime = ((double) System.currentTimeMillis()) / 1000.00; // to seconds
        double timeInterval = newTime - prevTime;

        double[] newVel = new double[3];
        double[] dispDelta = new double[3];
        for (int i = 0; i < 3; i++) {
            newVel[i] = prevVel[i] + accel[i] * timeInterval;
            dispDelta[i] = prevVel[i] * timeInterval
                    + 0.5 * accel[i] * timeInterval * timeInterval;
        }
        prevTime = newTime;
        prevVel = newVel;

        double newDist = prevDist + sqrt(dispDelta[0] * dispDelta[0] + dispDelta[1] * dispDelta[1]
                + dispDelta[2] * dispDelta[2]);
        prevDist = newDist;
        return newDist;
    }

    private double averageDistance(double stepDist, double sensorDist) {
        return (sensorDist + stepDist) / 2;
    }

    private void resetFields() {
        prevDist = 0;
        prevTime = 0;
        numSteps = 0;
        avgDist = 0;
        for (int i = 0; i < 3; i++) {
            prevVel[i] = 0;
        }

        counterText.setText(getString(R.string.step_text, numSteps));
        distanceText.setText(getString(R.string.distance_text, avgDist));
    }
}
