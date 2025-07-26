import com.google.gson.annotations.SerializedName

data class PetFacilityResponse(
    val page: Int,
    val perPage: Int,
    val totalCount: Int,
    val currentCount: Int,
    val matchCount: Int,
    val data: List<FacilityItem>
)

data class FacilityItem(
    @SerializedName("시설명")
    val fac_name: String?,

    @SerializedName("운영시간")
    val weekday_oper_time: String?,

    @SerializedName("휴무일")
    val closed_day: String?,

    @SerializedName("주차 가능여부")
    val parking_poss_yn: String?,

    @SerializedName("위도")
    val latitude: String?,

    @SerializedName("경도")
    val longitude: String?,

    @SerializedName("시도 명칭")
    val sido_name: String?,

    @SerializedName("시군구 명칭")
    val gugun_name: String?,

    @SerializedName("법정읍면동명칭")
    val dong_name: String?,

    @SerializedName("카테고리3")
    val ctg3_name: String?
)
