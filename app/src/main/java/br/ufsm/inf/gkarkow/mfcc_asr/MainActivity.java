package br.ufsm.inf.gkarkow.mfcc_asr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.mfcc.MFCC;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String fileName = R.raw.english;

        AndroidFFMPEGLocator ffmpegLocator = new AndroidFFMPEGLocator(this);

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(16000,1024,0);
        //AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(fileName,16000,1024,0);

        // MFCC parameters
        int samplesPerFrame = 400;      // 400 samples per frame
        float sampleRate = 16000;       // 16 kHz
        int amountOfCepstrumCoef = 12;  // 12 MFCC coeficients
        int amountOfMelFilters = 10;    // 26 filterbanks
        float lowerFrequency = 300;     // 300 Hz
        float upperFrequency = 8000;    // 8000 Hz

        MFCC mfcc = new MFCC(samplesPerFrame,
                sampleRate,
                amountOfCepstrumCoef,
                amountOfMelFilters,
                lowerFrequency,
                upperFrequency);

        dispatcher.addAudioProcessor(mfcc);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }
}
