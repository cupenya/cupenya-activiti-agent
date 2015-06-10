package com.cupenya.agent.activiti.listener;

import com.cupenya.agent.activiti.CupenyaConfig;
import com.cupenya.agent.common.model.v1.DefaultEvent;
import com.cupenya.agent.common.model.v1.EventType;
import com.cupenya.agent.common.sender.EventSender;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * The main listener, it converts the internal Activiti event to the Cupenya event API model.
 */
public class CupenyaListener implements ExecutionListener {

  private static final String EVENT_NAME_START = "start";
  private static final String EVENT_NAME_END = "end";
  private static final String EVENT_NAME_TRANSITION = "take";

  private final Logger log = LoggerFactory.getLogger(CupenyaListener.class);

  private final EventSender sender;

  public CupenyaListener() throws IOException {
    sender = new EventSender(CupenyaConfig.commonConfig);
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception {
    String eventId = execution.getCurrentActivityId();
    if (execution.getEventName().equals(EVENT_NAME_TRANSITION)) {
      eventId = ((ExecutionEntity) execution).getTransition().getId();
    }

    DefaultEvent event = new DefaultEvent();
    event.setId(UUID.randomUUID().toString());
    event.setEventId(eventId);
    if (execution.getEventName().equals(EVENT_NAME_START)) {
      event.setEventType(EventType.ELEMENT_BEGIN);
    } else if (execution.getEventName().equals(EVENT_NAME_END)) {
      event.setEventType(EventType.ELEMENT_END);
    } else if (execution.getEventName().equals(EVENT_NAME_TRANSITION)) {
      event.setEventType(EventType.TRANSITION);
    }
    event.setInstanceId(execution.getProcessInstanceId());
    event.setProcessId(execution.getProcessDefinitionId());
    event.setParentProcessId(execution.getParentId());
    // TODO. check, ClockUtil is not available anymore, how to get actual timestamp
    event.setTimestamp(System.currentTimeMillis());
    event.setProcessContext(new HashMap<String, String>());
    for (String varName : execution.getVariableNames()) {
      Object var = execution.getVariable(varName);
      String value = var == null ? "null" : var.toString();
      event.getProcessContext().put(varName, value);
    }
    if (log.isDebugEnabled()) {
      log.debug("Cupenya Event Trace: " + event);
    }
    sender.queueEvent(event);
  }
}
