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
     * 获取歌曲信息
     *
     * @param context
     * @param keyword
     * @return
     */
    public static void getSongSearchList(Context context, String keyword, GetSongListListener getSongListListener) {
        String url = String.format(Constant.URL_SONG_SEARCH, keyword);
        RetrofitRequest.sendGetRequest(url, SongSearchResult.class, new RetrofitRequest.ResultHandler<SongSearchResult>(context) {
            @Override
            public void onBeforeResult() {
            }

            @Override
            public void onResult(SongSearchResult songSearchResult) {
                if (songSearchResult == null) {
                    getSongListListener.onFailure();
                    return;
                }
                SongSearchResult.Data data = songSearchResult.getData();
                if (data == null) {
                    getSongListListener.onFailure();
                    return;
                }
                List<SongSearch> songSearchList = data.getLists();
                if (songSearchList == null || songSearchList.size() == 0) {
                    getSongListListener.onFailure();
                    return;
                }
                getSongListListener.onSongList(songSearchList);
            }

            @Override
            public void onAfterFailure() {
                getSongListListener.onFailure();
            }
        });
    }

    /**
     * 读取LRC接口授权key
     *
     * @param context
     * @param hash
     * @param getLrcAccessKeyListener
     */
    public static void getLrcAccessKey(Context context, String hash, GetLrcAccessKeyListener getLrcAccessKeyListener) {
        String url = String.format(Constant.URL_LRC_ACCESSKEY, hash);
        RetrofitRequest.sendGetRequest(url, LrcAccessResult.class, new RetrofitRequest.ResultHandler<LrcAccessResult>(context) {
            @Override
            public void onBeforeResult() {
            }

            @Override
            public void onResult(LrcAccessResult lrcAccessResult) {
                if (lrcAccessResult == null) {
                    getLrcAccessKeyListener.onFailure();
                    return;
                }
                List<LrcAccessResult.Candidate> candidates = lrcAccessResult.getCandidates();
                if (candidates == null || candidates.size() == 0) {
                    getLrcAccessKeyListener.onFailure();
                    return;
                }
                LrcAccessResult.Candidate candidate = candidates.get(0);
                if (candidate == null) {
                    getLrcAccessKeyListener.onFailure();
                    return;
                }
                getLrcAccessKeyListener.onCandidateList(candidates);
            }

            @Override
            public void onAfterFailure() {
                getLrcAccessKeyListener.onFailure();
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

    public interface GetSongListListener {
        /**
         * 歌曲列表回传
         *
         * @param songSearchList
         */
        void onSongList(List<SongSearch> songSearchList);

        /**
         * 读取失败
         */
        void onFailure();
    }

    public interface GetLrcAccessKeyListener {
        /**
         * LRC授权列表
         *
         * @param candidateList
         */
        void onCandidateList(List<LrcAccessResult.Candidate> candidateList);

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
