package com.almende.eve.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.almende.eve.agent.Agent;
import com.almende.eve.agent.AgentFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResultMonitor implements Serializable {
	private static final long						serialVersionUID	= -6738643681425840533L;
	
	public String									id;
	public String									agentId;
	public String									url;
	public String									method;
	public ObjectNode								params;
	public String									callbackMethod;
	public List<String>								schedulerIds		= new ArrayList<String>();
	public List<String>								remoteIds			= new ArrayList<String>();
	public String									cacheType;
	
	transient private static HashMap<String, Cache>	caches				= new HashMap<String, Cache>();
	
	public <T> ResultMonitor(String agentId, String url, String method, ObjectNode params,
			String callbackMethod) {
		this.id = UUID.randomUUID().toString();
		this.agentId = agentId;
		this.url = url;
		this.method = method;
		this.params = params;
		this.callbackMethod = callbackMethod;
	}
	
	public boolean hasCache() {
		return cacheType != null;
	}
	
	public void addCache(Cache config) {
		cacheType = config.getClass().getName();
		synchronized (caches) {
			caches.put(id, config);
		}
	}
	
	public Cache getCache() {
		synchronized (caches) {
			return caches.get(id);
		}
	}
	
	public void addPoll(Poll config) {
		AgentFactory factory = AgentFactory.getInstance();
		
		try {
			Agent agent = factory.getAgent(agentId);
			String taskId = config.init(this, agent);
			schedulerIds.add(taskId);
		} catch (Exception e){
			System.err.println("Couldn't init polling!");
			e.printStackTrace();
		}
	}
	
	public void addPush(Push config) {
		AgentFactory factory = AgentFactory.getInstance();
		
		try {
			Agent agent = factory.getAgent(agentId);
			List<String> remoteIds = config.init(this, agent);
			remoteIds.addAll(remoteIds);
		} catch (Exception e){
			System.err.println("Couldn't init Pushing!");
			e.printStackTrace();
		}
	}
	
	public void store() {
		AgentFactory factory = AgentFactory.getInstance();
		
		try {
			Agent agent = factory.getAgent(agentId);
			@SuppressWarnings("unchecked")
			Map<String, ResultMonitor> monitors = (Map<String, ResultMonitor>) agent
					.getState().get("_monitors");
			Map<String, ResultMonitor> newmonitors = new HashMap<String, ResultMonitor>();
			if (monitors != null) {
				newmonitors.putAll(monitors);
			}
			newmonitors.put(id, this);
			
			if (!agent.getState().putIfUnchanged("_monitors",
					(Serializable) newmonitors, (Serializable) monitors)) {
				store(); // recursive retry.
			}
		} catch (Exception e) {
			System.err.println("Couldn't find monitors:" + agentId + "." + id);
		}
	}
	
	public void delete(){
		AgentFactory factory = AgentFactory.getInstance();
		
		try {
			Agent agent = factory.getAgent(agentId);
			@SuppressWarnings("unchecked")
			Map<String, ResultMonitor> monitors = (Map<String, ResultMonitor>) agent
					.getState().get("_monitors");
			Map<String, ResultMonitor> newmonitors = new HashMap<String, ResultMonitor>();
			if (monitors != null) {
				newmonitors.putAll(monitors);
			}
			newmonitors.remove(id);
			
			if (!agent.getState().putIfUnchanged("_monitors",
					(Serializable) newmonitors, (Serializable) monitors)) {
				delete(); // recursive retry.
			}
		} catch (Exception e) {
			System.err.println("Couldn't delete monitor:" + agentId + "." + id);
			e.printStackTrace();
		}
	}
	
	public static ResultMonitor getMonitorById(String agentId, String id) {
		AgentFactory factory = AgentFactory.getInstance();
		
		try {
			Agent agent = factory.getAgent(agentId);
			@SuppressWarnings("unchecked")
			Map<String, ResultMonitor> monitors = (Map<String, ResultMonitor>) agent
					.getState().get("_monitors");
			if (monitors == null) {
				monitors = new HashMap<String, ResultMonitor>();
			}
			ResultMonitor result = monitors.get(id);
			if (result != null && !caches.containsKey(id)
					&& result.cacheType != null) result.addCache((Cache) Class
					.forName(result.cacheType).newInstance());
			return result;
			
		} catch (Exception e) {
			System.err.println("Couldn't find monitor:" + agentId + "." + id);
		}
		return null;
	}
	
}