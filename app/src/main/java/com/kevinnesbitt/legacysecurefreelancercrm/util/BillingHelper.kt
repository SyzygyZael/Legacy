package com.kevinnesbitt.legacysecurefreelancercrm.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingHelper(context: Context) : PurchasesUpdatedListener {

    // Required parameter in Billing Library 7.0+
    private val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .enablePrepaidPlans()
        .build()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(pendingPurchasesParams) // Updated for Billing Library 7.0+
        .build()

    // Expose subscription status to your UI
    private val _isSubscribed = MutableStateFlow<Boolean?>(null) // null = loading
    val isSubscribed = _isSubscribed.asStateFlow()

    private var productDetails: ProductDetails? = null
    private val productId = "legacy_full_access_monthly" // Your Play Console ID

    init {
        connectToPlayBilling()
    }

    private fun connectToPlayBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Connected! Now check existing purchases and fetch the product
                    queryExistingPurchases()
                    queryProductDetails()
                } else {
                    Log.e("BillingHelper", "Billing Setup Failed: ${billingResult.debugMessage}")
                    // Fallback to false if we cannot connect at all
                    _isSubscribed.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                // Connection to Play Billing lost.
                Log.w("BillingHelper", "Billing Service Disconnected")
            }
        })
    }

    // 1. Fetch the product details (Price, Name, etc.) from Google Play
    private fun queryProductDetails() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            ).build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // 1. Extract the list from productDetailsResult
                // 2. Use safe-call (?.) since productDetailsList can be null if empty
                productDetails = productDetailsResult.productDetailsList.firstOrNull { it.productId == productId }
            }
        }
    }

    // 2. Launch the bottom sheet purchase UI
    fun launchBillingFlow(activity: Activity) {
        val product = productDetails ?: return

        // Define the Base Plan ID created in the Play Console
        val targetBasePlanId = "legacy-base"

        // Search for the exact Base Plan inside the product's offers
        val offerDetails = product.subscriptionOfferDetails?.find { offer ->
            offer.basePlanId == targetBasePlanId
        } ?: run {
            Log.e("BillingHelper", "Base plan $targetBasePlanId not found in product details")
            return
        }

        // Grab the token for that specific Base Plan
        val offerToken = offerDetails.offerToken

        // Build the purchase parameters
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the Google Play slide-up sheet
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    // 3. Handle the result of the purchase UI
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase) // Auto-acknowledge new purchases
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("BillingHelper", "User canceled the purchase flow.")
        } else {
            Log.e("BillingHelper", "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    // 4. Check for active purchases when the app opens
    private fun queryExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val activePurchases = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }

                _isSubscribed.value = activePurchases.isNotEmpty()

                // Auto-acknowledge any unacknowledged active purchases
                for (purchase in activePurchases) {
                    handlePurchase(purchase)
                }
            } else {
                Log.e("BillingHelper", "Failed to query existing purchases: ${billingResult.debugMessage}")
                _isSubscribed.value = false
            }
        }
    }

    // 5. Acknowledge new purchases
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _isSubscribed.value = true

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d("BillingHelper", "Purchase successfully acknowledged!")
                    } else {
                        Log.e("BillingHelper", "Failed to acknowledge: ${billingResult.debugMessage}")
                    }
                }
            }
        }
    }
}