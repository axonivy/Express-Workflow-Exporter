[Ivy]
17BF4161514D6D80 9.3.0 #module
>Proto >Proto Collection #zClass
PP0 PP Big #zClass
PP0 B #cInfo
PP0 #process
PP0 @AnnotationInP-0n ai ai #zField
PP0 @TextInP .type .type #zField
PP0 @TextInP .processKind .processKind #zField
PP0 @TextInP .xml .xml #zField
PP0 @TextInP .responsibility .responsibility #zField
PP0 @StartRequest f0 '' #zField
PP0 @UserTask f1 '' #zField
PP0 @TkArc f2 '' #zField
PP0 @UserTask f3 '' #zField
PP0 @TkArc f4 '' #zField
PP0 @EndTask f5 '' #zField
PP0 @PushWFArc f6 '' #zField
>Proto PP0 PP0 PP #zField
PP0 f0 outLink start_PP.ivp #txt
PP0 f0 requestEnabled true #txt
PP0 f0 startName PP #txt
PP0 f0 caseData 'case.name=PP
customFields.STRING.embedInFrame="False"
customFields.STRING.steps="first,Final Review"' #txt
PP0 f0 wfuser 1 #txt
PP0 f0 roleExceptionId '>> Ignore Exception' #txt
PP0 f0 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>startPP.ivp</name>
        <desc>PP</desc>
    </language>
</elementInfo>
' #txt
PP0 f0 @C|.responsibility Everybody #txt
PP0 f0 81 113 30 30 -29 17 #rect
PP0 f1 dialogId ch.ivy.gawfs.workflowExecution.ExportedUserTaskForm #txt
PP0 f1 startMethod start(gawfs.ExecutePredefinedWorkflowData) #txt
PP0 f1 requestActionDecl '<gawfs.ExecutePredefinedWorkflowData execdata> param;' #txt
PP0 f1 requestMappingAction 'param.execdata=in.executePredefinedWorkflowData;
' #txt
PP0 f1 responseMappingAction 'out.executePredefinedWorkflowData=result.execdata;
' #txt
PP0 f1 taskData 'TaskA.EXP=new Duration(0,0,1,0,0,0)
TaskA.NAM=first
TaskA.ROL="\#demo"
TaskA.SKIP_TASK_LIST=true
TaskA.TYPE=2
TaskA.customFields.NUMBER.stepindex=0
TaskA.customFields.STRING.dynaform="{''type''\:''USER_TASK'',''responsibles''\:[''\#demo''],''subject''\:''first'',''taskPosition''\:1,''untilDays''\:1,''formElements''\:[{''label''\:''Description'',''required''\:true,''intSetting''\:7,''elementType''\:''InputTextArea'',''optionStrs''\:[''''],''elementPosition''\:''HEADER'',''indexInPanel''\:0}]}"' #txt
PP0 f1 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>first</name>
    </language>
</elementInfo>
' #txt
PP0 f1 168 106 112 44 -10 -8 #rect
PP0 f2 111 128 168 128 #arcP
PP0 f3 dialogId ch.ivy.gawfs.workflowExecution.FinalReviewForm #txt
PP0 f3 startMethod startExported(gawfs.ExecutePredefinedWorkflowData) #txt
PP0 f3 requestActionDecl '<gawfs.ExecutePredefinedWorkflowData execdata> param;' #txt
PP0 f3 requestMappingAction 'param.execdata=in.executePredefinedWorkflowData;
' #txt
PP0 f3 responseMappingAction 'out=in;
' #txt
PP0 f3 taskData 'TaskA.NAM=PP\: Final Review
TaskA.ROL=CREATOR
TaskA.TYPE=0' #txt
PP0 f3 @C|.xml '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<elementInfo>
    <language>
        <name>Final Review</name>
        <desc>Exported AxonIvyExpress Workflow PP</desc>
    </language>
</elementInfo>
' #txt
PP0 f3 296 106 112 44 -35 -8 #rect
PP0 f4 280 128 296 128 #arcP
PP0 f5 465 113 30 30 0 15 #rect
PP0 f6 408 128 465 128 #arcP
>Proto PP0 .type axon.ivy.express.export.Data #txt
>Proto PP0 .processKind NORMAL #txt
>Proto PP0 0 0 32 24 18 0 #rect
>Proto PP0 @|BIcon #fIcon
PP0 f0 mainOut f2 tail #connect
PP0 f2 head f1 in #connect
PP0 f1 out f4 tail #connect
PP0 f4 head f3 in #connect
PP0 f3 out f6 tail #connect
PP0 f6 head f5 mainIn #connect
