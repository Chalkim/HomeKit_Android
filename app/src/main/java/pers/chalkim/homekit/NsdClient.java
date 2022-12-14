package pers.chalkim.homekit;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;

public class NsdClient {

    public static final String TAG = "NsdClientWhy";

    private final String NSD_SERVER_TYPE = "_http._tcp.";
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolverListener;
    public NsdManager mNsdManager;
    private Context mContext;
    private String mServiceName;
    private Handler mHandler;
    private IServerFound mIServerFound;
    private ArrayList<String> discoveryList=new ArrayList<>();
    private ArrayList<String> resolveList=new ArrayList<>();

    /**
     * @param context:上下文对象
     * @param serviceName  客户端扫描 指定的地址
     * @param iServerFound 回调
     */
    public NsdClient(Context context, String serviceName, IServerFound iServerFound) {
        mContext = context;
        mServiceName = serviceName;
        mIServerFound = iServerFound;
    }

    public void startNSDClient(final Handler handler) {

        new Thread(){
            @Override
            public void run() {
                mHandler=handler;
                mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
                initializeDiscoveryListener();//初始化监听器
                initializeResolveListener();//初始化解析器
                mNsdManager.discoverServices(NSD_SERVER_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);//开启扫描
            }
        }.start();
    }

    /**
     * 扫描解析前的 NsdServiceInfo
     */
    private void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
                Log.e(TAG, "onStartDiscoveryFailed():");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
                Log.e(TAG, "onStopDiscoveryFailed():");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.e(TAG, "onDiscoveryStarted():");

            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.e(TAG, "onDiscoveryStopped():");
            }

            /**
             *
             * @param serviceInfo
             */
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "onServiceFound: "+serviceInfo );
                discoveryList.add(serviceInfo.toString());
                //根据咱服务器的定义名称，指定解析该 NsdServiceInfo
                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    mNsdManager.resolveService(serviceInfo, mResolverListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "onServiceLost(): serviceInfo=" + serviceInfo);
                discoveryList.remove(serviceInfo.toString());
            }
        };
    }

    /**
     * 解析发现的NsdServiceInfo
     */
    private void initializeResolveListener() {
        mResolverListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                int port = serviceInfo.getPort();
                String serviceName = serviceInfo.getServiceName();
                String hostAddress = serviceInfo.getHost().getHostAddress();

                Message message=Message.obtain();
                message.what=1;
                message.obj=hostAddress;
                mHandler.sendMessage(message);

                Log.e(TAG, "onServiceResolved 已解析:" + " host:" + hostAddress + ":" + port + " ----- serviceName: " + serviceName);
                resolveList.add(" host:" + hostAddress + ":" + port );
                //TODO 建立网络连接

            }
        };
    }

    public void stopNSDServer() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public interface IServerFound {

        /**
         * 回調 指定解析的结果
         */
        void onServerFound(NsdServiceInfo serviceInfo, int port);

        /**
         * 無合適 回調失敗
         */
        void onServerFail();
    }
}
