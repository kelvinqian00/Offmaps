package edu.jhu.kqian2.offmaps;

import android.content.Context;
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

public class StepCountActivity extends AppCompatActivity implements SensorEventListener {

    private boolean startCount;
    int numSteps;

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor stepDetector;

    // UI variables
    TextView counterText;
    Button counterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        counterText = (TextView) findViewById(R.id.text_counter);

        counterButton = (Button) findViewById(R.id.button_counter);
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCount = !startCount;
                if(startCount) {
                    sensorManager.registerListener(StepCountActivity.this,
                        stepDetector, SensorManager.SENSOR_DELAY_NORMAL);
                    counterButton.setText("End Counter");
                } else {
                    sensorManager.unregisterListener(StepCountActivity.this, stepDetector);

                    counterButton.setText("Start Counter");

                }
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR && startCount) {
            ++numSteps;
            counterText.setText("Steps: " + numSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
