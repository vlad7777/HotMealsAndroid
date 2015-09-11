/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.ericpol.hotmeals.RetrofitTools;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Log;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.client.Client;
import retrofit.client.Client.Provider;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.mime.FormUrlEncodedTypedOutput;

/**
 * A Builder class for a Retrofit REST Adapter. Extends the default implementation by providing logic to
 * handle an OAuth 2.0 password grant login flow. The RestAdapter that it produces uses an interceptor
 * to automatically obtain a bearer token from the authorization server and insert it into all client
 * requests.
 * 
 * You can use it like this:
 * 
  	private VideoSvcApi videoService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + VideoSvcApi.TOKEN_PATH)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(VideoSvcApi.class);
 * 
 * @author Jules, Mitchell
 *
 */
public class SecuredRestBuilder extends RestAdapter.Builder {

	private class OAuthHandler implements RequestInterceptor {

        private String accessToken;
		private Client client;

		public OAuthHandler(String token, Client client) {
			super();
			accessToken = token;
			this.client = client;
		}

		/**
		 * Every time a method on the client interface is invoked, this method is
		 * going to get called. The method checks if the client has previously obtained
		 * an OAuth 2.0 bearer token. If not, the method obtains the bearer token by
		 * sending a password grant request to the server.
		 *
		 * Once this method has obtained a bearer token, all future invocations will
		 * automatically insert the bearer token as the "Authorization" header in
		 * outgoing HTTP requests.
		 *
		 */

		@Override
		public void intercept(RequestFacade request) {
				request.addHeader("Authorization", "Bearer " + accessToken);
		}
	}

	private String username;
	private String password;
	private String loginUrl;
	private String clientId;
	private String clientSecret;
	private Client client;
	private String token;

	public SecuredRestBuilder setLoginEndpoint(String endpoint){
		loginUrl = endpoint;
		return this;
	}

	@Override
	public SecuredRestBuilder setEndpoint(String endpoint) {
		return (SecuredRestBuilder) super.setEndpoint(endpoint);
	}

	@Override
	public SecuredRestBuilder setEndpoint(Endpoint endpoint) {
		return (SecuredRestBuilder) super.setEndpoint(endpoint);
	}

	@Override
	public SecuredRestBuilder setClient(Client client) {
		this.client = client;
		return (SecuredRestBuilder) super.setClient(client);
	}

	@Override
	public SecuredRestBuilder setClient(Provider clientProvider) {
		client = clientProvider.get();
		return (SecuredRestBuilder) super.setClient(clientProvider);
	}

	@Override
	public SecuredRestBuilder setErrorHandler(ErrorHandler errorHandler) {

		return (SecuredRestBuilder) super.setErrorHandler(errorHandler);
	}

	@Override
	public SecuredRestBuilder setExecutors(Executor httpExecutor,
			Executor callbackExecutor) {

		return (SecuredRestBuilder) super.setExecutors(httpExecutor,
				callbackExecutor);
	}

	@Override
	public SecuredRestBuilder setRequestInterceptor(
			RequestInterceptor requestInterceptor) {

		return (SecuredRestBuilder) super
				.setRequestInterceptor(requestInterceptor);
	}

	@Override
	public SecuredRestBuilder setConverter(Converter converter) {

		return (SecuredRestBuilder) super.setConverter(converter);
	}

	@Override
	public SecuredRestBuilder setProfiler(@SuppressWarnings("rawtypes") Profiler profiler) {

		return (SecuredRestBuilder) super.setProfiler(profiler);
	}

	@Override
	public SecuredRestBuilder setLog(Log log) {

		return (SecuredRestBuilder) super.setLog(log);
	}

	@Override
	public SecuredRestBuilder setLogLevel(LogLevel logLevel) {

		return (SecuredRestBuilder) super.setLogLevel(logLevel);
	}

	public SecuredRestBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	public SecuredRestBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	public SecuredRestBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public SecuredRestBuilder setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	public SecuredRestBuilder setToken(String token) {
		this.token = token;
		return this;
	}

	@Override
	public RestAdapter build() {
		if (client == null)
			client = new OkClient(UnsafeHttpsClient.createUnsafeClient());
		OAuthHandler hdlr = new OAuthHandler(token, client);
		setRequestInterceptor(hdlr);

		return super.build();
	}
}