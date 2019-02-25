package com.bazardelfaidate.scribeapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class MainActivity extends AppCompatActivity {

    final String MAGENTO_API_KEY = "7m07ajz79bjrb75n1n9c44i2vac8v29s";
    final String MAGENTO_API_SECRET = "fa5j85uh772va0we72fvrdwpmokizi39";
    final String MAGENTO_REST_API_URL = "http://bazarfaidate.it/api/rest/";

    private static Token requestToken;
    private OAuthService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// To override error of execution of network thread on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            service = new ServiceBuilder()
                    .provider(MagentoThreeLeggedOAuth.class)
                    .apiKey(MAGENTO_API_KEY)
                    .apiSecret(MAGENTO_API_SECRET)
                    .debug()
                    .build();

            System.out.println("Magento'srkflow");
            System.out.println();

            // Obtain the Request Token
            System.out.println("FetchingRequest Token...");
            requestToken = service.getRequestToken();
            System.out.println("GotRequest Token!");
            System.out.println();

            System.out.println("FetchingAuthorization URL...");
            String authorizationUrl = service.getAuthorizationUrl(requestToken);
            Log.d("DEBUG", authorizationUrl);
            System.out.println("GotAuthorization URL!");
            System.out.println("Nownd authorize Main here:");
            System.out.println(authorizationUrl);
            System.out.println("Ande the authorization code here");
            System.out.print(">>");

            String verifierCode = ((EditText) findViewById(R.id.editText)).getText().toString();
            Log.d("DEBUG", verifierCode);
            Verifier verifier = new Verifier(verifierCode);
            System.out.println();
            System.out.println("TradingRequest Token for an Access Token...");
            Token accessToken = service.getAccessToken(requestToken, verifier);
            System.out.println("GotAccess Token!");
            System.out.println("(if curious it looks like this: "
                    + accessToken + " )");
            System.out.println();

            OAuthRequest request = new OAuthRequest(Verb.GET, MAGENTO_REST_API_URL + "/products?limit=2");
            service.signRequest(accessToken, request);
            Response response = request.send();
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());
            System.out.println();


        } catch (Exception e) {
            e.printStackTrace();
        }



    }



    public static final class MagentoThreeLeggedOAuth extends DefaultApi10a {
        private static final String BASE_URL = "http://bazarfaidate.it/index.php/admin0bft/";

        @Override
        public String getRequestTokenEndpoint() {
            return BASE_URL + "oauth/initiate";
        }

        @Override
        public String getAccessTokenEndpoint() {
            return BASE_URL + "oauth/token";
        }

        @Override
        public String getAuthorizationUrl(Token requestToken) {
            return BASE_URL + "admin/oauth_authorize?oauth_token="
                    + requestToken.getToken(); //this implementation is for admin roles only...
        }

    }
}
