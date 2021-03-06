package it.gphilo.combitracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v8.renderscript.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.Console;

import it.gphilo.combitracker.ScriptC_brightness;

public class BrightnessFragment extends Fragment {
    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;
    private ImageView out;
    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private ScriptC_brightness mScript;
    private SeekBar brightness;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.brightness_layout, null);

        brightness = (SeekBar) view.findViewById(R.id.brightness);

        brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                mScript.set_brightness(progress/255.0f);
                apply();
                out.invalidate();
            }
        });

        mBitmapIn = loadBitmap(R.drawable.sunset);
        mBitmapOut = loadBitmap(R.drawable.sunset);

        ImageView in = (ImageView) view.findViewById(R.id.displayin);
        in.setImageBitmap(mBitmapIn);

        out = (ImageView) view.findViewById(R.id.displayout);
        out.setImageBitmap(mBitmapOut);

        createScript();

        brightness.setProgress(0);

        apply();
        out.invalidate();

        return view;
    }

    protected void apply() {
        Log.d("CombiTracker", "Applying kernel");
        try {
            mScript.forEach_filter(mInAllocation, mOutAllocation);
            mOutAllocation.copyTo(mBitmapOut);
        } catch (Exception e) {
            Log.d("CombiTracker", "Thrown exception "+e.getMessage());
        }
    }

    private void createScript() {
        mRS = RenderScript.create(getActivity());

        mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());

        mScript = new ScriptC_brightness(mRS, getResources(), R.raw.brightness);

        mScript.set_gIn(mInAllocation);
        //mScript.set_gOut(mOutAllocation);
        //mScript.set_gScript(mScript);
    }

    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }
}