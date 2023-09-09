package com.lexmark.lpm_scan.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.geniusscansdk.core.FilterType;
import com.geniusscansdk.core.Quadrangle;

import java.io.File;

public class Page implements Parcelable {

    public Page(File originalImage) {
        this.originalImage = originalImage;
    }

    private final File originalImage;
    private File enhancedImage;
    private Quadrangle quadrangle;
    private FilterType filterType;
    private boolean distortionCorrectionEnabled = true;
    private boolean automaticallyOriented = false;

    public File getOriginalImage() {
        return originalImage;
    }

    public File getEnhancedImage() {
        return enhancedImage;
    }

    public void setEnhancedImage(File enhancedImage) {
        if (this.enhancedImage != null) {
            this.enhancedImage.delete();
        }
        this.enhancedImage = enhancedImage;
    }

    public void setQuadrangle(Quadrangle quadrangle) {
        this.quadrangle = quadrangle;
    }

    public Quadrangle getQuadrangle() {
        return quadrangle;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setDistortionCorrectionEnabled(boolean distortionCorrectionEnabled) {
        this.distortionCorrectionEnabled = distortionCorrectionEnabled;
    }

    public boolean isDistortionCorrectionEnabled() {
        return distortionCorrectionEnabled;
    }

    public boolean isAutomaticallyOriented() {
        return automaticallyOriented;
    }

    public void setAutomaticallyOriented(boolean automaticallyOriented) {
        this.automaticallyOriented = automaticallyOriented;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalImage.getAbsolutePath());
        dest.writeString(enhancedImage == null ? "" : enhancedImage.getAbsolutePath());
        dest.writeParcelable(quadrangle, flags);
        dest.writeSerializable(filterType);
        dest.writeByte((byte) (distortionCorrectionEnabled ? 1 : 0));
        dest.writeByte((byte) (automaticallyOriented ? 1 : 0));
    }

    protected Page(Parcel in) {
        originalImage = new File(in.readString());
        String enhancedImagePath = in.readString();
        enhancedImage = enhancedImagePath.isEmpty() ? null : new File(enhancedImagePath);
        quadrangle = in.readParcelable(Quadrangle.class.getClassLoader());
        filterType = (FilterType) in.readSerializable();
        distortionCorrectionEnabled = in.readByte() != 0;
        automaticallyOriented = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}