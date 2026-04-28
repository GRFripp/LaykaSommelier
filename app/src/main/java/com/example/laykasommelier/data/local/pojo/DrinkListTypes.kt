package com.example.laykasommelier.data.local.pojo

import android.media.Image

data class DrinkListTypes(
    val drinkListType: String,
    val drinkListTypeCount: Int = 0,
   // val drinkListTypeImageName: String = "ic_launcher_background.xml"
    // Необходимо будет добавить в БД поля с изображениями,  когда они будут браться с сервера
)
