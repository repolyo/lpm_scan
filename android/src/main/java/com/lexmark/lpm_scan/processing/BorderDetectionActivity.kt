package com.lexmark.lpm_scan.processing

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.geniusscansdk.core.Quadrangle
import com.geniusscansdk.ui.BorderDetectionImageView
import com.geniusscansdk.ui.MagnifierBorderDetectionListener
import com.geniusscansdk.ui.MagnifierView
import com.lexmark.lpm_scan.R
import com.lexmark.lpm_scan.enhance.ImageProcessingActivity
import com.lexmark.lpm_scan.model.Page


class BorderDetectionActivity : AppCompatActivity() {
    private lateinit var page: Page
    private lateinit var progressDialog: ProgressDialog
    private lateinit var imageView: BorderDetectionImageView

    private var magnifierView: MagnifierView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_border_detection)
        imageView = findViewById(R.id.image_view)
        imageView.setOverlayColorResource(R.color.blue)
        magnifierView = findViewById(R.id.magnifier_view)
        imageView.setListener(MagnifierBorderDetectionListener(magnifierView))
        page = intent.getParcelableExtra(EXTRA_PAGE)!!
    }

    override fun onResume() {
        super.onResume()
        val filename: String = page.originalImage.absolutePath
        val opts = BitmapFactory.Options()
        opts.inSampleSize = 2
        val bitmap = BitmapFactory.decodeFile(filename, opts)
        imageView.setImageBitmap(bitmap)
        magnifierView!!.bitmap = bitmap
        progressDialog = ProgressDialog(this)
        progressDialog.show()
        object : AnalyzeAsyncTask(this) {
            override fun onPostExecute(quadrangle: Quadrangle?) {
                super.onPostExecute(quadrangle)
                progressDialog.dismiss()
                addQuadrangleToView(quadrangle)
            }
        }.execute(page)
    }

    fun addQuadrangleToView(quadrangle: Quadrangle?) {
        imageView.quad = quadrangle
        imageView.invalidate()
    }

    fun setQuadrangleToFullImage(view: View?) {
        imageView.quad = Quadrangle.createFullQuadrangle()
        imageView.invalidate()
    }

    fun select(view: View?) {
        page.quadrangle = imageView.quad
        // TODO: update DocumentManager to reflect changes done here.]
        finish()
    }

    companion object {
        @Suppress("unused")
        private val TAG = BorderDetectionActivity::class.java.simpleName
        const val EXTRA_PAGE = "page"
    }
}