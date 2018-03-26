package com.hospital.tokensystem


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import home.TokenActivity


class LoginFragment : Fragment(), LoginView {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var etUserName: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var progressBar: ProgressBar? = null
    private var mLoginPresenterImpl: LoginPresenterImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etUserName = view!!.findViewById(R.id.username) as EditText
        etPassword = view.findViewById(R.id.password) as EditText
        btnLogin = view.findViewById(R.id.button) as Button
        progressBar = view.findViewById(R.id.progress) as ProgressBar
        mLoginPresenterImpl = LoginPresenterImpl(this)


        btnLogin!!.setOnClickListener { mLoginPresenterImpl!!.validateLogin(etUserName!!.text.toString(), etPassword!!.text.toString()) }


    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    override fun showProgress() {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar!!.visibility = View.INVISIBLE
    }

    override fun userNameError() {
        etUserName!!.error = getString(R.string.username_error)
    }

    override fun passwordError() {
        etPassword!!.error = getString(R.string.password_error)
    }

    override fun goToHomeFragment() {
        activity.startActivity(Intent(activity, TokenActivity::class.java))
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
