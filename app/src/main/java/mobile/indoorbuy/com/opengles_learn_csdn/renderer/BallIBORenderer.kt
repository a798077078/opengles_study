package mobile.indoorbuy.com.opengles_learn_csdn.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import mobile.indoorbuy.com.opengles_learn_csdn.R
import mobile.indoorbuy.com.opengles_learn_csdn.common.*
import mobile.indoorbuy.com.opengles_learn_csdn.shape.Ball
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import mobile.indoorbuy.com.opengles_learn_csdn.common.CameraViewport



/**
 * Created by BMW on 2018/6/12.
 * 立方体
 */
class BallIBORenderer(private val context:Context):GLSurfaceView.Renderer{

    var mResult = FloatArray(16)

    private var ball:Ball? = null

    private var targetViewport:CameraViewport? = null

    //private var angle = 0.1f
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        //Matrix.rotateM(mMVPMatrix,0,angle,0f,0f,1f)
        ball!!.setMatrix(mResult)
        ball!!.onDraw()
    }

    private var ratio: Float = 0f

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0,width,height)
        ratio = width.toFloat() / height.toFloat()
        //Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1f,1f,3f,20f)

//        // 全景球
//        MatrixHelper.perspectiveM(mProjectMatrix, 70f, width as Float / height as Float, 0.01f, 1000f)
//        Matrix.setLookAtM(mViewMatrix, 0,
//                0f, 0f, 2.8f,
//                0f, 0f, 0f,
//                0f, 1f, 0f)
//// 透视
//        MatrixHelper.perspectiveM(mProjectMatrix, 70f, width as Float / height as Float, 0.01f, 1000f)
//        Matrix.setLookAtM(mViewMatrix, 0,
//                0f, 0f, 1.0f,
//                0f, 0f, 0f,
//                0f, 1f, 0f)
//// 小行星
//        MatrixHelper.perspectiveM(mProjectMatrix, 150f, width as Float / height as Float, 0.01f, 1000f)
//        Matrix.setLookAtM(mViewMatrix, 0,
//                0f, 0f, 1.0f,
//                0f, 0f, 0f,
//                0f, 1f, 0f)

        if (targetViewport == null)
            targetViewport = CameraViewport()

        targetViewport!!.overlook = CameraViewport.CRYSTAL_OVERLOOK
        targetViewport!!.setCameraVector(0f, 0f, 2.8f)
        targetViewport!!.setTargetViewVector(0f, 0f, 0.0f)
        targetViewport!!.setCameraUpVector(0f, 1.0f, 0.0f)

        mResult = currentMatrix(ratio)
        ball!!.setMatrix(mResult)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        ball = Ball(context)
        ball!!.create()
    }

    fun currentMatrix(ratio:Float):FloatArray{
        val mProjectMatrix = FloatArray(16)
        val mViewMatrix = FloatArray(16)
        val mMVPMatrix = FloatArray(16)
        MatrixHelper.perspectiveM(mProjectMatrix,targetViewport!!.overlook,ratio,0.01f,1000f)
        Matrix.setLookAtM(mViewMatrix, 0,
                targetViewport!!.cx,  targetViewport!!.cy,  targetViewport!!.cz,
                targetViewport!!.tx,  targetViewport!!.ty,  targetViewport!!.tz,
                targetViewport!!.upx, targetViewport!!.upy, targetViewport!!.upz)
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)
        return mMVPMatrix
    }

    fun handleTouchUp(x: Float, y: Float) {
        ball!!.handleTouchUp(x, y)
    }

    fun handleTouchDrag(x: Float, y: Float) {
        ball!!.handleTouchDrag(x, y)
    }

    fun handleTouchDown(x: Float, y: Float) {
        ball!!.handleTouchDown(x, y)
    }

    private var currentControlMode = Content.RENDER_MODE_CRYSTAL //默认状态是全景球
    fun handleDoubleClick(){
        when(currentControlMode){
            Content.RENDER_MODE_CRYSTAL->{
                targetViewport!!.overlook = CameraViewport.PERSPECTIVE_OVERLOOK
                // 其实这些场景数据可用直接放在CameraViewport
                targetViewport!!.setCameraVector(0f, 0f, 1.0f)
                targetViewport!!.setTargetViewVector(0f, 0f, 0.0f)
                targetViewport!!.setCameraUpVector(0f, 1.0f, 0.0f)
                currentControlMode = Content.RENDER_MODE_PERSPECTIVE
                Log.e("weiwei","当前全景球，切换为普通透视")
            }
            Content.RENDER_MODE_PERSPECTIVE->{
                targetViewport!!.overlook = CameraViewport.PLANET_OVERLOOK
                targetViewport!!.setCameraVector(0f, 0f, 1.0f)
                targetViewport!!.setTargetViewVector(0f, 0f, 0.0f)
                targetViewport!!.setCameraUpVector(0f, 1.0f, 0.0f)
                currentControlMode = Content.RENDER_MODE_PLANET
                Log.e("weiwei","当前普通透视，切换为小行星")
            }
            Content.RENDER_MODE_PLANET->{
                targetViewport!!.overlook = CameraViewport.CRYSTAL_OVERLOOK
                targetViewport!!.setCameraVector(0f, 0f, 2.8f)
                targetViewport!!.setTargetViewVector(0f, 0f, 0.0f)
                targetViewport!!.setCameraUpVector(0f, 1.0f, 0.0f)
                currentControlMode = Content.RENDER_MODE_CRYSTAL
                Log.e("weiwei","当前小行星，切换为全景球")
            }
        }

        mResult = currentMatrix(ratio)
    }

}