package com.lexmark.lpm_scan.processing

import android.content.Context

import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.core.LicenseException
import com.geniusscansdk.core.ProcessingException
import com.geniusscansdk.core.Quadrangle
import com.geniusscansdk.core.RotationAngle
import com.geniusscansdk.core.ScanProcessor;
import com.geniusscansdk.core.ScanProcessor.Configuration
import com.geniusscansdk.core.ScanProcessor.CurvatureCorrection
import com.geniusscansdk.core.ScanProcessor.Enhancement
import com.geniusscansdk.core.ScanProcessor.OutputConfiguration
import com.geniusscansdk.core.ScanProcessor.PerspectiveCorrection
import com.geniusscansdk.core.ScanProcessor.Result
import com.geniusscansdk.core.ScanProcessor.Rotation
import com.lexmark.lpm_scan.model.Page

import java.io.File;
import java.io.IOException;


class PageProcessor {
    @Throws(LicenseException::class, ProcessingException::class)
    fun processPage(context: Context, page: Page) {
        val configuration: Configuration<File> = Configuration(
            if (page.getQuadrangle() == null) PerspectiveCorrection.automatic() else PerspectiveCorrection.withQuadrangle(
                page.getQuadrangle()
            ),
            CurvatureCorrection.create(page.isDistortionCorrectionEnabled()),
            if (page.getFilterType() == null) Enhancement.automatic() else Enhancement.withFilter(page.getFilterType()),
            if (page.isAutomaticallyOriented()) Rotation.none() else Rotation.automatic(),
            OutputConfiguration.file(context.getExternalFilesDir(null))
        )
        val result: Result<File> = ScanProcessor(context).process(page.getOriginalImage(), configuration)
        var appliedQuadrangle: Quadrangle = result.appliedQuadrangle
        val appliedRotation: RotationAngle = result.appliedRotation
        if (appliedRotation != RotationAngle.ROTATION_0) {
            appliedQuadrangle = try {
                val imagePath: String = page.getOriginalImage().getAbsolutePath()
                GeniusScanSDK.rotateImage(imagePath, imagePath, appliedRotation)
                appliedQuadrangle.rotate(appliedRotation)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        page.setQuadrangle(appliedQuadrangle)
        page.setFilterType(result.appliedFilter)
        if (!page.isAutomaticallyOriented()) {
            page.setAutomaticallyOriented(true)
        }
        page.setEnhancedImage(result.output)
    }
}