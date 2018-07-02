package mobile.indoorbuy.com.opengles_learn_csdn

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_mian_gl.*
import mobile.indoorbuy.com.opengles_learn_csdn.renderer.*



class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mian_gl)
        requestPermissions()

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        with(activityManager.run {
            deviceConfigurationInfo.reqGlEsVersion >= 0x20000
        }){
            if(this)
                surface.setEGLContextClientVersion(2)
        }
        //surface.setRenderer(CameraRenderer(this,surface))
        //surface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        surface.setRenderer(BallIBORenderer(this))
    }



    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun requestPermissions(){
        RxPermissions(this)
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            // 用户已经同意该权限
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        }
                        else -> {
                            // 用户拒绝了该权限，并且选中『不再询问』
                        }
                    }
                }
    }
}
