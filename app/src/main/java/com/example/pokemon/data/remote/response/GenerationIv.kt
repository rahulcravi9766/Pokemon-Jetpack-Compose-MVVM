package com.example.pokemon.data.remote.response


import com.google.gson.annotations.SerializedName

data class GenerationIv(
    @SerializedName("diamond-pearl")
    val diamondPearl: DiamondPearl?,
    @SerializedName("heartgold-soulsilver")
    val heartgoldSoulsilver: HeartgoldSoulsilver?,
    @SerializedName("platinum")
    val platinum: Platinum?
)