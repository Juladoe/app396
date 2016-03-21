package com.edusoho.imserver;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.WebSocket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by su on 2016/3/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ImServerUnitTest {

    @Test
    public void serverConnectTest() {
        ImServer imServer = new ImServer("ws://124.160.104.74:10000/chatRot?token=1:43:1458201831:desktop:f4727f4ba9b408cb089a675ddf0d0d93&clientName=suju");
        Assert.assertNotNull(imServer);

        imServer.getSocket().setCallback(new FutureCallback<WebSocket>() {
            @Override
            public void onCompleted(Exception e, WebSocket webSocket) {
                System.out.println("TAG" + webSocket);
            }
        });
    }
}
