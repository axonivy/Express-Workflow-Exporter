[Ivy]
17BE37427EDF03C3 9.3.0 #module
>Proto >Proto Collection #zClass
Et0 Export Big #zClass
Et0 B #cInfo
Et0 #process
Et0 @AnnotationInP-0n ai ai #zField
Et0 @TextInP .type .type #zField
Et0 @TextInP .processKind .processKind #zField
Et0 @TextInP .xml .xml #zField
Et0 @TextInP .responsibility .responsibility #zField
Et0 @StartRequest f0 '' #zField
Et0 @EndTask f1 '' #zField
Et0 @GridStep f4 '' #zField
Et0 @PushWFArc f2 '' #zField
Et0 @PushWFArc f6 '' #zField
>Proto Et0 Et0 Export #zField
Et0 f0 outLink startExpressWfExport.ivp #txt
Et0 f0 inParamDecl '<String workflowID> param;' #txt
Et0 f0 inParamTable 'out.processID=param.workflowID;
' #txt
Et0 f0 requestEnabled true #txt
Et0 f0 triggerEnabled false #txt
Et0 f0 callSignature startExpressWfExport(String) #txt
Et0 f0 caseData businessCase.attach=true #txt
Et0 f0 showInStartList 0 #txt
Et0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>startExpressWfExport.ivp</name>
    </language>
</elementInfo>
' #txt
Et0 f0 @C|.responsibility Everybody #txt
Et0 f0 65 49 30 30 -42 21 #rect
Et0 f1 369 49 30 30 0 15 #rect
Et0 f4 actionTable 'out=in;
' #txt
Et0 f4 actionCode 'import ch.ivy.addon.portalkit.service.ExpressProcessService;
import ch.ivy.addon.portalkit.bo.ExpressProcess;
import ch.ivyteam.ivy.ExpressWorkflowExporter;

ExpressProcess workflow = ExpressProcessService.getInstance().findById(in.processID) as ExpressProcess;
ExpressWorkflowExporter.export(workflow);' #txt
Et0 f4 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>call ExpressWorkflowExporter</name>
    </language>
</elementInfo>
' #txt
Et0 f4 144 42 176 44 -81 -8 #rect
Et0 f2 320 64 369 64 #arcP
Et0 f6 95 64 144 64 #arcP
Et0 f6 0 0.49392648114603677 0 0 #arcLabel
>Proto Et0 .type gawfs.Data #txt
>Proto Et0 .processKind NORMAL #txt
>Proto Et0 0 0 32 24 18 0 #rect
>Proto Et0 @|BIcon #fIcon
Et0 f4 mainOut f2 tail #connect
Et0 f2 head f1 mainIn #connect
Et0 f0 mainOut f6 tail #connect
Et0 f6 head f4 mainIn #connect
