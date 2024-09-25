package com.example.gps_tracker

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener


fun Activity.showDialog(
    message : String,
    positiveButtonText : String,
    onPositiveClickListner : OnClickListener ,
    negativeButtonText : String?,
    onNegativeClickListner : OnClickListener?

) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
    builder.setPositiveButton(positiveButtonText , onPositiveClickListner)
    builder.setNegativeButton(negativeButtonText , onNegativeClickListner)
    builder.show()

}