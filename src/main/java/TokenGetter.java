import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Response;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.AuthRequest;


import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class TokenGetter {
	
	
	
	  /** Directory to store user credentials. */
	  private static final File DATA_STORE_DIR =
	      new File(System.getProperty("user.home"), ".store/poctest_sample");

	  /**
	   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	   * globally shared instance across your application.
	   */
	  private static FileDataStoreFactory DATA_STORE_FACTORY;

	  /** OAuth 2 scope. */
	  private static final String SCOPE = "https://www.googleapis.com/auth/content";

	  /** Global instance of the HTTP transport. */
	  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	  /** Global instance of the JSON factory. */
	  static final JsonFactory JSON_FACTORY = new JacksonFactory();
	  
 
	  private static final String TOKEN_SERVER_URL = "https://graph.facebook.com/v7.0/oauth/access_token";
	  private static final String AUTHORIZATION_SERVER_URL =
	      "https://www.facebook.com/v2.12/dialog/oauth";

	  /** Authorizes the installed application to access user's protected data. */
	  private static Credential authorize(String userid) throws Exception {
//	    OAuth2ClientCredentials.errorIfNotSpecified();
	    // set up authorization code flow
		  
		  
	    AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
	    	BearerToken.authorizationHeaderAccessMethod(),
	        HTTP_TRANSPORT,
	        JSON_FACTORY,
	        new GenericUrl(TOKEN_SERVER_URL),
	        new ClientParametersAuthentication(
	            "1236020366743614", "b3e86d89909a85ff084c7db05f83d79e"),
	            "1236020366743614",
	        AUTHORIZATION_SERVER_URL)
//	    	.setScopes(Arrays.asList(SCOPE))
	        .setDataStoreFactory(DATA_STORE_FACTORY).build();
	    
	    
	    // authorize
	    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(
	        "localhost").setPort(8081).build();
	    return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userid);
	  }

//	  private static void run(HttpRequestFactory requestFactory) throws IOException {
//	    DailyMotionUrl url = new DailyMotionUrl("https://api.dailymotion.com/videos/favorites");
//	    url.setFields("id,tags,title,url");
//
//	    HttpRequest request = requestFactory.buildGetRequest(url);
//	    VideoFeed videoFeed = request.execute().parseAs(VideoFeed.class);
//	    if (videoFeed.list.isEmpty()) {
//	      System.out.println("No favorite videos found.");
//	    } else {
//	      if (videoFeed.hasMore) {
//	        System.out.print("First ");
//	      }
//	      System.out.println(videoFeed.list.size() + " favorite videos found:");
//	      for (Video video : videoFeed.list) {
//	        System.out.println();
//	        System.out.println("-----------------------------------------------");
//	        System.out.println("ID: " + video.id);
//	        System.out.println("Title: " + video.title);
//	        System.out.println("Tags: " + video.tags);
//	        System.out.println("URL: " + video.url);
//	      }
//	    }
//	  }

	  public static void main(String[] args) {
	    try {
	      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
//	      final Credential credential = authorize();
//	      
//	      System.out.println(credential.getAccessToken());
//	      HttpRequestFactory requestFactory =
//	          HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
//	            @Override
//	            public void initialize(HttpRequest request) throws IOException {
//	              credential.initialize(request);
//	              request.setParser(new JsonObjectParser(JSON_FACTORY));
//	            }
//	          });
//	      run(requestFactory);
	      // Success!
	      return;
	    } catch (IOException e) {
	      System.err.println(e.getMessage());
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	    System.exit(1);
	  }
	  
	  
	  public static String TokenLoader(String userid) throws Exception {
//		  DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
//		  AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(null, null, null, null, null, null, null).setDataStoreFactory(DATA_STORE_FACTORY).build();
//		  Credential credential = flow.loadCredential(userid);
//		  return credential.getAccessToken();
		  
		  
	      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
	      Credential credential = authorize(userid);

//              String accessToken = credential.getAccessToken();
//	          String refreshToken = credential.getRefreshToken();
	      
	          String refreshToken = credential.getAccessToken();
	          Long expire = credential.getExpiresInSeconds();
	          
	          
	          String accessToken = getRefreshToken(refreshToken);
	              
	          
//			  System.out.println("Access Token: " + accessToken);
//			  System.out.println("Refresh Token: " + refreshToken);
//			  System.out.println("Expire in Seconds: " + expire);
			  return accessToken;
	  }
	  
	  public static String getRefreshToken(String refreshToken) throws IOException {
		    try {

		        TokenResponse response =
		            new RefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(), new GenericUrl(
		            		TOKEN_SERVER_URL), refreshToken)
		                .setGrantType("fb_exchange_token").set("fb_exchange_token", refreshToken)
		                .setClientAuthentication(
		                		new ClientParametersAuthentication(
		                	            "1236020366743614", "b3e86d89909a85ff084c7db05f83d79e")).execute();
				    System.out.println("Access Token: " + response.getAccessToken());
				    System.out.println("Refresh Token: " + response.getRefreshToken());
				    System.out.println(response.get("expires_in"));
		        return response.getAccessToken();
		      } catch (TokenResponseException e) {
		        if (e.getDetails() != null) {
		          System.err.println("Error: " + e.getDetails().getError());
		          if (e.getDetails().getErrorDescription() != null) {
		            System.err.println(e.getDetails().getErrorDescription());
		          }
		          if (e.getDetails().getErrorUri() != null) {
		            System.err.println(e.getDetails().getErrorUri());
		          }
		        } else {
		          System.err.println(e.getMessage());
		        }
		      }
		    return null;
	  }
		
		
		
		
		
		
		
	
	
//	public TokenGetter() {
//		
//	}
//	
//	
//	public String getToken() {
//		
//		AuthAPI auth = new AuthAPI("dev-5g2d3pjl.us.auth0.com", 
//				"hfq7in7da8Ldx3m8ly6F39wwmI6XmDf3", 
//				"8y3SBa8UJ4bf7Gu6FdCM83YWHpgEEEck8BtPAMPQFE7JianD9eF-V6D9JvCc-oml");
//		
//		
//		String url = auth.authorizeUrl("https://dev-5g2d3pjl.us.auth0.com/callback")
//			    .withConnection("facebook")
//			    .withResponseType("token")
//			    .withAudience("https://api.dev-5g2d3pjl.us.auth0.com/users")
//			    .build();
//		
//		
//		// exchange authorization code
//		AuthRequest request = auth.exchangeCode("asdfgh", "https://dev-5g2d3pjl.us.auth0.com/callback")
//			    .setAudience("https://api.dev-5g2d3pjl.us.auth0.com/users")
//			    .setScope("openid contacts");
//			try {
//			    TokenHolder holder = request.execute();
//			} catch (APIException exception) {
//			    // api error
//			} catch (Auth0Exception exception) {
//			    // request error
//			}
//			
//		
//	    // request access token
//		AuthRequest request = auth.requestToken("https://api.dev-5g2d3pjl.us.auth0.com/users")
//			.setScope("openid contacts");
//			try {
//				TokenHolder holder = request.execute();
//			} catch (APIException exception) {
//				// api error
//			} catch (Auth0Exception exception) {
//				// request error
//			}
//		
//		return "";
//	}
}
