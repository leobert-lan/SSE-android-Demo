package osp.leobert.android.ssedemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.a).setOnClickListener {
            OkhttpTestCase.testOkhttpGet()
        }
    }

}
