package Netty;
import io.netty.buffer.ByteBuf;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;


import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class ResponseHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) {
        ByteBuf buffer = (ByteBuf) o;
        //System.out.println("as");
        JSONObject jsonObject = new JSONObject(buffer.toString(CharsetUtil.UTF_8));

        returnDefaultResponse(ctx, jsonObject);
    }

    void returnDefaultResponse(ChannelHandlerContext ctx, JSONObject JSONResponse) {
        // Get the HTTP Request by using the AttributeKey that was used to set it in |RequestHandler|.
        HttpRequest req = ctx.channel().attr(Server.REQ_KEY).get();
        int statusCode = (int) JSONResponse.remove("statusCode");

        ByteBuf bufferResponse = Unpooled.copiedBuffer(JSONResponse.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.valueOf(statusCode),
                Unpooled.wrappedBuffer(bufferResponse));
        response.headers()
                .set(CONTENT_TYPE, "application/json")
                .setInt(CONTENT_LENGTH, response.content().readableBytes());
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        if (keepAlive) {
            if (!req.protocolVersion().isKeepAliveDefault()) {
                response.headers().set(CONNECTION, KEEP_ALIVE);
            }
        } else {
            // Tell the client we're going to close the connection.
            response.headers().set(CONNECTION, CLOSE);
        }

        ChannelFuture f = ctx.write(response);

        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}