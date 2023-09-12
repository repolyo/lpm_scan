package com.lexmark.lpm_scan.enhance

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options;
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.geniusscansdk.core.FilterType
import com.geniusscansdk.core.RotationAngle
import com.lexmark.lpm_scan.R
import com.lexmark.lpm_scan.model.DocumentManager
import com.lexmark.lpm_scan.model.Page
import com.lexmark.lpm_scan.processing.BorderDetectionActivity
import io.flutter.embedding.android.FlutterActivity
import java.io.File


class ImageProcessingActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var distortionCorrectionButton: ImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var page: Page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = intent.getParcelableExtra(EXTRA_PAGE)!!
        setContentView(R.layout.activity_image_processing)
        imageView = findViewById(R.id.image_view)
        distortionCorrectionButton = findViewById(R.id.distortion_correction_button)
    }

    override fun onResume() {
        super.onResume()
        updateDistortionCorrectionButton()
        displayScan(page.originalImage)
        progressDialog = ProgressDialog(this)
        progressDialog.show()
        enhance()
    }

    private fun updateDistortionCorrectionButton() {
        distortionCorrectionButton.setImageResource(if (page.isDistortionCorrectionEnabled) R.drawable.straightened_distortion_grid else R.drawable.distortion_grid)
    }

    private fun endEnhance() {
        displayEnhancedScan()
        progressDialog.dismiss()
    }

    private fun endRotate() {
        displayEnhancedScan()
        progressDialog.dismiss()
    }

    fun changeEnhancement(view: View?) {
        AlertDialog.Builder(this)
            .setTitle(com.lexmark.lpm_scan.R.string.enhancement_dialog_title)
            .setItems(
                arrayOf<CharSequence>(
                    getString(R.string.image_type_none),
                    getString(R.string.image_type_color),
                    getString(R.string.image_type_whiteboard),
                    getString(R.string.image_type_black_white)
                ), DialogInterface.OnClickListener { dialog, which ->
                    val filterType = arrayOf(
                        FilterType.NONE,
                        FilterType.PHOTO,
                        FilterType.COLOR,
                        FilterType.BLACK_WHITE
                    )[which]
                    page.filterType = filterType
                    progressDialog.show()
                    enhance()
                }).show()
    }

    fun toggleDistortionCorrection(view: View?) {
        page.isDistortionCorrectionEnabled = !page.isDistortionCorrectionEnabled
        updateDistortionCorrectionButton()
        progressDialog.show()
        enhance()
    }

    private fun rotate(angle: RotationAngle) {
        progressDialog.show()
        object : RotateAsyncTask(this, angle) {
            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                endRotate()
            }
        }.execute(page)
    }

    private fun enhance() {
        object : EnhanceAsyncTask(this) {
            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                endEnhance()
            }
        }.execute(page)
    }

    private fun displayEnhancedScan() {
        displayScan(page.enhancedImage)
    }

    private fun displayScan(imageFile: File) {
        val opts = Options()
        opts.inSampleSize = 2
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, opts)
        imageView.setImageBitmap(bitmap)
        imageView.invalidate()
    }

    fun rotateLeft(view: View?) {
        rotate(RotationAngle.ROTATION_90_CCW)
    }

    fun rotateRight(view: View?) {
        rotate(RotationAngle.ROTATION_90_CW)
    }

    fun crop(view: View?) {
        val intent = Intent(this@ImageProcessingActivity, BorderDetectionActivity::class.java)
        intent.putExtra(BorderDetectionActivity.EXTRA_PAGE, page)
        startActivity(intent)
    }

    fun savePage(view: View?) {
        val intent = Intent(this, FlutterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent)
        finish()
    }

    companion object {
        @Suppress("unused")
        private val TAG = ImageProcessingActivity::class.java.simpleName
        const val EXTRA_PAGE = "page"
    }
}