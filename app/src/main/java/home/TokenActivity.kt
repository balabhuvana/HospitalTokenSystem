package home

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.hospital.tokensystem.LoginFragment
import com.hospital.tokensystem.R

import kotlinx.android.synthetic.main.activity_token.*

class TokenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)


        supportFragmentManager.beginTransaction()
                .replace(R.id.tokenFrameLayout, TokenFragment())
                .commit()

    }

}
