[Ivy]
163051859F226598 9.3.0 #module
>Proto >Proto Collection #zClass
Fs0 FinalReviewFormProcess Big #zClass
Fs0 RD #cInfo
Fs0 #process
Fs0 @TextInP .type .type #zField
Fs0 @TextInP .processKind .processKind #zField
Fs0 @AnnotationInP-0n ai ai #zField
Fs0 @MessageFlowInP-0n messageIn messageIn #zField
Fs0 @MessageFlowOutP-0n messageOut messageOut #zField
Fs0 @TextInP .xml .xml #zField
Fs0 @TextInP .responsibility .responsibility #zField
Fs0 @UdInit f0 '' #zField
Fs0 @UdProcessEnd f1 '' #zField
Fs0 @UdEvent f3 '' #zField
Fs0 @UdExitEnd f4 '' #zField
Fs0 @PushWFArc f5 '' #zField
Fs0 @UdEvent f16 '' #zField
Fs0 @UdProcessEnd f19 '' #zField
Fs0 @GridStep f17 '' #zField
Fs0 @PushWFArc f20 '' #zField
Fs0 @PushWFArc f18 '' #zField
Fs0 @GridStep f6 '' #zField
Fs0 @PushWFArc f7 '' #zField
Fs0 @PushWFArc f2 '' #zField
Fs0 @UdInit f8 '' #zField
Fs0 @PushWFArc f9 '' #zField
>Proto Fs0 Fs0 FinalReviewFormProcess #zField
Fs0 f0 guid 16305185A13E9F88 #txt
Fs0 f0 method start(java.util.List<gawfs.TaskDef>,java.util.List<String>,java.lang.Integer) #txt
Fs0 f0 inParameterDecl '<java.util.List<gawfs.TaskDef> finishedTasks,java.util.List<String> steps,Integer actualStepIndex> param;' #txt
Fs0 f0 inParameterMapAction 'out.actualStepIndex=param.actualStepIndex;
out.finishedTasks=param.finishedTasks;
out.steps=param.steps;
' #txt
Fs0 f0 outParameterDecl '<> result;' #txt
Fs0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>start()</name>
        <nameStyle>7,5,7
</nameStyle>
    </language>
</elementInfo>
' #txt
Fs0 f0 83 139 26 26 -16 15 #rect
Fs0 f1 531 139 26 26 0 12 #rect
Fs0 f3 guid 16305185A2B30A9B #txt
Fs0 f3 actionTable 'out=in;
' #txt
Fs0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>close</name>
    </language>
</elementInfo>
' #txt
Fs0 f3 83 235 26 26 -15 12 #rect
Fs0 f4 211 235 26 26 0 12 #rect
Fs0 f5 expr out #txt
Fs0 f5 109 248 211 248 #arcP
Fs0 f16 guid 16348C318DEC020D #txt
Fs0 f16 actionTable 'out=in;
' #txt
Fs0 f16 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>cancel</name>
        <nameStyle>6,5,7
</nameStyle>
    </language>
</elementInfo>
' #txt
Fs0 f16 77 339 26 26 -18 15 #rect
Fs0 f19 525 339 26 26 0 12 #rect
Fs0 f17 actionTable 'out=in;
' #txt
Fs0 f17 actionCode 'import ch.ivy.addon.portalkit.util.TaskUtils;
import ch.ivy.addon.portalkit.publicapi.PortalNavigatorAPI;

TaskUtils.resetTask(ivy.task);
PortalNavigatorAPI.navigateToPortalEndPage();' #txt
Fs0 f17 security system #txt
Fs0 f17 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>Reset task</name>
    </language>
</elementInfo>
' #txt
Fs0 f17 250 330 112 44 -29 -8 #rect
Fs0 f20 expr out #txt
Fs0 f20 362 352 525 352 #arcP
Fs0 f18 expr out #txt
Fs0 f18 103 352 250 352 #arcP
Fs0 f6 actionTable 'out=in;
' #txt
Fs0 f6 actionCode 'import gawfs.TaskDef;

for (TaskDef task : in.finishedTasks) {
	task.actualApplicant = ivy.wf.getSecurityContext().findUser(task.actualApplicantName);
}' #txt
Fs0 f6 security system #txt
Fs0 f6 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>Initialize</name>
    </language>
</elementInfo>
' #txt
Fs0 f6 229 129 112 44 -22 -8 #rect
Fs0 f7 expr out #txt
Fs0 f7 109 152 229 151 #arcP
Fs0 f2 expr out #txt
Fs0 f2 341 151 531 152 #arcP
Fs0 f8 guid 17BE95E5BB2D906E #txt
Fs0 f8 method startExported(gawfs.ExecutePredefinedWorkflowData) #txt
Fs0 f8 inParameterDecl '<gawfs.ExecutePredefinedWorkflowData execdata> param;' #txt
Fs0 f8 inParameterMapAction 'out.actualStepIndex=param.execdata.actualStepIndex+1;
out.finishedTasks=param.execdata.finishedTasks;
out.steps=param.execdata.steps;
' #txt
Fs0 f8 outParameterDecl '<> result;' #txt
Fs0 f8 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>startExported(ExecutePredefinedWorkflowData)</name>
    </language>
</elementInfo>
' #txt
Fs0 f8 83 51 26 26 -19 15 #rect
Fs0 f9 109 64 237 129 #arcP
>Proto Fs0 .type ch.ivy.gawfs.workflowExecution.FinalReviewForm.FinalReviewFormData #txt
>Proto Fs0 .processKind HTML_DIALOG #txt
>Proto Fs0 -8 -8 16 16 16 26 #rect
Fs0 f3 mainOut f5 tail #connect
Fs0 f5 head f4 mainIn #connect
Fs0 f16 mainOut f18 tail #connect
Fs0 f18 head f17 mainIn #connect
Fs0 f17 mainOut f20 tail #connect
Fs0 f20 head f19 mainIn #connect
Fs0 f0 mainOut f7 tail #connect
Fs0 f7 head f6 mainIn #connect
Fs0 f6 mainOut f2 tail #connect
Fs0 f2 head f1 mainIn #connect
Fs0 f8 mainOut f9 tail #connect
Fs0 f9 head f6 mainIn #connect
