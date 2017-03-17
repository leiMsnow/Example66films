package com.shuyu.core.proxy;

import android.text.TextUtils;
import android.util.Log;

import com.shuyu.core.uils.LogUtils;
import com.shuyu.core.uils.MD5Utils;
import com.shuyu.core.uils.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 代理服务器类
 *
 * @author hellogv
 */
public class HttpGetProxy {

    /**
     * 避免某些Mediaplayer不播放尾部就结束
     */
    private static final int SIZE = 1024 * 1024;

    final static public String TAG = "HttpGetProxy";
    /**
     * 预加载所需的大小
     */
    private int mBufferSize;
    /**
     * 预加载缓存文件的最大数量
     */
    private static final int mBufferFileMaximum = 30;
    /**
     * 链接带的端口
     */
    private int remotePort = -1;
    /**
     * 远程服务器地址
     */
    private String remoteHost;
    /**
     * 代理服务器使用的端口
     */
    private int localPort;
    /**
     * 本地服务器地址
     */
    private String localHost;
    /**
     * TCP Server，接收Media Player连接
     */
    private ServerSocket localServer = null;
    /**
     * 服务器的Address
     */
    private SocketAddress serverAddress;
    /**
     * 下载线程
     */
    private DownloadThread downloadThread = null;
    /**
     * Response对象
     */
    private Config.ProxyResponse proxyResponse = null;
    /**
     * 缓存文件夹
     */
    private String mBufferDirPath = null;

    /**
     * 预加载文件路径
     */
    private String mMediaFilePath;
    /**
     * 预加载是否可用
     */
    private boolean mEnable = false;

    private Proxy proxy = null;

    /**
     * 视频id，预加载文件以ID命名
     */
    private String mId;
    private String mResourceURL;

    /**
     * 初始化代理服务器，并启动代理服务器
     *
     * @param size 所需预加载的大小
     */
    public HttpGetProxy(int size) {
        try {
            String path = SDCardUtils.getDownloadFilePath() + "/videoCache";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            //初始化代理服务器
            mBufferDirPath = path;
            mBufferSize = size;
            localHost = Config.LOCAL_IP_ADDRESS;
            localServer = new ServerSocket(0, 1, InetAddress.getByName(localHost));
            //ServerSocket自动分配端口
            localPort = localServer.getLocalPort();
            //启动代理服务器
            new Thread() {
                public void run() {
                    startProxy();
                }
            }.start();

            mEnable = true;
        } catch (Exception e) {
            mEnable = false;
        }
    }

    public String getId() {
        return mId;
    }

    /**
     * 代理服务器是否可用
     *
     * @return
     */
    public boolean getEnable() {
        //判断外部存储器是否可用
        File dir = new File(mBufferDirPath);
        mEnable = dir.exists();
        if (!mEnable)
            return false;

        //获取可用空间大小
        long freeSize = Utils.getAvailableSize(mBufferDirPath);
        mEnable = (freeSize > mBufferSize);

        return mEnable;
    }

    /**
     * 停止下载
     */
    public void stopDownload() {
        if (downloadThread != null && downloadThread.isDownloading())
            downloadThread.stopThread();
    }

    /**
     * 开始预加载,一个时间只能预加载一个视频
     *
     * @param url 视频链接
     * @throws Exception
     */
    public void startDownload(String url) throws Exception {
        //代理服务器不可用
        if (!getEnable())
            return;

        //清除过去的缓存文件
        Utils.asyncRemoveBufferFile(mBufferDirPath, mBufferFileMaximum);

        mId = MD5Utils.md5(url);
        mResourceURL = url;
//        String fileName = Utils.getValidFileName(mId);
        mMediaFilePath = mBufferDirPath + "/" + mId;

        //判断文件是否存在，忽略已经缓冲过的文件
        File tmpFile = new File(mMediaFilePath);
        if (tmpFile.exists() && tmpFile.length() >= mBufferSize) {
            Log.i(TAG, "----exists:" + mMediaFilePath + " size:" + tmpFile.length());
            return;
        }
        stopDownload();
        downloadThread = new DownloadThread(mResourceURL, mMediaFilePath, mBufferSize);
        downloadThread.startThread();
        LogUtils.i(TAG, "----startDownload:" + mResourceURL);
    }

