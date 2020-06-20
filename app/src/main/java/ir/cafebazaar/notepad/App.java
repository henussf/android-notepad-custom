package ir.cafebazaar.notepad;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by MohMah on 8/17/2016.
 */
public class App extends Application {
    public static volatile Context CONTEXT;
    public static JobManager JOB_MANAGER;

    static OkHttpClient okHttpClient = null;


    @Override
    public void onCreate()
    {
        super.onCreate();
        CONTEXT = getApplicationContext();
        FlowManager.init(this);
        Stetho.initializeWithDefaults(this);
        configureJobManager();
    }

    private void configureJobManager()
    {
        Configuration.Builder builder = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled()
                    {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args)
                    {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args)
                    {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args)
                    {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args)
                    {

                    }
                })
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        //	builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(this,
        //			MyJobService.class), true);
        //}else{
        //	int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        //	if (enableGcm == ConnectionResult.SUCCESS){
        //		builder.scheduler(GcmJobSchedulerService.createSchedulerFor(this,
        //				MyGcmJobService.class), true);
        //	}
        //}
        JOB_MANAGER = new JobManager(builder.build());
    }

    public static OkHttpClient getHttpClient(){
//        okHttpClient = new OkHttpClient.Builder()
//                .sslSocketFactory(createSSLSocketFactory())
//                .hostnameVerifier(new TrustAllHostnameVerifier())
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS)
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })
//                .build();

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(base);
    }


}
