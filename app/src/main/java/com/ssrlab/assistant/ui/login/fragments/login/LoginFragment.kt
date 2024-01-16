package com.ssrlab.assistant.ui.login.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ssrlab.assistant.R
import com.ssrlab.assistant.databinding.FragmentLoginBinding
import com.ssrlab.assistant.ui.login.fragments.base.BaseLaunchFragment

class LoginFragment: BaseLaunchFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var fireAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(layoutInflater)

        fireAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setUpButtons()
        binding.loginMain.setOnClickListener { inputHelper.hideKeyboard(binding.root) }
    }

    private fun setUpButtons() {
        setUpLoginButton()
        setUpPasswordButton()
        setUpMovementButtons()
        setUpGoogleButton()

        binding.loginForgotText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPassword1Fragment)
        }
    }

    private fun setUpPasswordButton() {
        inputHelper.showPasswordAction(binding.loginPasswordInput, binding.loginPasswordShow)
    }

    private fun setUpMovementButtons() {
        val navController = findNavController()

        if (navController.previousBackStackEntry == null) binding.loginToRegister2.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }
        else {
            binding.loginToRegister2.setOnClickListener { navController.popBackStack() }
            binding.loginBack.apply {
                visibility = View.VISIBLE
                setOnClickListener { navController.popBackStack() }
            }
        }
    }

    private fun setUpGoogleButton() {
        binding.loginGoogleRipple.setOnClickListener {
            authClient.signIn(launchActivity, {
                launchActivity.intentNext()
            }, { msg, type ->
                inputHelper.handleErrorTypes(
                    message = msg,
                    type = type,
                    binding = binding
                )
            })
        }
    }

    /**
     * 1 - OK
     * 2 - Empty login
     * 3 - Empty password
     * 4 - Both empty
     */
    private fun setUpLoginButton() {
        binding.apply {
            loginButton.setOnClickListener {
                val login = loginEmailInput.text.toString()
                val password = loginPasswordInput.text.toString()

                inputHelper.hideKeyboard(binding.root)

                handleEmptyInput(login, password)
            }
        }
    }

    private fun handleEmptyInput(login: String, password: String) {
        inputHelper.checkSignEmptiness(binding.loginEmailInput, binding.loginPasswordInput) {
            when (it) {
                1 -> {
                    authClient.signIn(login, password, {
                        launchActivity.intentNext()
                    }, { msg, type ->
                        inputHelper.handleErrorTypes(
                            message = msg,
                            type = type,
                            textView1 = binding.loginEmailErrorTitle,
                            textView2 = binding.loginPasswordErrorTitle,
                            binding = binding
                        )
                    }, {
                        findNavController().navigate(R.id.action_loginFragment_to_confirmEmailFragment)
                    })
                }
                2 -> {
                    inputHelper.handleErrorTypes(
                        message = ContextCompat.getString(launchActivity, R.string.empty_field_error),
                        type = 1,
                        textView1 = binding.loginEmailErrorTitle,
                        binding = binding
                    )
                }
                3 -> {
                    inputHelper.handleErrorTypes(
                        message = ContextCompat.getString(launchActivity, R.string.empty_field_error),
                        type = 2,
                        textView2 = binding.loginPasswordErrorTitle,
                        binding = binding
                    )
                }
                4 -> {
                    inputHelper.handleErrorTypes(
                        message = ContextCompat.getString(launchActivity, R.string.empty_field_error),
                        type = 3,
                        textView1 = binding.loginEmailErrorTitle,
                        textView2 = binding.loginPasswordErrorTitle,
                        binding = binding
                    )
                }
            }
        }
    }
}