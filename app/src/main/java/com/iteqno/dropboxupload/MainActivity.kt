package com.iteqno.dropboxupload

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.dropbox.core.v2.sharing.SharedLinkMetadata
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val dbx = Dropbox()
    private val PICK_IMAGE = 1001
    private var uploadedImage: MutableLiveData<SharedLinkMetadata> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnFile.setOnClickListener { selectFile() }

        uploadedImage.observe(this, {
            Glide.with(this)
                .load(it.url+"&raw=1")
                .into(imageView)
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
        })
    }

    private fun getFile() {
        GlobalScope.launch {
            val preview =
                dbx.getCient().files().getThumbnail("")
            val shared = dbx.getCient().sharing().listSharedLinks()

            Log.d("XPreview", preview.result.toStringMultiline())
        }
    }

    private fun selectFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                doUpload(data)
            }
        }
    }

    private fun shareFile(path: String): SharedLinkMetadata{
        return dbx.getCient().sharing().createSharedLinkWithSettings(path)
    }

    private fun doUpload(data: Intent) {
        try {
            data.data?.let { uri ->
                val file = FileUtils.getFileFromUri(this, uri)
                file.let { image ->
                    GlobalScope.launch {
                        val meta = dbx.upload(image)
                        uploadedImage.postValue(shareFile(meta.pathDisplay))
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

}