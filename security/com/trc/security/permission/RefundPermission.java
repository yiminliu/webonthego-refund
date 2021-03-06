package com.trc.security.permission;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.security.core.Authentication;

import com.trc.user.User;
import com.trc.user.authority.ROLE;

/**
 * This was created as a test permission during development and is not finalized. Currently this only checks if the User has a "admin" authority.
 * 
 * @author Tachikoma
 * 
 */
public class RefundPermission extends InternalUserPermission {

	@PostConstruct
	public void init() {
		roleRepository.addAll(Arrays.asList(ROLE.getInternalRoles()));
	}

	@Override
	public boolean isAllowed(
			Authentication authentication,
			Object targetDomainObject) {

		boolean hasPermission = false;

		if (isAuthenticated(authentication)) {
			User user = (User) authentication.getPrincipal();
			hasPermission = super.isAllowed(authentication, targetDomainObject) ? true : isRoleGrantedPermission(user);
		}

		return hasPermission;
	}

}