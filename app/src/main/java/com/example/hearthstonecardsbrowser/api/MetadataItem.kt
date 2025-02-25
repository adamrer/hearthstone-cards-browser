package com.example.hearthstonecardsbrowser.api

import java.io.Serializable

data class MetadataItem(
    val id: Int = 0,
    val name: String = "",
    val slug: String = ""
) : Serializable