package ch.ivyteam.ivy;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import ch.ivy.addon.portalkit.bo.ExpressProcess;
import ch.ivy.addon.portalkit.bo.ExpressTaskDefinition;
import ch.ivy.addon.portalkit.persistence.converter.BusinessEntityConverter;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.bpm.exec.client.restricted.BpmExecDeployer;
import ch.ivyteam.ivy.components.ProcessKind;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.IProcess;
import ch.ivyteam.ivy.process.IProcessManager;
import ch.ivyteam.ivy.process.IProjectProcessManager;
import ch.ivyteam.ivy.process.model.diagram.Diagram;
import ch.ivyteam.ivy.process.model.diagram.shape.DiagramShape;
import ch.ivyteam.ivy.process.model.element.activity.UserTask;
import ch.ivyteam.ivy.process.model.element.activity.value.dialog.UserDialogId;
import ch.ivyteam.ivy.process.model.element.activity.value.dialog.UserDialogStart;
import ch.ivyteam.ivy.process.model.element.event.end.TaskEnd;
import ch.ivyteam.ivy.process.model.element.event.start.RequestStart;
import ch.ivyteam.ivy.process.model.element.event.start.value.CallSignature;
import ch.ivyteam.ivy.process.model.element.event.start.value.StartAccessPermissions;
import ch.ivyteam.ivy.process.model.element.gateway.TaskSwitchGateway;
import ch.ivyteam.ivy.process.model.element.value.CaseConfig;
import ch.ivyteam.ivy.process.model.element.value.IvyScriptExpression;
import ch.ivyteam.ivy.process.model.element.value.task.Activator;
import ch.ivyteam.ivy.process.model.element.value.task.ActivatorType;
import ch.ivyteam.ivy.process.model.element.value.task.CustomField;
import ch.ivyteam.ivy.process.model.element.value.task.TaskConfig;
import ch.ivyteam.ivy.process.model.element.value.task.TaskConfigs;
import ch.ivyteam.ivy.process.model.element.value.task.TaskIdentifier;
import ch.ivyteam.ivy.process.model.value.MappingCode;
import ch.ivyteam.ivy.process.model.value.scripting.VariableDesc;
import ch.ivyteam.ivy.process.resource.ProcessCreator;
import ch.ivyteam.ivy.process.resource.ProcessCreator.ProcessCreationParameters;
import ch.ivyteam.ivy.resource.datamodel.ResourceDataModelException;
import ch.ivyteam.ivy.security.exec.Sudo;

@SuppressWarnings("restriction")
public class ExpressWorkflowExporter {

  static final int GRID_X = 128;
  static final int GRID_Y = 96;

  public static void export(ExpressProcess expressProcess) throws ResourceDataModelException {

    IProcessModel pm = IApplication.current().findProcessModel("AxonIvyExpressExport");
    IProcessModelVersion pmv = pm.getReleasedProcessModelVersion();
    IProject project = pmv.getProject();
    write(expressProcess, project);
    Sudo.exec(() -> BpmExecDeployer.deploy(pmv));

  }

  private static void write(ExpressProcess expressProcess, IProject project)
          throws ResourceDataModelException {
    IProjectProcessManager manager = IProcessManager.instance()
            .getProjectDataModelFor(project);
    IProcess existing = manager
            .findProcessByPath("Business Processes/" + expressProcess.getProcessName(), false);
    if (existing != null) {
      Ivy.log().debug("Already existing " + existing);
      return;
    }

    ProcessCreationParameters createParameters = ProcessCreator
            .create(project, expressProcess.getProcessName())
            .kind(ProcessKind.NORMAL)
            .namespace("Business Processes")
            .createDefaultContent(false)
            .toCreator().getCreateParameters();
    IProcess process = manager.createProcess(createParameters, new NullProgressMonitor());

    Diagram diagram = process.getModel().getDiagram();

    drawElements(expressProcess.getTaskDefinitions(), diagram, expressProcess.getProcessName(), manager);

    process.save();

    refreshTree(project);
  }

