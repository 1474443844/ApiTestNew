package cn.wantu.apitest.data

import kotlinx.serialization.Serializable

@Serializable
data class ApiTestConfig(
    val versionCode: Long,
    val version: String,
    val content: String,
    val download: String,
    val skip: Int
)
