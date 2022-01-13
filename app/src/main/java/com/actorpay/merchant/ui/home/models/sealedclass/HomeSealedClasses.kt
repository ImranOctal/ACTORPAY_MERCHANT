package com.actorpay.merchant.ui.home.models.sealedclass

import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse

class HomeSealedClasses {
    companion object {
        sealed class ResponseHomeSealed {
            class Success(val response: Any) : ResponseHomeSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseHomeSealed()
            class loading : ResponseHomeSealed()
            object Empty : ResponseHomeSealed()
        }

        sealed class ResponseProductListSealed {
            class Success(val response: Any) : ResponseProductListSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseProductListSealed()
            class loading : ResponseProductListSealed()
            object Empty : ResponseProductListSealed()
        }

        sealed class ResponseDeleteSealed {
            class Success(val response: Any) : ResponseDeleteSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseDeleteSealed()
            class loading : ResponseDeleteSealed()
            object Empty : ResponseDeleteSealed()
        }
        sealed class ResponseGetProductSealed {
            class Success(val response: Any) : ResponseGetProductSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseGetProductSealed()
            class loading : ResponseGetProductSealed()
            object Empty : ResponseGetProductSealed()
        }
        sealed class ResponseEditProductSealed {
            class Success(val response: Any) : ResponseEditProductSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseEditProductSealed()
            class loading : ResponseEditProductSealed()
            object Empty : ResponseEditProductSealed()
        }
        sealed class ResponseAddProductSealed {
            class Success(val response: Any) : ResponseAddProductSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseAddProductSealed()
            class loading : ResponseAddProductSealed()
            object Empty : ResponseAddProductSealed()
        }
        sealed class ResponseChangePasswordSealed {
            class Success(val response: Any) : ResponseChangePasswordSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseChangePasswordSealed()
            class loading : ResponseChangePasswordSealed()
            object Empty : ResponseChangePasswordSealed()
        }

        sealed class ResponseGenrateNewTokenSealed {
            class Success(val response: Any) : ResponseGenrateNewTokenSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseGenrateNewTokenSealed()
            class loading : ResponseGenrateNewTokenSealed()
            object Empty : ResponseGenrateNewTokenSealed()
        }
        sealed class CatogrySealed {
            class Success(val response: Any) : CatogrySealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : CatogrySealed()
            class loading : CatogrySealed()
            object Empty : CatogrySealed()
        }
        sealed class SubCatSealed {
            class Success(val response: Any) : SubCatSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : SubCatSealed()
            class loading : SubCatSealed()
            object Empty : SubCatSealed()
        }
        sealed class TaxationSealed {
            class Success(val response: Any) : TaxationSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : TaxationSealed()
            class loading : TaxationSealed()
            object Empty : TaxationSealed()
        }

        sealed class ResponseSealed {
            class Success(val response: Any) : ResponseSealed()
            class ErrorOnResponse(val failResponse: FailResponse?) : ResponseSealed()
            class loading : ResponseSealed()
            object Empty : ResponseSealed()
        }

    }
}