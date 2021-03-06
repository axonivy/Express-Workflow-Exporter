package ch.ivy.addon.portalkit.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import ch.ivy.addon.portalkit.constant.CustomFields;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.call.ISubProcessStart;
import ch.ivyteam.ivy.process.call.SubProcessRunner;
import ch.ivyteam.ivy.process.call.SubProcessSearchFilter;
import ch.ivyteam.ivy.process.call.SubProcessSearchFilter.Builder;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.SecurityManagerFactory;
import ch.ivyteam.ivy.service.ServiceException;
import ch.ivyteam.ivy.workflow.ITask;

/**
 * This class is to implement method to get information of Ivy
 */
public class IvyAdapterService {

  /**
   * Executes the given {@link Callable} as System-User.
   * 
   * @param callable The {@link Callable} to be executed.
   * 
   * @return The return value of the {@link Callable}.
   */
  public static <V> V executeCallableAsSystem(Callable<V> callable) {
    try {
      ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
      return securityManager.executeAsSystem(callable);
    } catch (NoSuchFieldException e) {
      String message = "Field not found.";
      Ivy.log().error(message);
      throw new ServiceException(message, e);
    } catch (Exception e) {
      String message = "There was an unspecific error.";
      Ivy.log().error(message);
      throw new ServiceException(message, e);
    }
  }

  /**
   * Calls the sub process with the given subProcessSignature with the given parameters that are not from excluded
   * libraries. Exactly one sub process with the given signature is expected.
   * 
   * @param subProcessSignature The signature of the sub process to be triggered.
   * @param parameters The parameters to pass to the process.
   * @param excludedLibraries The subprocess from these libraries name will be excluded
   * @return The response of the process execution.
   */
  public static Map<String, Object> startSubProcess(String subProcessSignature, Map<String, Object> parameters,
      List<String> excludedLibraries) {
    FindSubProcessStartCallable findSubProcessStartCallable =
        new FindSubProcessStartCallable(subProcessSignature, excludedLibraries);
    ISubProcessStart subProcessStart = executeCallableAsSystem(findSubProcessStartCallable);

    SubProcessCallerCallable subprocessCallable = new SubProcessCallerCallable(subProcessStart, parameters);
    return executeCallableAsSystem(subprocessCallable);
  }

  /**
   * {@link Callable} that finds an {@link ISubProcessStart}.
   */
  public static class FindSubProcessStartCallable implements Callable<ISubProcessStart> {

    private String subprocessSignature;
    private List<String> excludedLibraries;

    public FindSubProcessStartCallable(String subprocessSignature, List<String> excludedLibraries) {
      this.subprocessSignature = subprocessSignature;
      this.excludedLibraries = excludedLibraries;
    }

    /**
     * {@inheritDoc}<br />
     * <b>This implementation:</b><br />
     * Finds a {@link ISubProcessStart} with the given subprocess signature.
     */
    @Override
    public ISubProcessStart call() {
      Builder subprocessFilter = SubProcessSearchFilter.create();
      SubProcessSearchFilter filter =
          subprocessFilter.setSignature(subprocessSignature).setSearchInAllProjects(true)
              .setSearchInDependentProjects(false).toFilter();
      return findSubprocess(filter);
    }

    private ISubProcessStart findSubprocess(SubProcessSearchFilter filter) {
      List<ISubProcessStart> subProcessStarts = SubProcessRunner.findSubProcessStarts(filter);
      ISubProcessStart foundSubProcessStart = null;
      ISubProcessStart defaultsubProcessStart = null;
      for (ISubProcessStart subProcessStart : subProcessStarts) {
        IProcessModelVersion processModelVersion = subProcessStart.getProcessModelVersion();
        String libraryId = processModelVersion.getLibrary().getId();
        if (excludedLibraries == null || !excludedLibraries.contains(libraryId)) {
          if (processModelVersion.equals(Ivy.request().getProcessModelVersion())) {
            return subProcessStart;
          }
          foundSubProcessStart = subProcessStart;
        } else {
          defaultsubProcessStart = subProcessStart;
        }
      }
      return foundSubProcessStart != null && 
          foundSubProcessStart.getProcessModelVersion() != null && 
          foundSubProcessStart.getProcessModelVersion().getActivityState() == ActivityState.ACTIVE 
          ? foundSubProcessStart : defaultsubProcessStart;
    }
  }

  /**
   * Calls a given sub process with parameters.
   */
  public static class SubProcessCallerCallable implements Callable<Map<String, Object>> {
    private ISubProcessStart subProcessStart;
    private Map<String, Object> parameters;

    public SubProcessCallerCallable(ISubProcessStart subProcessStart, Map<String, Object> parameters) {
      this.subProcessStart = subProcessStart;
      this.parameters = parameters;
    }

    /**
     * {@inheritDoc}<br />
     * <b>This implementation:</b><br />
     * Executes the given sub process with the given parameters.
     */
    @Override
    public Map<String, Object> call() {
      try {
        return SubProcessRunner.execute(subProcessStart, parameters);
      } catch (Exception e) {
        String message = "Unable to execute subprocess.";
        Ivy.log().error(message, e);
        throw new ServiceException(message, e);
      }
    }
  }
  
  /**
   * @param task
   * @return whether task embed in IFrame
   */
  public static Boolean getEmbedInIFrameCustomField(ITask task) {
    String embedInIFrame = task.customFields().stringField(CustomFields.EMBED_IN_FRAME).getOrNull();
    if (embedInIFrame == null) {
      embedInIFrame = task.getCase().customFields().stringField(CustomFields.EMBED_IN_FRAME).getOrNull();
    }
    return embedInIFrame != null ? Boolean.valueOf(embedInIFrame) : null;
  }

}
