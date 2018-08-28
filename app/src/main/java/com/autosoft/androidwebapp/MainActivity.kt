package com.autosoft.androidwebapp

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error.view.*

class MainActivity : BaseActivity() {

    private var error = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        web_view.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                showError()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                showWebView()
                loading = false
            }
        }

        web_view.webChromeClient = MyWebChromeClient(this)

        web_view.settings.allowFileAccess = true
        web_view.settings.javaScriptEnabled = true

        web_view.setDownloadListener { url, _, _, _, _ ->
            downloadFile(url)
        }

        val currentUrl = savedInstanceState?.getString(CURRENT_URL)

        if (currentUrl != null) {
            web_view.loadUrl(currentUrl)
        } else {
            web_view.loadUrl(LOAD_URL)
        }

        error_view.btn_retry.setOnClickListener {
            error = false
            web_view.reload()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(CURRENT_URL, web_view.url)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (RC_STORAGE_PERMISSION == requestCode) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PERMISSION_GRANTED) {
                val pendingDownload = intent.getStringExtra(PENDING_DOWNLOAD)
                if (pendingDownload != null) {
                    downloadFile(pendingDownload)
                    intent.removeExtra(PENDING_DOWNLOAD)
                }
            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun showError() {
        error = true
        error_view.visibility = View.VISIBLE
        web_view.visibility = View.GONE
    }

    private fun showWebView() {
        if (!error) {
            error_view.visibility = View.GONE
            web_view.visibility = View.VISIBLE
        }
    }

    private fun downloadFile(url: String) {
        if (hasStoragePermission()) {

            val filename = url.substring(url.lastIndexOf('/') + 1)

            val request = DownloadManager.Request(Uri.parse(url))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, filename)
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)

            toast(getString(R.string.downloading))

        } else {
            intent.putExtra(PENDING_DOWNLOAD, url)
            requestStoragePermission()
        }
    }

    private fun hasStoragePermission() = checkSelfPermission(this,
            WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_required))
                    .setMessage(getString(R.string.storage_permission_rationale))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),
                                RC_STORAGE_PERMISSION)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
        } else {
            requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), RC_STORAGE_PERMISSION)
        }
    }
}
