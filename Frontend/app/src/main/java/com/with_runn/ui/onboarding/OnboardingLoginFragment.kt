package com.with_runn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.with_runn.R
import com.with_runn.databinding.FragmentOnboardingLoginBinding
import kotlinx.coroutines.launch
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import org.json.JSONObject

class OnboardingLoginFragment : Fragment() {

    private var _binding: FragmentOnboardingLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private val credentialManager by lazy { CredentialManager.create(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingLoginBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.naverLoginButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val googleCred = signInWithGoogle()
                    val (email, id) = parseEmailAndId(googleCred.idToken)
                    authViewModel.loginWithGoogle(email, id, googleCred.idToken)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginState.collect { result ->
                    if (result != null) {
                        // 로그인 성공
                        findNavController().navigate(
                            R.id.action_onboardingLoginFragment_to_onboardingProfileFragment
                        )
                    } else {
                        // 로그인 실패
                    }
                }
            }
        }


        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingLoginFragment_to_mypageOptionFragment) //todo: test코드: 나중에 삭제
        }
    }

    private suspend fun signInWithGoogle(): GoogleIdTokenCredential {
        val option = GetSignInWithGoogleOption.Builder(getString(R.string.server_client_id)).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val result = credentialManager.getCredential(requireActivity(), request)
        val cred = result.credential as CustomCredential
        return GoogleIdTokenCredential.createFrom(cred.data)
    }

    private fun parseEmailAndId(idToken: String): Pair<String, String> {
        val payload = idToken.substringAfter('.').substringBeforeLast('.')
        val decoded = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val obj = JSONObject(String(decoded))
        val email = requireNotNull(obj.optString("email", null)) { "Google 토큰에 email 없음" }
        val id = email.substringBefore('@')
        return email to id
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
