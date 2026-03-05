package hcmute.edu.vn.TokTick_23110172

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        val tvAppName = findViewById<TextView>(R.id.tvAppName)

        // Thực hiện Animation cho Logo: Phóng to (scale) và Mờ dần (alpha) với hiệu ứng Overshoot (nảy lên)
        ivLogo.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(OvershootInterpolator())
            .start()

        // Thực hiện Animation cho Tên App: Trượt từ dưới lên và Mờ dần sau khi logo bắt đầu xuất hiện
        tvAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(400)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Sử dụng Coroutines để trì hoãn việc chuyển màn hình mà không chặn UI Thread
        lifecycleScope.launch {
            delay(2000)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            
            // Chuyển cảnh mượt mà
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            
            // Kết thúc SplashActivity để người dùng không quay lại được bằng nút Back
            finish()
        }
    }
}