    /**
     * 获取播放链接
     */
    public String getProxyURL() {

        if (TextUtils.isEmpty(mId))     //没预加载过
            return "";

        //代理服务器不可用
        if (!getEnable())
            return mResourceURL;

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        DownTask task = new DownTask(mResourceURL);
        FutureTask<String> futureTask = new FutureTask<>(task);
        executorService.submit(futureTask);
        executorService.shutdown();

        String url = null;
        try {
            url = futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return url;
    }

    private class DownTask implements Callable<String> {

        private String mUrl;

        DownTask(String url) {
            this.mUrl = url;
        }

        @Override
        public String call() throws Exception {
            //排除HTTP特殊,如重定向
            String mMediaUrl = Utils.getRedirectUrl(mUrl);
            // ----获取对应本地代理服务器的链接----//
            String localUrl;
            URI originalURI = URI.create(mMediaUrl);
            remoteHost = originalURI.getHost();
            // 保存端口，中转时替换
            remotePort = originalURI.getPort();
            // URL带Port
            if (remotePort != -1) {
                // 使用默认端口
                serverAddress = new InetSocketAddress(remoteHost, remotePort);
                localUrl = mMediaUrl.replace(remoteHost + ":" + remotePort, localHost + ":" + localPort);
            }
            // URL不带Port
            else {
                serverAddress = new InetSocketAddress(remoteHost, Config.HTTP_PORT);// 使用80端口
                remotePort = -1;
                localUrl = mMediaUrl.replace(remoteHost, localHost + ":" + localPort);
            }

            return localUrl;
        }
    }


    private void startProxy() {
        while (true) {
            // --------------------------------------
            // 监听MediaPlayer的请求，MediaPlayer->代理服务器
            // --------------------------------------
            try {
                Socket s = localServer.accept();
                if (proxy != null) {
                    proxy.closeSockets();
                }
                proxy = new Proxy(s);
                new Thread() {
                    public void run() {
                        try {
                            Socket s = localServer.accept();
                            proxy.closeSockets();
                            proxy = new Proxy(s);
                            proxy.run();
                        } catch (IOException e) {
                            LogUtils.e(TAG, e.toString());
                            LogUtils.e(TAG, Utils.getExceptionMessage(e));
                        }
                    }
                }.start();
                proxy.run();
            } catch (IOException e) {
                LogUtils.e(TAG, e.toString());
                LogUtils.e(TAG, Utils.getExceptionMessage(e));
            }
        }
    }

    private class Proxy {
        /**
         * 收发Media Player请求的Socket
         */
        private Socket sckPlayer = null;
        /**
         * 收发Media Server请求的Socket
         */
        private Socket sckServer = null;

        Proxy(Socket sckPlayer) {
            this.sckPlayer = sckPlayer;
        }

        /**
         * 关闭现有的链接
         */
        void closeSockets() {
            try {
                // 开始新的request之前关闭过去的Socket
                if (sckPlayer != null) {
                    sckPlayer.close();
                    sckPlayer = null;
                }

                if (sckServer != null) {
                    sckServer.close();
                    sckServer = null;
                }
            } catch (IOException e1) {
            }
        }

        public void run() {
            HttpParser httpParser;
            HttpGetProxyUtils utils;
            int bytes_read;

            byte[] local_request = new byte[1024];
            byte[] remote_reply = new byte[1024 * 50];

            boolean sentResponseHeader = false;

            try {
                stopDownload();

                httpParser = new HttpParser(remoteHost, remotePort, localHost,
                        localPort);

                Config.ProxyRequest request = null;
                while ((bytes_read = sckPlayer.getInputStream().read(
                        local_request)) != -1) {
                    byte[] buffer = httpParser.getRequestBody(local_request,
                            bytes_read);
                    if (buffer != null) {
                        request = httpParser.getProxyRequest(buffer);
                        break;
                    }
                }

                utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
                boolean isExists = new File(mMediaFilePath).exists();
                if (request != null) {// MediaPlayer的request有效
                    sckServer = utils.sentToServer(request.body);// 发送MediaPlayer的request
                } else {// MediaPlayer的request无效
                    closeSockets();
                    return;
                }
                // ------------------------------------------------------
                // 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
                // ------------------------------------------------------
                while (sckServer != null
                        && ((bytes_read = sckServer.getInputStream().read(remote_reply)) != -1)) {
                    if (sentResponseHeader) {
                        try {// 拖动进度条时，容易在此异常，断开重连
                            utils.sendToMP(remote_reply, bytes_read);
                        } catch (Exception e) {
                            LogUtils.e(TAG, e.toString());
                            LogUtils.e(TAG, Utils.getExceptionMessage(e));
                            break;// 发送异常直接退出while
                        }

                        if (proxyResponse == null)
                            continue;// 没Response Header则退出本次循环

                        // 已完成读取
                        if (proxyResponse.currentPosition > proxyResponse.duration - SIZE) {
                            LogUtils.i(TAG, "....ready....over....");
                            proxyResponse.currentPosition = -1;
                        } else if (proxyResponse.currentPosition != -1) {// 没完成读取
                            proxyResponse.currentPosition += bytes_read;
                        }
                        continue;// 退出本次while
                    }
                    proxyResponse = httpParser.getProxyResponse(remote_reply,
                            bytes_read);
                    if (proxyResponse == null)
                        continue;// 没Response Header则退出本次循环

                    sentResponseHeader = true;
                    // send http header to mediaplayer
                    utils.sendToMP(proxyResponse.body);

                    if (isExists) {// 需要发送预加载到MediaPlayer
                        LogUtils.i(TAG, "----------------->需要发送预加载到MediaPlayer");
                        isExists = false;
                        int sentBufferSize = 0;
                        sentBufferSize = utils.sendPreBufferToMP(
                                mMediaFilePath, request.rangePosition);
                        if (sentBufferSize > 0) {// 成功发送预加载，重新发送请求到服务器
                            // 修改Range后的Request发送给服务器
                            int newRange = (int) (sentBufferSize + request.rangePosition);
                            String newRequestStr = httpParser
                                    .modifyRequestRange(request.body, newRange);
                            LogUtils.i(TAG, newRequestStr);
                            try {
                                if (sckServer != null)
                                    sckServer.close();
                            } catch (IOException ex) {
                            }
                            sckServer = utils.sentToServer(newRequestStr);
                            // 把服务器的Response的Header去掉
                            proxyResponse = utils.removeResponseHeader(
                                    sckServer, httpParser);
                            continue;
                        }
                    }
                    // 发送剩余数据
                    if (proxyResponse.other != null) {
                        utils.sendToMP(proxyResponse.other);
                    }
                }
                // 关闭 2个SOCKET
                closeSockets();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}