  private static void drawElements(List<ExpressTaskDefinition> tasks, Diagram execDiagram,
          String processname, IProjectProcessManager manager) {
    int x = 96;
    int y = 0;

    y += 128;
    DiagramShape start = execDiagram.add().shape(RequestStart.class).at(x, y);
    start.getLabel().setText("start" + processname + ".ivp");
    RequestStart starter = start.getElement();
    makeExecutable(starter, processname, getSteps(tasks));

    DiagramShape previous = start;
    boolean isfirstTask = true;
    for (ExpressTaskDefinition taskdef : tasks) {
      x += GRID_X;

      if (taskdef.getResponsibles().size() > 1) {
        DiagramShape split = execDiagram.add().shape(TaskSwitchGateway.class).at(x, y);
        split.getLabel().setText("split");
        previous.edges().connectTo(split); // connect
        x += GRID_X;

        DiagramShape current = execDiagram.add().shape(UserTask.class).at(x, y-GRID_Y/2);
        createUserTask(taskdef, current, isfirstTask, 0);
        isfirstTask = false;
        split.edges().connectTo(current); // connect

        x += GRID_X;
        DiagramShape join = execDiagram.add().shape(TaskSwitchGateway.class).at(x, y);
        join.getLabel().setText("join");
        current.edges().connectTo(join); // connect

        for (int nb = 1; nb < taskdef.getResponsibles().size(); nb++) {
          DiagramShape more = execDiagram.add().shape(UserTask.class).at(x - GRID_X, y + nb * GRID_Y - GRID_Y/2);
          createUserTask(taskdef, more, isfirstTask, nb);
          isfirstTask = false;

          //DiagramEdge connectTo = split.edges().connectTo(more); // connect
          //SequenceFlow flow = connectTo.getConnector();
          //bendConnectorLegacy(execDiagram, flow);

          //DiagramEdge connectTo2 = more.edges().connectTo(join); // connect
          //SequenceFlow flow2 = connectTo2.getConnector();// connect
          //flow2.setWaypoints(flow2.getWaypoints().add(new Position(x, y + nb * GRID_Y)));
          //bendConnectorLegacy(execDiagram, flow2);
        }
        createSystemTaskGateway(manager, split);

        previous = join;

      } else {
        DiagramShape current = execDiagram.add().shape(UserTask.class).at(x, y);
        createUserTask(taskdef, current, isfirstTask, 0);
        isfirstTask = false;
        previous.edges().connectTo(current); // connect
        Ivy.log().debug("previuos"+previous);
        if(previous.representsInstanceOf(TaskSwitchGateway.class))
        {
          createSystemTaskGateway(manager, previous);
        }

        previous = current;
      }
    }

    x += GRID_X;
    DiagramShape finalreviewtask = execDiagram.add().shape(UserTask.class).at(x, y);
    createFinalReviewTask(finalreviewtask, processname);
    previous.edges().connectTo(finalreviewtask);
    if(previous.representsInstanceOf(TaskSwitchGateway.class))
    {
      createSystemTaskGateway(manager, previous);
    }

    x += GRID_X;
    DiagramShape end = execDiagram.add().shape(TaskEnd.class).at(x, y);
    finalreviewtask.edges().connectTo(end);
  }

//  private static void bendConnectorLegacy(Diagram execDiagram, SequenceFlow flow) {
//    ProcessZ legacy = (ProcessZ)execDiagram.getProcess().getRootProcess();
//    ZFacade zFacade = legacy.internal().getZFacade();
//    new DefaultArcComputer(zFacade).breach(flow.getLegacyAPI().getZObject().getField(), false).execute();
//  }

  private static void createSystemTaskGateway(IProjectProcessManager manager, DiagramShape taskGateway) {
    manager.getProcessConfigurator().updateElement(taskGateway.getElement().getLegacyAPI().getZObject());
    TaskSwitchGateway gateway = taskGateway.getElement();
    //gateway.setTaskConfigs(task -> task.setTaskConfig(null, null));
    TaskConfigs taskConfigs = gateway.getTaskConfigs();
    Set<TaskIdentifier> taskIdentifiers = taskConfigs.getTaskIdentifiers();
    for(TaskIdentifier ident:taskIdentifiers)
    {
      Ivy.log().debug("task:"+ident);
      TaskConfig taskConfig = taskConfigs.getTaskConfig(ident);
      taskConfig = taskConfig.setName("SYSTEM "+taskGateway.getLabel());
      taskConfig = taskConfig.setActivator(new Activator("SYSTEM", ActivatorType.ROLE));
      taskConfigs = taskConfigs.setTaskConfig(ident, taskConfig);
    }
    gateway.setTaskConfigs(taskConfigs);

    Ivy.log().info("read: "+gateway.getTaskConfigs());
  }

  private static String getSteps(List<ExpressTaskDefinition> tasks) {
    StringBuffer sb = new StringBuffer();
    sb.append("\"");
    for (ExpressTaskDefinition task : tasks) {
      sb.append(task.getSubject());
      sb.append(",");
    }
    sb.append("Final Review");
    sb.append("\"");
    return sb.toString();
  }

