package com.example.data.model

enum class OwnerRoleMode(val labelAr: String) {
    PARTICIPANT("أشارك لاعباً"),
    ORGANIZER_ONLY("منظّم فقط")
}

enum class TournamentStatus(val labelAr: String) {
    REGISTRATION_OPEN("التسجيل مفتوح"),
    FULL("مكتمل العدد"),
    DRAFT_DRAW("القرعة مسودة (غير منشورة)"),
    IN_PROGRESS("البطولة جارية"),
    COMPLETED("مكتملة ومختتمة"),
    ARCHIVED("مؤرشفة")
}

enum class MatchStatus(val labelAr: String) {
    UPCOMING("قادمة"),
    LIVE("مباشرة"),
    COMPLETED("مكتملة"),
    POSTPONED("مؤجلة")
}

enum class StageType(val labelAr: String) {
    GROUP("دور المجموعات"),
    QUARTER("ربع النهائي"),
    SEMI("نصف النهائي"),
    THIRD_PLACE("المركز الثالث"),
    FINAL("المباراة النهائية")
}

enum class DisputeStatus(val labelAr: String) {
    NONE("لا يوجد"),
    UNDER_REVIEW("اعتراض قيد المراجعة"),
    RESOLVED("تم الفصل والاعتماد")
}

enum class ConnectionState(val labelAr: String) {
    ONLINE("متصل"),
    OFFLINE("غير متصل"),
    RECENT_DISCONNECT("انقطاع حديث")
}

enum class ComplaintType(val labelAr: String) {
    COMPLAINT("شكوى"),
    SUGGESTION("مقترح"),
    FEATURE_REQUEST("طلب ميزة")
}

enum class ComplaintStatus(val labelAr: String) {
    NEW("جديد"),
    UNDER_REVIEW("قيد المراجعة"),
    ACCEPTED("مقبول"),
    REJECTED("مرفوض"),
    COMPLETED("مكتمل")
}

enum class SupportedGame(val labelAr: String, val shortName: String) {
    EFOOTBALL("eFootball", "eFootball"),
    PES("PES", "PES"),
    EA_FC("EA SPORTS FC", "EA FC"),
    FIFA("FIFA", "FIFA")
}
