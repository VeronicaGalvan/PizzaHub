package com.example.pizzahub_mobile.data.network

import android.content.Context
import android.util.Log
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.routing.CalculateRouteCallback
import com.here.sdk.routing.CarOptions
import com.here.sdk.routing.Route
import com.here.sdk.routing.RoutingEngine
import com.here.sdk.routing.Waypoint
import com.here.sdk.search.AddressQuery
import com.here.sdk.search.SearchEngine
import com.here.sdk.search.SearchOptions
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

// HERE SDK repository using SearchEngine and RoutingEngine
class HereRepository(private val context: Context) {

    private var searchEngine: SearchEngine? = null
    private var routingEngine: RoutingEngine? = null

    init {
        try {
            searchEngine = SearchEngine()
            routingEngine = RoutingEngine()
        } catch (e: InstantiationErrorException) {
            Log.e("HereRepository", "Error initializing HERE engines", e)
        }
    }

    data class RouteSummary(val durationSeconds: Long = 0, val lengthMeters: Int = 0)

    // Geocode an address using HERE SDK SearchEngine
    suspend fun geocodeAddress(
            calle: String,
            numero: String,
            colonia: String,
            ciudad: String = "León",
            estado: String = "Guanajuato",
            pais: String = "México"
    ): Result<Pair<Double, Double>> = suspendCancellableCoroutine { continuation ->
        val engine = searchEngine
        if (engine == null) {
            continuation.resume(Result.failure(Exception("SearchEngine not initialized")))
            return@suspendCancellableCoroutine
        }

        try {
            // Build full address
            val fullAddress = "$calle $numero, $colonia, $ciudad, $estado, $pais"
            Log.d("HereRepository", "Geocoding address: $fullAddress")

            val query = AddressQuery(fullAddress, GeoCoordinates(21.12, -101.68)) // León area
            val searchOptions = com.here.sdk.search.SearchOptions()
            searchOptions.languageCode = com.here.sdk.core.LanguageCode.ES_MX
            searchOptions.maxItems = 1

            val callback =
                    com.here.sdk.search.SearchCallbackExtended { searchError, list, _ ->
                        if (searchError != null) {
                            Log.e("HereRepository", "Geocoding error: ${searchError.name}")
                            continuation.resume(
                                    Result.failure(
                                            Exception("Geocoding failed: ${searchError.name}")
                                    )
                            )
                        } else if (list.isNullOrEmpty()) {
                            Log.w("HereRepository", "No results found for address")
                            continuation.resume(
                                    Result.failure(Exception("No coordinates found for address"))
                            )
                        } else {
                            val place = list[0]
                            val coords = place.geoCoordinates
                            if (coords != null) {
                                Log.d(
                                        "HereRepository",
                                        "Geocoded to: ${coords.latitude}, ${coords.longitude}"
                                )
                                continuation.resume(
                                        Result.success(coords.latitude to coords.longitude)
                                )
                            } else {
                                continuation.resume(
                                        Result.failure(Exception("No coordinates in result"))
                                )
                            }
                        }
                    }

            engine.search(query, searchOptions, callback)
        } catch (e: Exception) {
            Log.e("HereRepository", "Geocoding request failed", e)
            continuation.resume(Result.failure(e))
        }
    }

    // Calculate route using HERE SDK RoutingEngine
    suspend fun getRoute(
            originLat: Double,
            originLon: Double,
            destLat: Double,
            destLon: Double
    ): Result<Pair<Route, RouteSummary>> = suspendCancellableCoroutine { continuation ->
        val engine = routingEngine
        if (engine == null) {
            continuation.resume(Result.failure(Exception("RoutingEngine not initialized")))
            return@suspendCancellableCoroutine
        }

        try {
            val startWaypoint = Waypoint(GeoCoordinates(originLat, originLon))
            val destWaypoint = Waypoint(GeoCoordinates(destLat, destLon))
            val waypoints = listOf(startWaypoint, destWaypoint)

            val carOptions = CarOptions()

            Log.d(
                    "HereRepository",
                    "Calculating route from ($originLat, $originLon) to ($destLat, $destLon)"
            )

            val callback = CalculateRouteCallback { routingError, routes ->
                if (routingError != null) {
                    Log.e("HereRepository", "Routing error: ${routingError.name}")
                    continuation.resume(
                            Result.failure(Exception("Routing failed: ${routingError.name}"))
                    )
                } else if (routes.isNullOrEmpty()) {
                    Log.w("HereRepository", "No route found")
                    continuation.resume(Result.failure(Exception("No route found")))
                } else {
                    val route = routes[0]
                    val durationSeconds = route.duration.seconds
                    val lengthMeters = route.lengthInMeters

                    val summary =
                            RouteSummary(
                                    durationSeconds = durationSeconds,
                                    lengthMeters = lengthMeters
                            )

                    Log.d("HereRepository", "Route found: ${lengthMeters}m, ${durationSeconds}s")
                    continuation.resume(Result.success(route to summary))
                }
            }

            engine.calculateRoute(waypoints, carOptions, callback)
        } catch (e: Exception) {
            Log.e("HereRepository", "Route calculation failed", e)
            continuation.resume(Result.failure(e))
        }
    }

    fun cleanup() {
        searchEngine = null
        routingEngine = null
    }
}
