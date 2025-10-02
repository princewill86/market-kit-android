package io.horizontalsystems.marketkit.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

data class MarketInfoOverviewRaw(
    val performance: Map<String, Map<String, BigDecimal?>>,
    @SerializedName("genesis_date")
    val genesisDate: Date?,
    val categories: List<CoinCategory>,
    val description: String?,
    val links: Map<String, String>,
    @SerializedName("market_data")
    val marketData: MarketData,
    
) {

    fun marketInfoOverview(fullCoin: FullCoin): MarketInfoOverview {
            val performance = performance.map { (vsCurrency, v) ->
                vsCurrency to v.mapNotNull { (timePeriodRaw, performance) ->
                    if (performance == null) return@mapNotNull null
                    val timePeriod = MarketInfoOverview.strToHsTimePeriod(timePeriodRaw)
                        ?: return@mapNotNull null

                    timePeriod to performance
                }.toMap()
            }.toMap()

            val links = links
                .mapNotNull { (linkTypeRaw, link) ->
                    LinkType.fromString(linkTypeRaw)?.let {
                        it to link
                    }
                }.toMap()

            return MarketInfoOverview(
                fullCoin,
                marketData.marketCap,
                marketData.marketCapRank,
                marketData.totalSupply,
                marketData.circulatingSupply,
                marketData.volume24h,
                marketData.dilutedMarketCap,
                marketData.tvl,
                performance,
                genesisDate,
                categories,
                description ?: "",
                links,
                Thank you for clarifying that you want to fix the code to display ATH and ATL data using the forked MarketKit library. Since you’ve forked the Horizontal Systems MarketKit, we can modify MarketInfoOverviewRaw and related classes to include ATH/ATL data, which are available in CoinGecko’s /coins/{id} endpoint (as MarketKit likely pulls data from CoinGecko). The errors (Unresolved reference: ath, athDate, atl, atlDate) occurred because these fields are not in MarketInfoOverview or MarketInfoOverviewRaw. We’ll update the data models and CoinViewFactory to include and display this data just below genesisDate.
Step 1: Update MarketKit Models
Since you’ve forked MarketKit, we’ll modify MarketInfoOverviewRaw and MarketInfoOverview to include ath, athDate, atl, and atlDate from CoinGecko’s market_data response. CoinGecko provides these fields in the /coins/{id} endpoint under market_data (e.g., ath, ath_date, atl, atl_date as maps by currency code and ISO 8601 date strings).
Modified MarketInfoOverviewRaw
Update the MarketInfoOverviewRaw class to parse ATH/ATL fields:
kotlinpackage io.horizontalsystems.marketkit.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

data class MarketInfoOverviewRaw(
    val performance: Map<String, Map<String, BigDecimal?>>,
    @SerializedName("genesis_date")
    val genesisDate: Date?,
    val categories: List<CoinCategory>,
    val description: String?,
    val links: Map<String, String>,
    @SerializedName("market_data")
    val marketData: MarketData,
) {

    fun marketInfoOverview(fullCoin: FullCoin): MarketInfoOverview {
        val performance = performance.map { (vsCurrency, v) ->
            vsCurrency to v.mapNotNull { (timePeriodRaw, performance) ->
                if (performance == null) return@mapNotNull null
                val timePeriod = MarketInfoOverview.strToHsTimePeriod(timePeriodRaw)
                    ?: return@mapNotNull null
                timePeriod to performance
            }.toMap()
        }.toMap()

        val links = links
            .mapNotNull { (linkTypeRaw, link) ->
                LinkType.fromString(linkTypeRaw)?.let { it to link }
            }.toMap()

        return MarketInfoOverview(
            fullCoin,
            marketData.marketCap,
            marketData.marketCapRank,
            marketData.totalSupply,
            marketData.circulatingSupply,
            marketData.volume24h,
            marketData.dilutedMarketCap,
            marketData.tvl,
            performance,
            genesisDate,
            categories,
            description ?: "",
            links,
            marketData.ath,  // Add ATH
            marketData.athDate,  // Add ATH date
            marketData.atl,  // Add ATL
            marketData.atlDate  // Add ATL date
            )
        }

    data class MarketData(
        @SerializedName("market_cap")
        val marketCap: BigDecimal?,
        @SerializedName("market_cap_rank")
        val marketCapRank: Int?,
        @SerializedName("total_supply")
        val totalSupply: BigDecimal?,
        @SerializedName("circulating_supply")
        val circulatingSupply: BigDecimal?,
        @SerializedName("total_volume")
        val volume24h: BigDecimal?,
        @SerializedName("fully_diluted_valuation")
        val dilutedMarketCap: BigDecimal?,
        val tvl: BigDecimal?,
        @SerializedName("ath")  // Add ATH (map by currency, e.g., {"usd": 123.45})
        val ath: Map<String, BigDecimal>?,
        @SerializedName("ath_date")  // Add ATH date (map by currency, e.g., {"usd": "2021-11-10T14:24:11.849Z"})
        val athDate: Map<String, String>?,
        @SerializedName("atl")  // Add ATL
        val atl: Map<String, BigDecimal>?,
        @SerializedName("atl_date")  // Add ATL date
        val atlDate: Map<String, String>?
    )
}
