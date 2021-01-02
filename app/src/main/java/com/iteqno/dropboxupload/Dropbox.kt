package com.iteqno.dropboxupload

import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.Metadata
import com.dropbox.core.v2.files.UploadErrorException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException


class Dropbox {

    private var client: DbxClientV2
    private val ACCESS_TOKEN = ""
    private val DIRECTORY = "DBXAPP"
    private lateinit var metadata: Metadata

    init {

        val config = DbxRequestConfig.newBuilder("DBX-APP")
            .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
            .build()

        // Individual account
        client = DbxClientV2(config, ACCESS_TOKEN)

        // Business account
        // client = DbxTeamClientV2(config, ACCESS_TOKEN).asMember("dbmid:XXX")
    }

    fun getCient() = client

    fun upload(file: File): Metadata {
        try {
            FileInputStream(file.absolutePath).use { `in` ->
                metadata = client.files().uploadBuilder("/$DIRECTORY/${file.name}")
                    .uploadAndFinish(`in`)
            }

            return metadata
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: UploadErrorException) {
            e.printStackTrace()
        } catch (e: DbxException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return metadata
    }

}