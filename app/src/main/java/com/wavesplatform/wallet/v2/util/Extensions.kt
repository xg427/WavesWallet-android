package com.wavesplatform.wallet.v2.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.wavesplatform.wallet.R
import pers.victor.ext.dp2px
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by anonymous on 13.09.17.
 */
fun Context.isNetworkConnection(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
    if (this != null) f(this)
}

fun String?.getAge(): String {
    if (this.isNullOrEmpty()) return ""

    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()

    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    dob.time = sdf.parse(this)

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    val ageInt = age

    return ageInt.toString()
}


fun ImageView.loadImage(url: String?, centerCrop: Boolean = true, name: String? = "") {
    this.post({
        val options = RequestOptions()
                .override(this.width, this.height)

        if (!name.isNullOrEmpty()) {
            var placeholder = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            var canvas = android.graphics.Canvas(placeholder)
            val rectPaint = Paint()
            val textPaint = Paint()

            rectPaint.color = android.support.v4.content.ContextCompat.getColor(this.context, R.color.blockchain_blue)
            textPaint.color = android.support.v4.content.ContextCompat.getColor(this.context, android.R.color.white)
            textPaint.textSize = pers.victor.ext.sp2px(50).toFloat()
            textPaint.textAlign = android.graphics.Paint.Align.CENTER


            canvas.drawRoundRect(0f, 0f,
                    height.toFloat(), width.toFloat(), dp2px(8).toFloat(), dp2px(8).toFloat(), rectPaint)

            canvas.drawText(name?.substring(0,1), (canvas.width / 2).toFloat(),
                    ((canvas.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)), textPaint)


            options.error(BitmapDrawable(resources, placeholder))
            options.placeholder(BitmapDrawable(resources, placeholder))
        }

        if (centerCrop) options.transform(CenterCrop())

        Glide.with(this)
                .asBitmap()
                .load(url)
                .apply(options)
                .into(this)
    })
}

fun ImageView.loadImage(file: File?, centerCrop: Boolean = true, name: String? = "", deleteImmediately: Boolean = true) {
    this.post({
        val options = RequestOptions()
                .override(this.width, this.height)

        if (!name.isNullOrEmpty()) {
            var placeholder = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            var canvas = android.graphics.Canvas(placeholder)
            val rectPaint = Paint()
            val textPaint = Paint()

            rectPaint.color = android.support.v4.content.ContextCompat.getColor(this.context, R.color.blockchain_blue)
            textPaint.color = android.support.v4.content.ContextCompat.getColor(this.context, android.R.color.white)
            textPaint.textSize = pers.victor.ext.sp2px(50).toFloat()
            textPaint.textAlign = android.graphics.Paint.Align.CENTER


            canvas.drawRoundRect(0f, 0f,
                    height.toFloat(), width.toFloat(), dp2px(8).toFloat(), dp2px(8).toFloat(), rectPaint)

            canvas.drawText(name?.substring(0,1), (canvas.width / 2).toFloat(),
                    ((canvas.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)), textPaint)


            options.error(BitmapDrawable(resources, placeholder))
            options.placeholder(BitmapDrawable(resources, placeholder))
        }

        if (centerCrop) options.transform(CenterCrop())

        Glide.with(this)
                .load(file)
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        this@loadImage.setImageDrawable(resource)
                        if (deleteImmediately) file?.delete()
                        return true
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return true
                    }

                })
                .into(this)
    })
}


@SuppressWarnings("deprecation")
fun Context.fromHtml(source: String): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
    } else {
        return Html.fromHtml(source)
    }
}

/**
 * Extensions for simpler launching of Activities
 */

inline fun <reified T : Any> Activity.launchActivity(
        requestCode: Int = -1,
        clear: Boolean = false,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {

    var intent = newIntent<T>(this)
    if (options != null) intent.putExtras(options)

    if (clear) {
        intent = newClearIntent<T>(this)
    }

    intent.init()
    if (requestCode != -1) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> Fragment.launchActivity(
        requestCode: Int = -1,
        clear: Boolean = false,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {

    var intent = newIntent<T>(activity!!)
    if (options != null) intent.putExtras(options)

    if (clear) {
        intent = newClearIntent<T>(activity!!)
    }

    intent.init()
    if (requestCode != -1) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> Context.launchActivity(
        options: Bundle? = null,
        clear: Boolean = false,
        noinline init: Intent.() -> Unit = {}) {

    var intent = newIntent<T>(this)
    if (options != null) intent.putExtras(options)

    if (clear) {
        intent = newClearIntent<T>(this)
    }


    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)

inline fun <reified T : Any> newClearIntent(context: Context): Intent {
    var intent = Intent(context, T::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return intent
}

fun View.setMargins(
        left: Int? = null,
        top: Int? = null,
        right: Int? = null,
        bottom: Int? = null
) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
            ?: return

    lp.setMargins(
            left ?: lp.leftMargin,
            top ?: lp.topMargin,
            right ?: lp.rightMargin,
            bottom ?: lp.rightMargin
    )

    layoutParams = lp
}
