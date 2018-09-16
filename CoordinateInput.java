package edu.jhu.kqian2.offmaps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class CoordinateInput extends AppCompatActivity {

    EditText startLatEdit;
    EditText startLongEdit;
    EditText endLatEdit;
    EditText endLongEdit;

    double startLat;
    double startLong;
    double endLat;
    double endLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinate_input);

        startLatEdit = findViewById(R.id.start_lat_edit);
        startLongEdit = findViewById(R.id.start_long_edit);
        endLatEdit = findViewById(R.id.end_lat_edit);
        endLongEdit = findViewById(R.id.end_long_edit);
    }

    public void inputData(View view) {
        if (view != findViewById(R.id.submit_coords_button)) return;
        try {
            startLat = Double.parseDouble(startLatEdit.getText().toString());
            startLong = Double.parseDouble(startLongEdit.getText().toString());
            endLat = Double.parseDouble(endLatEdit.getText().toString());
            endLong = Double.parseDouble(endLongEdit.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        this.startNewActivity();
    }

    private void startNewActivity() {
        double[] startCoords = {startLat, startLong};
        double[] endCoords = {endLat, endLong};

        Intent intent = new Intent(this, StepCountActivity.class);
        intent.putExtra("start_coords", startCoords);
        intent.putExtra("end_coords", endCoords);
        this.startActivity(intent);
    }
}