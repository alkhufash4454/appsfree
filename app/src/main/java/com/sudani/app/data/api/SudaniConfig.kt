package com.sudani.app.data.api

/**
 * إعدادات الخفاش الموحدة 🦇
 * تم نقل كافة المسارات والأكواد من ملف Python لضمان "زيرو فشل"
 */
object SudaniConfig {
    const val BASE_URL = "https://mapp.sudani.sd/prod/"
    
    [span_1](start_span)// المسارات الحقيقية (Paths)[span_1](end_span)
    const val GENERATE_OTP_PATH = "sc-onboarding/api/customer/generate-otp"
    const val VERIFY_OTP_PATH = "sc-onboarding/api/customer/verify-otp"
    const val ONBOARDING_PATH = "sc-onboarding/api/customer/onboarding"
    const val DASHBOARD_PATH = "sc-dashboard/api/get-dashboard"
    const val CLAIM_REWARD_PATH = "gamification-service/api/reward/claim"
    const val REDEEM_OFFER_PATH = "offer-service/api/loyalty/redeem-offer-v2"
    const val SUBSCRIBE_PATH = "offer-service/api/catalogue/subscribe"

    [span_2](start_span)// ثوابت الجهاز والترخيص (Headers Base)[span_2](end_span)
    const val DEVICE_ID = "ginkgo_xiaomi_ginkgo_Redmi Note 8_Xiaomi_qcom_PKQ1.190616.001"
    const val TENANT = "tec_sudatel"
    const val CHANNEL = "sc_app"
    const val PLATFORM = "android"

    [span_3](start_span)// أكواد عروض النقاط (Redeem)[span_3](end_span)
    const val OFFER_300MB_ID = "320196"
    const val OFFER_300MB_PRODUCT_ID = "2002"
    const val OFFER_300MB_POINTS = "70"

    const val OFFER_1GB_ID = "320197"
    const val OFFER_1GB_PRODUCT_ID = "2023"
    const val OFFER_1GB_POINTS = "100"

    [span_4](start_span)// قائمة الخدمات الـ 15 (الاشتراك بالرصيد)[span_4](end_span)
    val SERVICE_OFFERINGS = listOf(
        // باقات مميزة (Mixed)
        Triple("243586", "Ahla Youm", "1570"),
        Triple("237602", "Raih Balak", "1699"),
        Triple("238884", "Raih Balak Max", "1762"),
        Triple("240891", "Raih Balak", "1612"),
        Triple("238883", "Raih Balak", "1763"),
        
        // باقات مكالمات (Voice)
        Triple("231232", "Khalli Anak", "1606"),
        Triple("243979", "Khalli Anak", "1603"),
        Triple("243980", "Khalli Anak", "1607"),
        
        // باقات إنترنت (Data)
        Triple("244340", "5GB", "2001"),
        Triple("244341", "10GB", "2002"),
        Triple("244441", "5GB", "2003"),
        Triple("244448", "500MB", "2004"),
        Triple("239806", "200MB", "2005"),
        Triple("244449", "1.5GB", "2006"),
        Triple("239706", "1GB", "2007")
    )
}
