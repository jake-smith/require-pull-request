package com.eftours.bitbucket.plugin.hook;

import com.atlassian.bitbucket.hook.*;
import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.*;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequireDevelopPullRequest implements PreReceiveRepositoryHook
{
	private final PermissionService permissionService;
	
	@Autowired
	public RequireDevelopPullRequest(@ComponentImport PermissionService permissionService)
	{
		this.permissionService = permissionService;
	}
	
    /**
     * Disables pushing to the develop branch, instructing to create a pull request
     */
    @Override
    public boolean onReceive(RepositoryHookContext context, Collection<RefChange> refChanges, HookResponse hookResponse)
    {
    	if (permissionService.hasRepositoryPermission(context.getRepository(), Permission.REPO_ADMIN))
    	{
    		return true;
    	}
    	
        for (RefChange refChange : refChanges)
        {
            if (refChange.getRef().getId().equals("refs/head/develop") && refChange.getType() == RefChangeType.UPDATE)
            {
                hookResponse.err().println("No pushing. Please create a Pull Request.");
                return false;
            }
        }
        return true;
    }
}
