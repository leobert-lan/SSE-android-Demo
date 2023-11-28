package osp.leobert.android.ssedemo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.a).setOnClickListener {
            OkhttpTestCase.testOkhttpGet()
        }

        findViewById<View>(R.id.b).setOnClickListener {
            CoroutineScope(Job()).launch {
                RetrofitSSECase.api.word()
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        Log.d("lmsg", "$it")
                    }
                    .flowOn(Dispatchers.Main)
                    .collect()
            }
        }

        findViewById<View>(R.id.c).setOnClickListener {
            MainScope().launch {
                val obj = CoroutineScope(Job()).async {
                        RetrofitSSECase.api.test()
                    }.await()
                Log.d("lmsg", "$obj")
            }
        }
    }

}