  private static void createUserTask(ExpressTaskDefinition taskdef, DiagramShape current,
          boolean isfirstTask, int index) {

    current.getLabel().setText(taskdef.getSubject());

    UserTask usertask = current.getElement();
    usertask.setName(taskdef.getSubject());

    TaskConfig taskConfig = usertask.getTaskConfig();
    taskConfig = taskConfig.setName(taskdef.getSubject());
    taskConfig = taskConfig.setDescription(taskdef.getDescription() == null ? "" : taskdef.getDescription());

    taskConfig = taskConfig.setTaskListSkipped(isfirstTask);

    Activator activator = new Activator("\"" + taskdef.getResponsibles().get(index) + "\"",
            ActivatorType.ROLE_FROM_ATTRIBUTE);
    taskConfig = taskConfig.setActivator(activator);

    List<CustomField> customFields = taskConfig.getCustomFields();
    customFields.add(new CustomField("dynaform",
            new IvyScriptExpression(
                    "\"" + BusinessEntityConverter.entityToJsonValue(taskdef).replace('"', '\'') + "\""),
            CustomField.Type.STRING));
    customFields.add(new CustomField("stepindex",
            new IvyScriptExpression("" + (taskdef.getTaskPosition() - 1)),
            CustomField.Type.NUMBER));
    taskConfig = taskConfig.setCustomFields(customFields);

    taskConfig = taskConfig.setExpiryDelay("new Duration(0,0," + taskdef.getUntilDays() + ",0,0,0)");

    usertask.setTaskConfig(taskConfig);

    List<VariableDesc> inputParameters = Arrays
            .asList(new VariableDesc("execdata", "gawfs.ExecutePredefinedWorkflowData"));
    List<VariableDesc> outputParameters = Arrays
            .asList(new VariableDesc("execdata", "gawfs.ExecutePredefinedWorkflowData"));
    CallSignature callSigature = new CallSignature("start", inputParameters, outputParameters);
    UserDialogStart userDialogStart = usertask.getTargetDialog()
            .setId(UserDialogId.create("ch.ivy.gawfs.workflowExecution.ExportedUserTaskForm"))
            .setStartMethod(callSigature);
    usertask.setTargetDialog(userDialogStart);
    usertask.setParameters(MappingCode.mapOnly("param.execdata", "in.executePredefinedWorkflowData"));
    usertask.setOutput(MappingCode.mapOnly("out.executePredefinedWorkflowData", "result.execdata"));
  }

  private static void createFinalReviewTask(DiagramShape finalreviewtask, String processname) {
    finalreviewtask.getLabel().setText("Final Review");

    UserTask usertask = finalreviewtask.getElement();
    usertask.setName("Final Review");
    usertask.setDescription("Exported AxonIvyExpress Workflow " + processname);

    TaskConfig taskConfig = usertask.getTaskConfig();
    taskConfig = taskConfig.setName(processname + ": Final Review");
    taskConfig.setDescription("The workflow " + processname + " has been finsihed");

    taskConfig = taskConfig.setActivator(new Activator("CREATOR", ActivatorType.ROLE));
    usertask.setTaskConfig(taskConfig);

    List<VariableDesc> inputParameters = Arrays
            .asList(new VariableDesc("execdata", "gawfs.ExecutePredefinedWorkflowData"));
    List<VariableDesc> outputParameters = Arrays.asList();
    CallSignature callSignature = new CallSignature("startExported", inputParameters, outputParameters);
    UserDialogStart userDialogStart = usertask.getTargetDialog()
            .setId(UserDialogId.create("ch.ivy.gawfs.workflowExecution.FinalReviewForm"))
            .setStartMethod(callSignature);
    usertask.setTargetDialog(userDialogStart);
    usertask.setParameters(MappingCode.mapOnly("param.execdata", "in.executePredefinedWorkflowData"));
  }

  private static void makeExecutable(RequestStart starter, String processname, String steps) {
    starter.setLinkName("start_" + processname + ".ivp");
    starter.setDescription(processname);
    starter.setStartByHttpRequestAllowed(true);
    starter.setStartName(processname);
    StartAccessPermissions permissions = new StartAccessPermissions("Everybody");
    starter.setRequiredPermissions(permissions);

    CaseConfig caseConfig = starter.getCaseConfig();
    caseConfig = caseConfig.setName(processname);
    List<CustomField> customFields = caseConfig.getCustomFields();
    customFields.add(new CustomField("steps",
            new IvyScriptExpression(steps),
            CustomField.Type.STRING));
    customFields.add(new CustomField("embedInFrame",
            new IvyScriptExpression("\"False\""),
            CustomField.Type.STRING));
    caseConfig = caseConfig.setCustomFields(customFields);
    starter.setCaseConfig(caseConfig);
  }

  private static void refreshTree(IProject project) {
    try {
      if (Advisor.instance().isDesigner()) {
        project.getProject().build(IResource.PROJECT, new NullProgressMonitor());
        project.getFolder("processes").refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
      }
    } catch (CoreException e) {
      e.printStackTrace();
    }

  }
}
