package com.best.cy.fitnessapp2;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;


/**IOT hub 라이브러리**/
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.Message;
import java.io.IOException;
import java.net.URISyntaxException;

/*IOT hub EventCallback 메소드**/
class EventCallback implements IotHubEventCallback
{
    public void execute(IotHubStatusCode status, Object context)
    {
        Integer i = (Integer) context;
        System.out.println("IoT Hub responded to message " + i.toString()
                + " with status " + status.name());
    }
}



public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /*IOT hub 변수 설정**/
    private final String connString = "HostName=seonamiIothub.azure-devices.net;DeviceId=seonamidevice;SharedAccessKey=SYuQ8F7WX1LtCVb/2X+kwu5Ii7hBiXE4Ir5NeSvkN2k=";
    private final String deviceId = "seonamidevice";
    static DeviceClient client;
    IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;

    /*IOT hub 메소드 구현*/
    private void InitClient() throws IOException, URISyntaxException {
        client = new DeviceClient(connString, protocol);
        try {
            client.open();
        } catch (Exception e) {
            client.close();
        }
    }



    SensorManager sm;
    Sensor sensor_accelerometer;

    long myTime, myTime2;

    float x, y, z;
    float lastX, lastY, lastZ;


    final int walkThreshold = 455; //걷기 인식 임계값

    double acceleration = 0;
    StepCounterView mJumpGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //화면을 세로로 설정하기
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.stepcounter);

        mJumpGame = findViewById(R.id.mJumpGame);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensor_accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        try {

            /**IOT hub 실행*/
            InitClient();

        } catch (Exception e) {

        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        sm.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            myTime2 = System.currentTimeMillis();
            long gabOfTime = myTime2 - myTime;

            if (gabOfTime > 100) {
                myTime = myTime2;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                acceleration = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (acceleration > walkThreshold) {
                    StepCounterView.walkingCount += 1.0;
                    mJumpGame.postInvalidate();

                    try {
                        String msgStr = Integer.toString(StepCounterView.walkingCount);
                        Message msg = new Message(msgStr);
                        EventCallback eventCallback = new EventCallback();
                        client.sendEventAsync(msg, eventCallback, 1);
                    } catch (Exception e) {}


                }

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
            }

        }
    }
}

