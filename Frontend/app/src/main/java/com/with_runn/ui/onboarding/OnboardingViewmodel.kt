package com.with_runn.ui.onboarding

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewmodel : ViewModel(){
    // 예시: 프래그먼트 간 공유할 변수
    private val _townId = MutableLiveData<Int>()
    private val _cityId = MutableLiveData<Int>()
    private val _name = MutableLiveData<String>()
    private val _gender = MutableLiveData<String>()
    private val _birth = MutableLiveData<String>()
    private val _breed = MutableLiveData<String>()
    private val _size = MutableLiveData<String>()
    private val _characters = MutableLiveData<List<String>>()
    private val _style = MutableLiveData<List<String>>()
    private val _introduction = MutableLiveData<String>()
    private val _profileImg = MutableLiveData<Uri>()


    val townId: LiveData<Int> = _townId
    val cityId: LiveData<Int> =_cityId
    val name: LiveData<String> =_name
    val gender: LiveData<String> =_gender
    val birth: LiveData<String> =_birth
    val breed: LiveData<String> =_breed
    val size: LiveData<String> =_size
    val characters: LiveData<List<String>> =_characters
    val style: LiveData<List<String>> =_style
    val introduction: LiveData<String> =_introduction
    val profileImg: LiveData<Uri> =_profileImg

    private var isDefaultValuesSet = false
    private var isCharactersValuesSet = false
    private var isWalkingStyleValuesSet = false
    private var isProfileImgSet = false

    fun setDefaultValues(name: String,gender: String,birth: String,breed: String,size: String,introduction: String) {
        _name.value = name
        _gender.value = gender
        _birth.value = birth
        _breed.value = breed
        _size.value = size
        _introduction.value = introduction
        isDefaultValuesSet = true
    }

    fun setProfileImg(img: Uri){
        _profileImg.value = img
        isProfileImgSet = true
    }

    fun setCharacters(characters: List<String>) {
        _characters.value = characters
        isCharactersValuesSet = true
    }

    fun setStyle(style: List<String>) {
        _style.value = style
        isWalkingStyleValuesSet = true
    }

    fun hasDefaultBeenSet(): Boolean {
        return isDefaultValuesSet
    }
    fun hasCharactersBeenSet(): Boolean {
        return isCharactersValuesSet
    }
    fun hasStyleBeenSet(): Boolean {
        return isWalkingStyleValuesSet
    }
}