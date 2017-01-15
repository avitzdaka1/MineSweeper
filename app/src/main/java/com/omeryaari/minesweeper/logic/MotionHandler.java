package com.omeryaari.minesweeper.logic;

import com.omeryaari.minesweeper.service.AccelerometerService;

public class MotionHandler implements Runnable, AccelerometerService.AccelerometerListener, TimerChangedListener{

    private static final String TAG = Logic.class.getSimpleName();
    private static final int MOTION_CHANGE = 3;
    private float[] initialValues;
    private boolean started = false;
    private boolean initialPlacement = true;
    private AccelerometerService accelerometerService;
    private Logic.ScreenSide side;
    private Logic gameLogic;

    public MotionHandler(AccelerometerService accelerometerService, Logic gameLogic) {
        this.accelerometerService = accelerometerService;
        this.gameLogic = gameLogic;
    }

    @Override
    public void onSensorEvent(float[] values) {
        if (!started) {
            initialValues = values.clone();
            started = true;
        }
        if (Math.abs(values[0] - initialValues[0]) >= MOTION_CHANGE) {
            // If device was tilted to the left side.
            if (values[0] > initialValues[0])
                side = Logic.ScreenSide.Left;
                //  If device was tilted to the right side.
            else if (values[0] < initialValues[0])
                side = Logic.ScreenSide.Right;
            initialPlacement = false;
        }

        //  Pitch.
        else if (Math.abs(values[1] - initialValues[1]) >= MOTION_CHANGE) {
            //  If device was tilted to the bottom.
            if (values[1] > initialValues[1])
                side = Logic.ScreenSide.Bottom;
                //  If device was tilted to the top.
            else if (values[1] < initialValues[1])
                side = Logic.ScreenSide.Top;
            initialPlacement = false;
        }

        //  Back to initial placement.
        else {
            if (!initialPlacement) {
                side = Logic.ScreenSide.Initial;
                initialPlacement = true;
            }
        }
    }

    @Override
    public void run() {
        accelerometerService.startListening();
    }

    @Override
    public void timeChanged() {
        if (side != null && side != Logic.ScreenSide.Initial) {
            for(int i = 0; i < Logic.MINES_TO_ADD_WHEN_TILTED; i++)
                gameLogic.onTiltDevice(side);
        }
    }
}