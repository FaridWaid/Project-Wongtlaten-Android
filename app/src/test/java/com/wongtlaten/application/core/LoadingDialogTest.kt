package com.wongtlaten.application.core

import android.app.Activity
import android.app.AlertDialog
import com.wongtlaten.application.R
import junit.framework.TestCase.assertEquals
import org.junit.Test

internal class LoadingDialogTest {

    private lateinit var mActivity: Activity
    private lateinit var isDialog: AlertDialog

    @Test
    fun startDialog() {
        var isDialog: AlertDialog
        //Set View
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_bar, null)
        //set Dialog
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    @Test
    fun isDissmis() {
        isDialog.dismiss()
    }

    @Test
    fun convert() {

        val input: Float = 20F
        val output: Float
        val expected = 25F
        val fungsiConvert = LoadingDialog(mActivity)
        output = fungsiConvert.convert(input)

        assertEquals(expected, output, 0.1F)
    }

}