package com.roket.shmtzh.uchat.api;

import com.roket.shmtzh.uchat.model.Message;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by shmtzh on 7/7/16.
 */
public interface MessageApi {

    @GET("user/message")
    Observable<List<Message>> getMessageList();

    @POST("user/message")
    Observable<Message> postMessage(@Body Message message);

}
