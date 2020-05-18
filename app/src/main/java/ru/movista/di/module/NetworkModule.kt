package ru.movista.di.module

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.movista.BuildConfig
import ru.movista.data.entity.*
import ru.movista.data.entity.objects.*
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.JSON_MEDIA_TYPE
import ru.movista.data.source.local.OBJECT_ID
import ru.movista.data.source.local.ObjectType
import ru.movista.data.source.remote.*
import ru.movista.domain.model.FavouritePlacesType
import ru.movista.utils.AuthorizationHelper
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ContextModule::class, AppModule::class])
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Api {
        return Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
            .create(Api::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(context: Context): Cache =
        Cache(context.cacheDir, 10 * 1024 * 1024)


    @Provides
    @Singleton
    fun provideOkHttp(
        context: Context,
        cache: Cache,
        deviceInfo: DeviceInfo,
        authorizationHelper: AuthorizationHelper
    ): OkHttpClient {
        val builder = getBaseBuilder(cache).apply {
            addInterceptor(createRequiredBodyParamsInterceptor(deviceInfo))
            authenticator(authorizationHelper.AuthInterceptor())
            if (BuildConfig.DEBUG) addInterceptor(createHttpLoggingInterceptor())
        }
        return builder.build()
    }

    private fun getBaseBuilder(cache: Cache): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .cache(cache)
            .retryOnConnectionFailure(false)
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
    }

    private fun createMoshi(): Moshi {
        return Moshi.Builder()
            .add(
                ru.movista.utils.PolymorphicJsonAdapterFactory.of(ObjectEntity::class.java, OBJECT_ID)
                    .withDefaultValue(null)
                    .withSubtype(BotMessageEntity::class.java, ObjectType.BOT_MESSAGE)
                    .withSubtype(ShortRoutesEntity::class.java, ObjectType.ROUTES_GOOGLE)
                    .withSubtype(TaxiEntity::class.java, ObjectType.TAXI)
                    .withSubtype(CarEntity::class.java, ObjectType.CAR)
                    .withSubtype(LongRoutesEntity::class.java, ObjectType.ROUTES_MOVISTA)
                    .withSubtype(MovistaPlaceholderEntity::class.java, ObjectType.PLACEHOLDER_MOVISTA)
                    .withSubtype(AvailableTravelCardsEntity::class.java, ObjectType.AVAILABLE_TRAVEL_CARDS)
                    .withSubtype(SkillEntity::class.java, ObjectType.SKILL)
                    .withSubtype(WeatherEntity::class.java, ObjectType.WEATHER)
                    .withSubtype(AddressesEntity::class.java, ObjectType.ADDRESS)
            )
            .add(
                ru.movista.utils.PolymorphicJsonAdapterFactory.of(TaxiRouteEntity::class.java, OBJECT_ID)
                    .withDefaultValue(null)
                    .withSubtype(CommonTaxiRouteEntity::class.java, ObjectType.ROUTE_TAXI)
            )
            .add(
                ru.movista.utils.PolymorphicJsonAdapterFactory.of(ShortRouteEntity::class.java, OBJECT_ID)
                    .withDefaultValue(null)
                    .withSubtype(GoogleRouteEntity::class.java, ObjectType.ROUTE_GOOGLE)
            )
            .add(
                ru.movista.utils.PolymorphicJsonAdapterFactory.of(TripEntity::class.java, OBJECT_ID)
                    .withDefaultValue(null)
                    .withSubtype(SubwayTripEntity::class.java, ObjectType.TRIP_SUBWAY)
                    .withSubtype(OnFootTripEntity::class.java, ObjectType.TRIP_ON_FOOT)
                    .withSubtype(BusTripEntity::class.java, ObjectType.TRIP_BUS)
                    .withSubtype(CommuterTrainTripEntity::class.java, ObjectType.TRIP_COMMUTER_TRAIN)
                    .withSubtype(TaxiTripEntity::class.java, ObjectType.TAXI)
                    .withSubtype(FerryTripEntity::class.java, ObjectType.TRIP_FERRY)
                    .withSubtype(HeavyRailTripEntity::class.java, ObjectType.TRIP_HEAVY_RAIL)
                    .withSubtype(ShareTaxiTripEntity::class.java, ObjectType.TRIP_SHARE_TAXI)
                    .withSubtype(TrolleybusTripEntity::class.java, ObjectType.TRIP_TROLLEYBUS)
                    .withSubtype(TramTripEntity::class.java, ObjectType.TRIP_TRAM)
            )
            .add(
                ru.movista.utils.PolymorphicJsonAdapterFactory.of(CarRouteEntity::class.java, OBJECT_ID)
                    .withDefaultValue(null)
                    .withSubtype(GoogleCarEntity::class.java, ObjectType.TRIP_CAR)
            )
            .add(
                ru.movista.utils.PolymorphicJsonAdapterFactory.of(LongRouteEntity::class.java, OBJECT_ID)
                    .withDefaultValue(null)
                    .withSubtype(MovistaRouteEntity::class.java, ObjectType.ROUTE_MOVISTA)
            )
            .add(
                FavouritePlacesType::class.java,
                EnumJsonAdapter
                    .create(FavouritePlacesType::class.java)
                    .withUnknownFallback(FavouritePlacesType.default)
            )
            .add(ComfortTypeAdapter())
            .add(PathGroupStateAdapter())
            .add(TripTypeAdapter())
            .add(ObjectTripTypeAdapter())
            .add(PriceSegmentTypeAdapter())
            .add(ObjectServiceTypeAdapter())
            .add(TrainCoachTypeAdapter())
            .add(DateTimeAdapter())
            .add(ZonedDateTimeAdapter())
            .add(DateAdapter())
            .build()
    }

    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun createRequiredBodyParamsInterceptor(deviceInfo: DeviceInfo): Interceptor {
        return Interceptor { chain ->
            var original = chain.request()
            var body = original.body
            if (body != null) {
                body = processApplicationJsonRequestBody(body, deviceInfo)
                if (body != null) {
                    val requestBuilder = original.newBuilder()
                    when (original.method) {
                        "PUT" -> requestBuilder.put(body)
                        else -> requestBuilder.post(body)
                    }
                    original = requestBuilder.build()
                }
            }
            return@Interceptor chain.proceed(original)

        }
    }

    private fun processApplicationJsonRequestBody(requestBody: RequestBody, deviceInfo: DeviceInfo): RequestBody? {
        val bodyString = bodyToString(requestBody)
        return try {
            val obj = if (!bodyString.isNullOrEmpty()) JSONObject(bodyString) else JSONObject()
            obj.put("version", BuildConfig.VERSION_NAME.take(3)) // 1.0 вместо 1.0.1-dev
            obj.put("user_id", deviceInfo.deviceId)
            obj.put("client", "android")
            RequestBody.create(JSON_MEDIA_TYPE.toMediaTypeOrNull(), obj.toString())
        } catch (e: JSONException) {
            Timber.e(e)
            null
        }
    }

    private fun bodyToString(requestBody: RequestBody): String? {
        return try {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            Timber.e(e)
            null
        }
    }
}

