package com.trc.web.security.authentication;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.trc.config.Config;
import com.trc.manager.AccountManager;
import com.trc.manager.UserManager;
import com.trc.security.encryption.StringEncryptor;
import com.trc.user.EmptyUser;
import com.trc.user.User;
import com.trc.user.account.AccountDetail;
import com.trc.user.authority.Authority;
import com.trc.user.authority.ROLE;
import com.trc.web.session.SessionKey;
import com.trc.web.session.SessionManager;
import com.trc.web.session.cache.CacheKey;
import com.trc.web.session.cache.CacheManager;
import com.tscp.util.logger.DevLogger;
import com.tscp.util.logger.LogLevel;
import com.tscp.util.logger.aspect.Loggable;

public class MySavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
	private UserManager userManager;
	@Autowired
	private AccountManager accountManager;
	@Autowired
	private CacheManager cacheManager;

	@Loggable(value = LogLevel.INFO)
	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		User user = userManager.getLoggedInUser();

		DevLogger.trace("Authentication successful, " + user.getUsername() + " is now logged in.");

		if (!user.isInternalUser())
			userManager.getUserRealName(user);

		setSessionAttributes(user);

		if (Config.ADMIN && user.isInternalUser())
			setAdminDefaultTargetUrl(user);
		else
			setUserDefaultTargetUrl(user);

		super.onAuthenticationSuccess(request, response, authentication);
	}

	private void setSessionAttributes(
			User user) {

		if (Config.ADMIN && user.isInternalUser()) {
			CacheManager.set(SessionKey.CONTROLLING_USER, user);
			CacheManager.set(SessionKey.USER, new EmptyUser());
		} else {
			CacheManager.set(SessionKey.CONTROLLING_USER, new EmptyUser());
			CacheManager.set(SessionKey.USER, user);
		}

		CacheManager.set(SessionKey.ENCRYPTOR, new StringEncryptor(SessionManager.getCurrentSession().getId()));

		cacheManager.refreshCache(user);
	}

	private void setAdminDefaultTargetUrl(
			User user) {

		setAlwaysUseDefaultTargetUrl(true);

		Authority authority = user.getGreatestAuthority();

		if (authority.compare(ROLE.ROLE_MANAGER) >= 0)
			setDefaultTargetUrl("/admin/home");
		else if (authority.compare(ROLE.ROLE_AGENT) == 0) {
			setDefaultTargetUrl("/support/ticket");
		} else if (authority.compare(ROLE.ROLE_SALES) == 0)
			setDefaultTargetUrl("/sales/home");
		else
			setDefaultTargetUrl("/home");
	}

	private void setUserDefaultTargetUrl(
			User user) {

		String targetUrl = "/";
		boolean hasDevice = false;

		List<AccountDetail> accountDetails = (List<AccountDetail>) CacheManager.get(CacheKey.ACCOUNT_DETAILS);
		hasDevice = accountDetails != null && !accountDetails.isEmpty();
		targetUrl = hasDevice ? "/account" : "/start";

		setAlwaysUseDefaultTargetUrl(!hasDevice);
		setDefaultTargetUrl(targetUrl);
	}

}