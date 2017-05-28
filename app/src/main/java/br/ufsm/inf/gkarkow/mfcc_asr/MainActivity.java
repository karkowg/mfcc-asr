package br.ufsm.inf.gkarkow.mfcc_asr;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.caucse.ai.hermes.core.FeatureExtractor;
import com.caucse.ai.hermes.core.FileLoader;
import com.caucse.ai.hermes.util.WavFileException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.mfcc.MFCC;

public class MainActivity extends AppCompatActivity {

    private Button btnProcess;
    private EditText etMfccMeanValue;
    private EditText etPsfValue;
    private String testFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnProcess = (Button) findViewById(R.id.btn_process_wav);
        etMfccMeanValue = (EditText) findViewById(R.id.et_mfcc_mean_value);
        etPsfValue = (EditText) findViewById(R.id.et_psf_value);

        testFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mfcc/english.wav";

        byte[] soundBytes;

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.english);

            soundBytes = new byte[inputStream.available()];
            inputStream.read(soundBytes);
            inputStream.close();
            File sdcard = Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath() + "/mfcc/");
            dir.mkdir();
            File file = new File(dir, "english.wav");
            try {
                FileOutputStream os = new FileOutputStream(file);
                os.write(soundBytes);
                os.flush();
                os.close();
                Toast.makeText(this, "test wav saved in external storage " + soundBytes, Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] streamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    // Hermes
    public void processWav(View view) {
        FileLoader fileLoader = new FileLoader(testFile);
        String destinationPath = fileLoader.getDestinationPath();
        FeatureExtractor fex = null;

        try {
            fex = new FeatureExtractor(destinationPath);
        } catch (IOException | WavFileException e) {
            e.printStackTrace();
        }

        assert fex != null;
        double mfcc = fex.getTarsosDSP_MFCC_Mean();
        double psf = fex.getPerceptualSpectralFlux_Mean();
        etMfccMeanValue.setText(Double.toString(mfcc));
        etPsfValue.setText(Double.toString(psf));
    }

    // TarsosDSP
    public void getMfcc(View view) {
        AndroidFFMPEGLocator ffmpegLocator = new AndroidFFMPEGLocator(this);

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(16000,1024,0);
        //AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(testFile,16000,1024,0);

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
