package com.example.restaurantapp.authentication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurantapp.database.User
import com.example.restaurantapp.R
import com.example.restaurantapp.database.AppDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var enterButton: MaterialButton
    private lateinit var registerButton: MaterialButton
    private lateinit var loginTextField: TextInputEditText
    private lateinit var passwordTextField: TextInputEditText
    private lateinit var loginTextLayout: TextInputLayout
    private lateinit var passwordTextLayout: TextInputLayout
    private lateinit var viewModel: AuthViewModel
    private lateinit var loginTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_auth_activity)
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("alluser", AppDatabase.getDatabase(this@AuthActivity).userDao().getAllUser().toString())
            val isAdminExist =
                AppDatabase.getDatabase(this@AuthActivity).userDao().getUserByUsername("admin")
            if (isAdminExist == null) {
                AppDatabase.getDatabase(this@AuthActivity).userDao().insertUser(
                    User(
                        id = -100L,
                        name = "Admin panel",
                        username = "admin",
                        password = "admin",
                        isAdmin = true
                    )
                )
            }
        }


        val preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        enterButton = findViewById(R.id.enter_button)
        registerButton = findViewById(R.id.register_button)
        loginTextField = findViewById(R.id.login_text)
        passwordTextField = findViewById(R.id.password_text)
        loginTitle = findViewById(R.id.title_login)
        loginTextLayout = findViewById(R.id.login)
        passwordTextLayout = findViewById(R.id.password)

        viewModel = AuthViewModel()


        enterButton.setOnClickListener{
            val username = loginTextField.text.toString()
            val password = passwordTextField.text.toString()
            if (registerButton.visibility == View.GONE) {
                val user = User(
                    username = username,
                    password = password
                )
                Log.i("user111", user.toString())
                viewModel.register(user, this, preferences)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.login(username, password, this@AuthActivity, preferences)
                }
            }
        }

        registerButton.setOnClickListener{
            enterButton.text = "Регистрация"
            registerButton.visibility = View.GONE
            loginTitle.text = "Зарегистрируйтесь"
        }
    }
}