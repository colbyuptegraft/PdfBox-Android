package com.tom_roush.pdfbox.pdmodel.graphics.color;

import android.graphics.Bitmap;
import android.util.Log;

import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;

/**
 * Colours in the DeviceRGB colour space are specified according to the additive
 * RGB (red-green-blue) colour model.
 *
 * @author Ben Litchfield
 * @author John Hewson
 */
public final class PDDeviceRGB extends PDDeviceColorSpace {
    /**
     * This is the single instance of this class.
     */
    public static final PDDeviceRGB INSTANCE = new PDDeviceRGB();

    private final PDColor initialColor = new PDColor(new float[]{0, 0, 0}, this);

//    private volatile ColorSpace awtColorSpace;

    private PDDeviceRGB()
    {
    }

    /**
     * Lazy setting of the AWT color space due to JDK race condition.
     */
    private void init()
    {
        // no need to synchronize this check as it is atomic
//        if (awtColorSpace != null)
//        {
//            return;
//        }
        synchronized (this)
        {
            // we might have been waiting for another thread, so check again
//            if (awtColorSpace != null)
//            {
//                return;
//            }
//            awtColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);

            // there is a JVM bug which results in a CMMException which appears to be a race
            // condition caused by lazy initialization of the color transform, so we perform
            // an initial color conversion while we're still synchronized, see PDFBOX-2184
//            awtColorSpace.toRGB(new float[] { 0, 0, 0, 0 });
        }
    }

    @Override
    public String getName() {
        return COSName.DEVICERGB.getName();
    }

    /**
     * @inheritDoc
     */
    public int getNumberOfComponents() {
        return 3;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0, 1, 0, 1, 0, 1};
    }

    @Override
    public PDColor getInitialColor() {
        return initialColor;
    }

    @Override
    public float[] toRGB(float[] value) {
        // This is just assuming that the values being sent to it are already in RGB color space.
        if (value.length == 3) {
            return value;
        } else {
//            init();
//            return awtColorSpace.toRGB(value);
            return initialColor.getComponents();
        }
    }

    @Override
    public Bitmap toRGBImage(Bitmap raster) throws IOException
    {
        if (raster.getConfig() == Bitmap.Config.ALPHA_8)
        {
            Log.e("PdfBox-Android", "Raster in PDDeviceRGB was ALPHA_8");
        }
        return raster;

//        inti();
//        ColorModel colorModel = new ComponentColorModel(awtColorSpace,
//                false, false, Transparency.OPAQUE, raster.getDataBuffer().getDataType());
//
//        return new BufferedImage(colorModel, raster, false, null); TODO: PdfBox-Android
    }
}
