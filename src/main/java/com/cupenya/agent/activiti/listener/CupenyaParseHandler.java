package com.cupenya.agent.activiti.listener;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BusinessRuleTask;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.EventGateway;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.ScriptTask;
import org.activiti.bpmn.model.SendTask;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.parse.BpmnParseHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A BpmnParseHandler that registers for all events in a BPM execution of Activiti and
 * forwards the events to the CupenyaListener.
 */
public class CupenyaParseHandler implements BpmnParseHandler {
  private static final String START_EVENT = "start";
  private static final String END_EVENT = "end";

  private final CupenyaListener defaultListener;

  public CupenyaParseHandler() throws IOException {
    defaultListener = new CupenyaListener();
  }

  public Collection<Class<? extends BaseElement>> getHandledTypes() {
    Set<Class<? extends BaseElement>> classes = new HashSet<Class<? extends BaseElement>>();
    classes.add(EndEvent.class);
    classes.add(ThrowEvent.class);
    classes.add(BoundaryEvent.class);
    classes.add(IntermediateCatchEvent.class);
    classes.add(ExclusiveGateway.class);
    classes.add(InclusiveGateway.class);
    classes.add(ParallelGateway.class);
    classes.add(EventGateway.class);
    classes.add(Task.class);
    classes.add(ManualTask.class);
    classes.add(ReceiveTask.class);
    classes.add(ScriptTask.class);
    classes.add(ServiceTask.class);
    classes.add(BusinessRuleTask.class);
    classes.add(SendTask.class);
    classes.add(UserTask.class);
    classes.add(CallActivity.class);
    classes.add(SubProcess.class);
    return Collections.unmodifiableSet(classes);
  }

  public void parse(BpmnParse bpmnParse, BaseElement baseElement) {
    ActivityImpl activity = bpmnParse.getCurrentScope().findActivity(baseElement.getId());
    activity.addExecutionListener(START_EVENT, defaultListener);
    activity.addExecutionListener(END_EVENT, defaultListener);
  }
}
