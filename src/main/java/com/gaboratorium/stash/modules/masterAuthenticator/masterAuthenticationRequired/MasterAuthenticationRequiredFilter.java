package com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired;

import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import lombok.AllArgsConstructor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

@MasterAuthenticationRequired
@AllArgsConstructor
public class MasterAuthenticationRequiredFilter implements ContainerRequestFilter {

    private final StashTokenStore stashTokenStore;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {


        final Cookie tokenCookie = requestContext.getCookies().get("X-Auth-Master-Token");
        final Cookie masterIdCookie = requestContext.getCookies().get("X-Auth-Master-Id");

        final String errorMsg =
            "Master authenticaation failed";

        final boolean isParamListNotProvided =  tokenCookie == null || masterIdCookie == null;

        if (isParamListNotProvided) {
            rejectRequest(requestContext);
        } else  {

            final String token = tokenCookie.getValue();
            final String masterId = masterIdCookie.getValue();
            if (!stashTokenStore.isValid(token, masterId)) {
                rejectRequest(requestContext);
            }
        }
    }

    private void rejectRequest(ContainerRequestContext requestContext) {
        final URI uri = URI.create("dashboard/login");
        final Response response = Response.seeOther(uri).build();
        requestContext.abortWith(response);
    }
}
