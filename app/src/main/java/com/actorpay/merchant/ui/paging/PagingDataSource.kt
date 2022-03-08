package com.actorpay.merchant.ui.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.ProductPram
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.retrofitrepository.apiclient.ApiClient
import org.json.JSONObject

class PagingDataSource(private var apiClient: ApiClient, val token: String,
                       val productParams: ProductPram,

): PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {

        try {
            val pageNumber = params.key ?: 0
            var response=  apiClient.getProductList(token, pageNumber,10,"createdAt",asc = true,productParams)

            if(response.isSuccessful.not()) {
                return LoadResult.Error(
                    Throwable(
                        handleError(
                            response.code(),
                            response.errorBody()!!.string()
                        )
                    )
                )

            }
            val pagedResponse = response.body()!!
            val data = pagedResponse.data


            var nextPageNumber: Int? = null
            if (pagedResponse.data.pageNumber < (pagedResponse.data.totalPages-1)) {

                nextPageNumber =pagedResponse.data.pageNumber+1
            }

           return LoadResult.Page(
                data = data.items,
                prevKey = null,
                nextKey = nextPageNumber
            )

        }catch (e:Exception){
            return   LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    fun handleError(code:Int,error:String): String {

        if (code == 403) {
            val json = JSONObject()
            json.put("code", code)
            return json.toString()
        }
        else {
            val json = JSONObject(error)
            json.put("code", 0)
            return json.toString()
        }

    }

}

