package com.dommy.music.util;

import android.content.Context;

import com.dommy.music.bean.SongSearch;
import com.dommy.retrofitframe.network.RetrofitRequest;
import com.dommy.retrofitframe.network.result.LrcAccessResult;
import com.dommy.retrofitframe.network.result.LrcGetResult;
import com.dommy.retrofitframe.network.result.SongSearchResult;

import java.util.List;

public class KuGouLrcUtil {
    /**
     * 获取LRC歌词读取地址
     *
     * @param context
     * @param keyword
     * @return
     */
    public static void getLrcUrl(Context context, String keyword, GetLrcUrlListener getLrcUrlListener) {
        String url = String.format(Constant.URL_SONG_SEARCH, keyword);
        RetrofitRequest.sendGetRequest(url, SongSearchResult.class, new RetrofitRequest.ResultHandler<SongSearchResult>(context) {
            @Override
            public void onBeforeResult() {
            }

            @Override
            public void onResult(SongSearchResult songSearchResult) {
                if (songSearchResult == null) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                SongSearchResult.Data data = songSearchResult.getData();
                if (data == null) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                List<SongSearch> songSearchList = data.getLists();
                if (songSearchList == null || songSearchList.size() == 0) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                SongSearch songSearch = songSearchList.get(0);
                if (songSearch == null) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                String hash = songSearch.getSQFileHash();
                if (CommonUtil.isNull(hash)) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                getLrcAccessKey(context, hash, getLrcUrlListener);
            }

            @Override
            public void onAfterFailure() {
                getLrcUrlListener.onFailure();
            }
        });
    }

    /**
     * 读取LRC接口授权key
     *
     * @param context
     * @param hash
     * @param getLrcUrlListener
     */
    private static void getLrcAccessKey(Context context, String hash, GetLrcUrlListener getLrcUrlListener) {
        String url = String.format(Constant.URL_LRC_ACCESSKEY, hash);
        RetrofitRequest.sendGetRequest(url, LrcAccessResult.class, new RetrofitRequest.ResultHandler<LrcAccessResult>(context) {
            @Override
            public void onBeforeResult() {
            }

            @Override
            public void onResult(LrcAccessResult lrcAccessResult) {
                if (lrcAccessResult == null) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                List<LrcAccessResult.Candidate> candidates = lrcAccessResult.getCandidates();
                if (candidates == null || candidates.size() == 0) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                LrcAccessResult.Candidate candidate = candidates.get(0);
                if (candidate == null) {
                    getLrcUrlListener.onFailure();
                    return;
                }
                String url = String.format(Constant.URL_LRC_READ, candidate.getId(), candidate.getAccesskey());
                getLrcUrlListener.onLrcUrl(url);
            }

            @Override
            public void onAfterFailure() {
                getLrcUrlListener.onFailure();
            }
        });
    }

    /**
     * 读取LRC本文
     *
     * @param context
     * @param lrcUrl
     * @param getLrcListener
     */
    public static void getLrc(Context context, String lrcUrl, GetLrcListener getLrcListener) {
        RetrofitRequest.sendGetRequest(lrcUrl, LrcGetResult.class, new RetrofitRequest.ResultHandler<LrcGetResult>(context) {
            @Override
            public void onBeforeResult() {
            }

            @Override
            public void onResult(LrcGetResult lrcGetResult) {
                if (lrcGetResult == null) {
                    getLrcListener.onFailure();
                    return;
                }
                getLrcListener.onLrcLoad(lrcGetResult.getContent());
            }

            @Override
            public void onAfterFailure() {
                getLrcListener.onFailure();
            }
        });
    }

    public interface GetLrcUrlListener {
        /**
         * LRC url回传
         *
         * @param lrcUrl
         */
        void onLrcUrl(String lrcUrl);

        /**
         * 读取失败
         */
        void onFailure();
    }

    public interface GetLrcListener {
        /**
         * LRC读取成功
         *
         * @param lrc
         */
        void onLrcLoad(String lrc);

        /**
         * 读取失败
         */
        void onFailure();
    }
}
