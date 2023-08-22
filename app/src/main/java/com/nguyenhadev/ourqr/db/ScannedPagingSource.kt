package com.nguyenhadev.ourqr.db

//
//class ScannedPagingSource(val dao: MeDao):PagingSource<Int, QRScanned>() {
//    override fun getRefreshKey(state: PagingState<Int, QRScanned>): Int? {
//        return state.anchorPosition?.let { pos->
//            val closePage= state.closestPageToPosition(pos)
//            closePage?.prevKey?.plus(1) ?: closePage?.prevKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QRScanned> {
//        try {
//            val key = params.key ?: 0
//            val data = dao.gets(params.loadSize, key)
//            return LoadResult.Page(data = data,if (key== 0)null else key-params.loadSize,if(data.size < params.loadSize) null else key + params.loadSize)
//        }catch (e:Exception){
//            return LoadResult.Error(object :Throwable(){})
//        }
//    }
//}