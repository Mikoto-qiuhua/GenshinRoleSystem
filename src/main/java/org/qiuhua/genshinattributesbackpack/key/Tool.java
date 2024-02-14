package org.qiuhua.genshinattributesbackpack.key;

import io.lumine.mythic.bukkit.utils.lib.http.HttpEntity;
import io.lumine.mythic.bukkit.utils.lib.http.client.methods.CloseableHttpResponse;
import io.lumine.mythic.bukkit.utils.lib.http.client.methods.HttpGet;
import io.lumine.mythic.bukkit.utils.lib.http.client.utils.URIBuilder;
import io.lumine.mythic.bukkit.utils.lib.http.impl.client.CloseableHttpClient;
import io.lumine.mythic.bukkit.utils.lib.http.impl.client.HttpClients;
import io.lumine.mythic.bukkit.utils.lib.http.util.EntityUtils;
import org.qiuhua.genshinattributesbackpack.configfile.DefaultConfig;


import java.net.*;

public class Tool {

    private static final String deviceCode = DeviceCodeExample();
    public static String sendLogin(){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder("http://111.230.40.162:13554/api.php");
            uriBuilder.setParameter("api", "kmlogon");
            uriBuilder.setParameter("app","10000");
            uriBuilder.setParameter("kami", DefaultConfig.getString("Key"));
            uriBuilder.setParameter("markcode", deviceCode);
            uriBuilder.setParameter("t", String.valueOf(System.currentTimeMillis()));
            URI uri = uriBuilder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                int index = responseBody.indexOf("{");
                if (index != -1) {
                    return responseBody.substring(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String DeviceCodeExample() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = networkInterface.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (byte b : hardwareAddress) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}
