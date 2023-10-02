package com.mikaela.sps;

import android.os.CountDownTimer;

public class Test {
    private void test() {
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {

            }
        }.start();
    }
}
