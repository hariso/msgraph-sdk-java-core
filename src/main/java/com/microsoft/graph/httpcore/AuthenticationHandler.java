package com.microsoft.graph.httpcore;

import java.io.IOException;

import com.microsoft.graph.httpcore.middlewareoption.MiddlewareType;
import com.microsoft.graph.httpcore.middlewareoption.TelemetryOptions;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationHandler implements Interceptor {
	
	public final MiddlewareType MIDDLEWARE_TYPE = MiddlewareType.AUTHENTICATION;

	private ICoreAuthenticationProvider authProvider;
	
	public AuthenticationHandler(ICoreAuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request originalRequest = chain.request();
		
		if(originalRequest.tag(TelemetryOptions.class) == null)
			originalRequest = originalRequest.newBuilder().tag(TelemetryOptions.class, new TelemetryOptions()).build();
		originalRequest.tag(TelemetryOptions.class).setFeatureUsage(TelemetryOptions.AUTH_HANDLER_ENABLED_FLAG);
		
		Request authenticatedRequest = authProvider.authenticateRequest(originalRequest);
		return chain.proceed(authenticatedRequest);
	}

}
