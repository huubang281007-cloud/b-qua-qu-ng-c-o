package com.example.skipad

/** Danh sách app được hỗ trợ + các cụm từ "Bỏ qua quảng cáo" đa ngôn ngữ. */
object Targets {

    /** Mọi bản YouTube (chính thức, Music, Go, Kids, TV, Vanced, ReVanced...). */
    val YOUTUBE_PACKAGES = setOf(
        "com.google.android.youtube",
        "com.google.android.apps.youtube.music",
        "com.google.android.apps.youtube.mango",
        "com.google.android.apps.youtube.kids",
        "com.google.android.youtube.tv",
        "com.vanced.android.youtube",
        "com.vanced.android.apps.youtube.music",
        "app.revanced.android.youtube",
        "app.revanced.android.apps.youtube.music"
    )

    /** Trình duyệt (xem youtube.com trên web). */
    val BROWSER_PACKAGES = setOf(
        "com.android.chrome", "com.chrome.beta", "org.mozilla.firefox", "org.mozilla.fenix",
        "com.brave.browser", "com.microsoft.emmx", "com.sec.android.app.sbrowser",
        "com.opera.browser", "com.opera.mini.native", "com.vivaldi.browser",
        "com.kiwibrowser.browser", "com.duckduckgo.mobile.android", "com.coccoc.trinhduyet"
    )

    fun isYouTube(pkg: String) = pkg in YOUTUBE_PACKAGES
    fun isBrowser(pkg: String) = pkg in BROWSER_PACKAGES

    /** Cụm từ chắc chắn nói về quảng cáo (dùng cho cả app YouTube lẫn trình duyệt). */
    val STRONG_PHRASES = listOf(
        "skip ad", "skip ads", "bỏ qua quảng cáo", "bỏ qua qc",
        "saltar anuncio", "omitir anuncio", "passer l'annonce", "ignorer l'annonce",
        "anzeige überspringen", "werbung überspringen", "salta annuncio", "pular anúncio",
        "pular anuncio", "пропустить рекламу", "広告をスキップ", "광고 건너뛰기", "跳过广告",
        "略過廣告", "跳過廣告", "lewati iklan", "ข้ามโฆษณา", "reklamı atla", "reklamı geç",
        "تخطي الإعلان", "pomiń reklamę", "advertentie overslaan", "hoppa över annons"
    )

    /** Từ "Bỏ qua" đơn lẻ: chỉ chấp nhận trong app YouTube và khi view id có chữ skip. */
    val WEAK_PHRASES = listOf(
        "skip", "bỏ qua", "saltar", "omitir", "passer", "überspringen", "salta", "pular",
        "пропустить", "スキップ", "건너뛰기", "跳过", "跳過", "lewati", "ข้าม", "atla", "تخطي"
    )

    /** Từ khoá id view của nút bỏ qua quảng cáo trong các bản YouTube. */
    val SKIP_ID_HINTS = listOf("skip_ad_button", "skip_ad", "ad_skip", "skip_button")
}
